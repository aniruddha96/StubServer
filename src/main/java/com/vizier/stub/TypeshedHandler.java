package com.vizier.stub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TypeshedHandler {

	static List<String> cachedList;
	
	static List<String> getTypeshedList(){
		if(cachedList==null) {
			createList();
		}
		return cachedList;
	}
	
	static void createList() {
		cachedList = new ArrayList<String>();
		URL url;
		HttpURLConnection conn = null;
		BufferedReader in = null ;
		try {
			url = new URL(
					"https://raw.githubusercontent.com/typeshed-internal/stub_uploader/main/data/uploaded_packages.txt");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int status = conn.getResponseCode();
			if (status == 200) {
				in= new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					cachedList.add(inputLine.replace("types-", "").toLowerCase());
				}	
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				in.close();
				conn.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
}
