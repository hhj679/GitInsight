package com.gitinsight.github.api;

import java.io.File;
import java.io.IOException;

import com.gitinsight.util.HtmlUtil;

import net.sf.json.JSONObject;

public class RequestProjects {

	public RequestProjects() {
		// TODO Auto-generated constructor stub
	}
	
	public static String API_ACCESS_TOKEN = "access_token=26eb310a1188c55d78ddd46e89886390ba98ae48";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String languages[] = {"Java", "JavaScript", "CSS", "HTML", "Objective-C", "PHP", "Python", "Ruby", 
				"Scala", "Go", "R", "Swift", "C", "C++", "C#"};
		for(String language : languages) {
			try {
				File saveFile = new File("D:\\gitinsight\\github\\data\\projects\\json\\" + language);
				if(!saveFile.exists()) {
					saveFile.mkdirs();
				}
				
				int fileNo = 2;
				
				String repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A%3E100+language%3A" 
						+ language + "&sort=stars&order=desc&per_page=100&" + API_ACCESS_TOKEN, 
					"D:\\gitinsight\\github\\data\\projects\\json\\" + language + "\\" + language + "_1" + ".json");
				JSONObject jsonObj = JSONObject.fromObject(repoJson);
				int total = jsonObj.getInt("total_count");
				int totalPage = total/100 + 1;
				if(totalPage > 10) {
					totalPage = 10;
				}
				
				int stars = 100;
				
				for(int page = 2; page <= totalPage; page++) {
					repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A%3E100+language%3A" 
							+ language + "&sort=stars&order=desc&per_page=100&page=" + page + "&" + API_ACCESS_TOKEN, 
							"D:\\gitinsight\\github\\data\\projects\\json\\" + language + "\\" + language + "_" + (fileNo++) + ".json");
					
					if(page == 10) {
						jsonObj = JSONObject.fromObject(repoJson);
						
						JSONObject lastItem = (JSONObject) jsonObj.getJSONArray("items").get(99);
						stars = lastItem.getInt("stargazers_count");
					}
					
					Thread.sleep(1400);
				}
				while(totalPage == 10) {
					repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A<%3D" + stars + "+language%3A" 
							+ language + "&sort=stars&order=desc&per_page=100&" + API_ACCESS_TOKEN, 
						"D:\\gitinsight\\github\\data\\projects\\json\\" + language + "\\" + language + (fileNo++) + ".json");
					jsonObj = JSONObject.fromObject(repoJson);
					total = jsonObj.getInt("total_count");
					totalPage = total/100 + 1;
					if(totalPage > 10) {
						totalPage = 10;
					}
					
					for(int page = 2; page <= totalPage; page++) {
						repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A<%3D" + stars + "+language%3A" 
								+ language + "&sort=stars&order=desc&per_page=100&page=" + page + "&" + API_ACCESS_TOKEN, 
								"D:\\gitinsight\\github\\data\\projects\\json\\" + language + "\\" + language + "_" + (fileNo++) + ".json");
						
						if(page == 10) {
							jsonObj = JSONObject.fromObject(repoJson);
							
							JSONObject lastItem = (JSONObject) jsonObj.getJSONArray("items").get(99);
							stars = lastItem.getInt("stargazers_count");
						}
						
						Thread.sleep(1400);
					}
					
					if(stars < 100) {
						break;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
