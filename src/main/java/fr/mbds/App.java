package fr.mbds;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import org.apache.http.HttpHost;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;


public class App {

	private static PDFFilter filter = new PDFFilter();

	private static RestHighLevelClient client = null;
    private static File once = null;
	private static String onceText = null;

	static {
		HttpHost h1 = new HttpHost("localhost", 9200, "http");
		HttpHost h2 = new HttpHost("localhost", 9201, "http");
		RestClientBuilder clientBuilder = RestClient.builder(h1, h2);
		client = new RestHighLevelClient(clientBuilder);
	}

	public static void indexDoc(BulkRequest br, File file, String text) {
		IndexRequest req = new IndexRequest("cool","doc");
		Map<String, Object> data = new HashMap<>();
		data.put("filename",file.getName());
		data.put("content", text);
		req.source(data);
		br.add(req);

	}

	public static void printBulkResponseDetails(BulkResponse bulkResponse) {
		BulkItemResponse[] bulkItemResponses = bulkResponse.getItems();
		System.out.println(Arrays.toString(bulkItemResponses));
		for(BulkItemResponse bir : bulkItemResponses) {
			BulkItemResponse.Failure failure = bir.getFailure();
			if(failure != null) {
				System.err.println(failure.getMessage());
			} else {
				DocWriteResponse dwr = bir.getResponse();;
				DocWriteRequest.OpType opType = bir.getOpType();
				switch(opType) {
					case CREATE:
					case INDEX: {
						IndexResponse indexResponse = (IndexResponse)dwr;
						// System.out.println(indexResponse);
						break;
					}
					case UPDATE: {
						UpdateResponse updateResponse = (UpdateResponse)dwr;
						System.out.println(updateResponse);
						break;
					}
					case DELETE: {
						DeleteResponse deleteResponse = (DeleteResponse)dwr;
						System.out.println(deleteResponse);
						break;
					}
				}
			}			
		}
	}

	public static void searchFor(String searchTerm)  throws IOException {

		HighlightBuilder highlightBuilder = new HighlightBuilder();
		HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("content");
		highlightContent.highlighterType("unified");
		highlightBuilder.field(highlightContent);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("content", searchTerm));
		searchSourceBuilder.highlighter(highlightBuilder);
		searchSourceBuilder.fetchSource(new String[0], new String[]{"content"});

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		HTMLWriter hw = new HTMLWriter();
		hw.writeSearchResponse(searchTerm, searchResponse);
		writeResultsToFile(searchResponse, "./result.txt");
	}

	public static void writeResultsToFile(SearchResponse searchResponse, String filename) throws IOException {
		PrintWriter pw = new PrintWriter(new File(filename), StandardCharsets.UTF_8.name());
		SearchHits hits = searchResponse.getHits();
		pw.println("There are " + hits.getTotalHits() + ", in " + searchResponse.getTook());
		pw.println(searchResponse);
		pw.close();
	}

	private static void launchDirectoryChooserFrame() throws IOException {
		new DirectoryChooserFrame();
	}

	public static void indexPath(Path startPath) throws IOException {
    	final BulkRequest bulkRequest = new BulkRequest();
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

        	@Override
        	public FileVisitResult visitFile(Path filePath, BasicFileAttributes bfa) throws IOException{
        		File file = filePath.toFile();
        		if(filter.accept(file)) {
        			try {
	        			PDDocument doc = PDDocument.load(file);

	        			if(!doc.isEncrypted()) {
	        				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
	        				stripper.setSortByPosition(true);

	        				PDFTextStripper tStripper = new PDFTextStripper();

	        				String text = tStripper.getText(doc);
	        				System.out.println("=====");
	        				System.out.println(file.getAbsolutePath());
	        				
							indexDoc(bulkRequest, file, text);

	        			}
	        			doc.close();
	        		} catch(IOException ioe) {
	        			ioe.printStackTrace();
	        		}
        		}
        		return FileVisitResult.CONTINUE;
        	}

        });
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        printBulkResponseDetails(bulkResponse);
	}

    public static void main( String[] args ) {
    	ExecutionMode mode = ExecutionMode.BOTH;
    	if(args.length > 0) { 
    		mode = ExecutionMode.valueOf(args[0]);
    	}
    	try {
    		if(mode != ExecutionMode.SEARCH) {
	    		Path startPath = FileSystems.getDefault().getPath("..","gros_backup");
	    		launchDirectoryChooserFrame();
	    	}
	    	if(mode != ExecutionMode.INDEX) {
	        	searchFor(AskUser.askSearchTerm());
	    	}
	        client.close();
	    } catch(IOException ioe) {
	    	ioe.printStackTrace();
	    }
    }
}
