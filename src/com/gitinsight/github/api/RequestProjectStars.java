package com.gitinsight.github.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gitinsight.util.DBUtil;
import com.gitinsight.util.HtmlUtil;

public class RequestProjectStars {
	public static Logger LOG = Logger.getLogger(RequestProjectStars.class);
	
	public RequestProjectStars() {
		// TODO Auto-generated constructor stub
	}
	
	public static String API_ACCESS_TOKEN[] = {"26eb310a1188c55d78ddd46e89886390ba98ae48", "7eb2ed4e458de4ad9c1e02e1e6c01fbfe143c801"};

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String languages[] = {"Java", "JavaScript", "CSS", "HTML", "Objective-C", "PHP", "Python", "Ruby", 
//				"Scala", "Go", "R", "Swift", "C", "C++", "C#"};
		
		String sql = "SELECT distinct p.full_name FROM insightdb.git_projects p";
		String[] reColsName = {"full_name"};
		List<Map<String, Object>> rsList = DBUtil.getTableData(sql, null, reColsName);
		List<String> reposList = new ArrayList<String>();
		LOG.info("request size:" + rsList.size());
		
		for(Map<String, Object> rsMap : rsList){
			reposList.add((String) rsMap.get("full_name"));
		}
		
//		reposList.add("0x00A/paramify");
//		reposList.add("0x00A/Porter");
		
		request(reposList);
	}
	
	public static void request(List<String> reposList) {
		String savePath = "E:\\opensource\\github\\data\\stars\\";
		for(String repo : reposList) {
			try {
				String saveFilePath = repo.replace("/", "_qqq;;;_");
				File saveFile = new File(savePath + saveFilePath);
				if(!saveFile.exists()) {
					saveFile.mkdirs();
				}
				
				int fileNo = 2;
				
				String [] reInfo = HtmlUtil.requestPageByGetReLink("https://api.github.com/repos/" + repo + "/stargazers?page=1&per_page=100&access_token=" + getToken(), 
						savePath + saveFilePath + "\\" + saveFilePath + "stars_1" + ".json");
				
				String link = reInfo[1];
				int lastPage = Integer.valueOf(getLastPageByLink(link));

				for(int i=2; i<=lastPage; i++){
					HtmlUtil.requestPageByGet("https://api.github.com/repos/" + repo + "/stargazers?page=" + i +"&per_page=100&access_token=" + getToken(), 
							savePath + saveFilePath + "\\" + saveFilePath + "stars_" + (fileNo++) + ".json");
					Thread.sleep(1000);
				}

//				String repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A%3E100+language%3A" 
//						+ repo + "&sort=stars&order=desc&per_page=100&" + getToken(), 
//					savePath + saveFilePath + "\\" + saveFilePath + "_1" + ".json");
//				JSONObject jsonObj = JSONObject.fromObject(repoJson);
//				int total = jsonObj.getInt("total_count");
//				int totalPage = total/100 + 1;
//				if(totalPage > 10) {
//					totalPage = 10;
//				}
//				
//				int stars = 100;
//				
//				for(int page = 2; page <= totalPage; page++) {
//					repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A%3E100+language%3A" 
//							+ repo + "&sort=stars&order=desc&per_page=100&page=" + page + "&" + getToken(), 
//							savePath + repo + "\\" + repo + "_" + (fileNo++) + ".json");
//					
//					if(page == 10) {
//						jsonObj = JSONObject.fromObject(repoJson);
//						
//						JSONObject lastItem = (JSONObject) jsonObj.getJSONArray("items").get(99);
//						stars = lastItem.getInt("stargazers_count");
//					}
//					
//					Thread.sleep(1400);
//				}
//				while(totalPage == 10) {
//					repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A<%3D" + stars + "+language%3A" 
//							+ repo + "&sort=stars&order=desc&per_page=100&" + getToken(), 
//						savePath + saveFilePath + "\\" + saveFilePath + (fileNo++) + ".json");
//					jsonObj = JSONObject.fromObject(repoJson);
//					total = jsonObj.getInt("total_count");
//					totalPage = total/100 + 1;
//					if(totalPage > 10) {
//						totalPage = 10;
//					}
//					
//					for(int page = 2; page <= totalPage; page++) {
//						repoJson = HtmlUtil.requestPageByGet("https://api.github.com/search/repositories?q=stars%3A<%3D" + stars + "+language%3A" 
//								+ repo + "&sort=stars&order=desc&per_page=100&page=" + page + "&" + getToken(), 
//								savePath + saveFilePath + "\\" + saveFilePath + "_" + (fileNo++) + ".json");
//						
//						if(page == 10) {
//							jsonObj = JSONObject.fromObject(repoJson);
//							
//							JSONObject lastItem = (JSONObject) jsonObj.getJSONArray("items").get(99);
//							stars = lastItem.getInt("stargazers_count");
//						}
//						
//						Thread.sleep(1400);
//					}
//					
//					if(stars < 100) {
//						break;
//					}
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("Exception", e);
			}
		}
	}
	
	public static String getToken(){
		int i = (int)(1+Math.random()*(2-1+1)) - 1;
		
		return API_ACCESS_TOKEN[i];
	}
	
	public static String getLastPageByLink(String link) {
		if(link == null || link.length()==0) {
			return "1";
		}
		
		String links[] = link.split(",");
		String last = links[1];
		last = last.substring(last.indexOf("&page=") + 6);
		last = last.substring(last.indexOf("?page=") + 6);
		String lastPage = last.substring(0, last.indexOf("&"));
		
		return lastPage;
	}
}
