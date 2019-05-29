package fr.mbds;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexResponse;

public class IndexActionListener implements ActionListener<IndexResponse> {

	public void onResponse(IndexResponse resp) {
		System.out.println(resp);
	}

	public void onFailure(Exception e) {
		e.printStackTrace();
	}
}