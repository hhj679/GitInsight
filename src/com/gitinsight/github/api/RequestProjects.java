package com.gitinsight.github.api;

import java.io.File;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

import com.gitinsight.util.HtmlUtil;

public class RequestProjects {

	public RequestProjects() {
		// TODO Auto-generated constructor stub
	}
	public static Logger LOG = Logger.getLogger(RequestProjects.class);
	public static String API_ACCESS_TOKEN[] = {"26eb310a1188c55d78ddd46e89886390ba98ae48", "7eb2ed4e458de4ad9c1e02e1e6c01fbfe143c801"};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String languages[] = {"Java", "JavaScript", "CSS", "HTML", "Objective-C", "PHP", "Python", "Ruby", 
				"Scala", "Go", "R", "Swift", "C", "C++", "C#"};
		
		String savePath = "E:\\opensource\\github\\data\\projects\\";
		
		for(String language : languages) {
			try {
				File saveFile = new File(savePath + language);
				if(!saveFile.exists()) {
					saveFile.mkdirs();
				}
				
				int fileNo = 2;
				
				String repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A%3E100+language%3A" 
						+ language + "&sort=stars&order=desc&per_page=100&access_token=" + getToken(), 
					savePath + language + "\\" + language + "_1" + ".json");
				JSONObject jsonObj = JSONObject.fromObject(repoJson);
				int total = jsonObj.getInt("total_count");
				int totalPage = total/100 + 1;
				if(totalPage > 10) {
					totalPage = 10;
				}
				
				int stars = 100;
				
				for(int page = 2; page <= totalPage; page++) {
					repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A%3E100+language%3A" 
							+ language + "&sort=stars&order=desc&per_page=100&page=" + page + "&access_token=" + getToken(), 
							savePath + language + "\\" + language + "_" + (fileNo++) + ".json");
					
					if(page == 10) {
						jsonObj = JSONObject.fromObject(repoJson);
						
						JSONObject lastItem = (JSONObject) jsonObj.getJSONArray("items").get(jsonObj.getJSONArray("items").size()-1);
						stars = lastItem.getInt("stargazers_count");
					}
					
					Thread.sleep(1400);
				}
				while(totalPage == 10) {
					repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A<%3D" + stars + "+language%3A" 
							+ language + "&sort=stars&order=desc&per_page=100&access_token=" + getToken(), 
						savePath + language + "\\" + language + (fileNo++) + ".json");
					jsonObj = JSONObject.fromObject(repoJson);
					total = jsonObj.getInt("total_count");
					totalPage = total/100 + 1;
					if(totalPage > 10) {
						totalPage = 10;
					}
					
					for(int page = 2; page <= totalPage; page++) {
						repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A<%3D" + stars + "+language%3A" 
								+ language + "&sort=stars&order=desc&per_page=100&page=" + page + "&access_token=" + getToken(), 
								savePath + language + "\\" + language + "_" + (fileNo++) + ".json");
						
						if(page == 10) {
							jsonObj = JSONObject.fromObject(repoJson);
							
							JSONObject lastItem = (JSONObject) jsonObj.getJSONArray("items").get(jsonObj.getJSONArray("items").size());
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
				LOG.error("Exception",e);
			}
		}
	}
	
	public static String getToken(){
		int i = (int)(1+Math.random()*(2-1+1)) - 1;
		
		return API_ACCESS_TOKEN[i];
	}
}
