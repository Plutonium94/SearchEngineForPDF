package fr.mbds;

import org.elasticsearch.action.search.SearchResponse;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;

public class HTMLWriter {


	private String fileName;
	private String templateHtml = "";
	private Path templatePath;

	private PrintWriter resultWriter;

	public HTMLWriter() throws IOException {
		this("result.html");
	}

	public HTMLWriter(String fileName) throws IOException { 
		this.fileName = fileName;
		templatePath = FileSystems.getDefault().getPath("src","main","resources","template.html");
		resultWriter = new PrintWriter(fileName, StandardCharsets.UTF_8.name());
	}

	private void beginHtmlTemplate() {
		try {
			BufferedReader br = Files.newBufferedReader(templatePath, StandardCharsets.UTF_8);
			for(String line = br.readLine(); line != null; line = br.readLine()) {
				templateHtml += line;
			}
			br.close();
			int bodyTagIndex = templateHtml.indexOf("<body>");
			String beforeBody = templateHtml.substring(0, bodyTagIndex + "<body>".length());
			resultWriter.println(beforeBody);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void writeSearchResponse(String searchTerm, SearchResponse searchResponse) {
		beginHtmlTemplate();
		SearchResponseDetails searchResponseDetails = new SearchResponseDetails(searchTerm, searchResponse);
		resultWriter.printf("<h1>%d search results for : %s</h1>",
			searchResponseDetails.getNumResults(), searchTerm);
		resultWriter.println("<table><thead><th>fileName</th><th>score</th><th>fragement</th></thead>");
		for(SingleResultDetail snrd : searchResponseDetails.getResultDetails() ) {
			resultWriter.printf("<tr><td>%s</td><td>%f</td><td>%s</td></tr>",
				snrd.getFilename(), snrd.getScore(),
				Arrays.toString(snrd.getContentFragments()));
		}
		finishHtmlTemplate();


	}

	private void finishHtmlTemplate() {
		resultWriter.println("</body></html>");
		resultWriter.close();
	}



}