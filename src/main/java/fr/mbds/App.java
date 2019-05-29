package fr.mbds;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.ActionListener;

import org.apache.http.HttpHost;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;


public class App {

	private static PDFFilter filter = new PDFFilter();

	private static RestHighLevelClient client = null;
    private static  File once = null;
	private static String onceText = null;

	static {
		HttpHost h1 = new HttpHost("localhost", 9200, "http");
		HttpHost h2 = new HttpHost("localhost", 9201, "http");
		RestClientBuilder clientBuilder = RestClient.builder(h1, h2);
		client = new RestHighLevelClient(clientBuilder);
	}

	public static void indexDoc(File file, String text) {
		IndexRequest req = new IndexRequest("cool","doc");
		Map<String, Object> data = new HashMap<>();
		data.put("filename",file.getName());
		data.put("content", text);
		req.source(data);
		client.indexAsync(req, RequestOptions.DEFAULT, new IndexActionListener());		

	}

    public static void main( String[] args ) {
    	try {
	    	Path startPath = FileSystems.getDefault().getPath("D:","attestations_stage");
	        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

	        	@Override
	        	public FileVisitResult visitFile(Path filePath, BasicFileAttributes bfa) throws IOException{
	        		File file = filePath.toFile();
	        		if(filter.accept(file)) {
	        			PDDocument doc = PDDocument.load(file);

	        			if(!doc.isEncrypted()) {
	        				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
	        				stripper.setSortByPosition(true);

	        				PDFTextStripper tStripper = new PDFTextStripper();

	        				String text = tStripper.getText(doc);
	        				System.out.println("=====");
	        				System.out.println(text);
	        				
							indexDoc(file, text);

	        			}
	        			doc.close();
	        		}
	        		return FileVisitResult.CONTINUE;
	        	}

	        });
	        // client.close();
	    } catch(IOException ioe) {
	    	ioe.printStackTrace();
	    }
    }
}
