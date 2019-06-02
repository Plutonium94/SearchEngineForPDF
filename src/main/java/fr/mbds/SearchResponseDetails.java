package fr.mbds;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.text.Text;

import java.util.*;

public class SearchResponseDetails {

	private long numResults;
	private String searchTerm;
	private List<SingleResultDetail> resultDetails = new ArrayList<>();

	public SearchResponseDetails(String searchTerm, SearchResponse searchResponse) {
		SearchHits hits = searchResponse.getHits();
		this.numResults = hits.getTotalHits();
		for(SearchHit sh : hits.getHits()) {
			float score = sh.getScore();
			System.out.println(sh.getSourceAsMap().get("filename"));
			String filename = (String)sh.getSourceAsMap().get("filename");
			Text[] contentFragmentTexts = sh.getHighlightFields().get("content").getFragments();
			String[] contentFragments = new String[contentFragmentTexts.length];
			for(int i = 0; i < contentFragmentTexts.length; i++) {
				contentFragments[i] = contentFragmentTexts[i].string();
			}
			SingleResultDetail srd = new SingleResultDetail(score, filename, contentFragments);
			resultDetails.add(srd);
		}
	}

	public long getNumResults() {
		return numResults;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public List<SingleResultDetail> getResultDetails() {
		return resultDetails;
	}

	public String toString() {
		return "SearchResponseDetails[searchTerm: " + searchTerm
			+ ", numResults: " + numResults
			+ ", resultDetails: " + resultDetails;
	}


}