package com.gitinsight.github.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.gitinsight.util.DBUtil;
import com.gitinsight.util.HtmlUtil;

public class RequestUserFollowers {
	public static Logger LOG = Logger.getLogger(RequestUserFollowers.class);
	
	private static ConcurrentLinkedQueue<String> USERS_QUEUE = new ConcurrentLinkedQueue<String>();
	private static ConcurrentLinkedQueue<String> TOKEN_QUEUE = new ConcurrentLinkedQueue<String>();
//	private static ConcurrentLinkedQueue<String> REPOS_QUEUE = new ConcurrentLinkedQueue<String>();
	
	public RequestUserFollowers() {
		// TODO Auto-generated constructor stub
	}
	
	public static String API_ACCESS_TOKEN[] = {"26eb310a1188c55d78ddd46e89886390ba98ae48", "7eb2ed4e458de4ad9c1e02e1e6c01fbfe143c801"};

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String languages[] = {"Java", "JavaScript", "CSS", "HTML", "Objective-C", "PHP", "Python", "Ruby", 
//				"Scala", "Go", "R", "Swift", "C", "C++", "C#"};
		
		String sql = "SELECT count(p.user_login) counts FROM insightdb.project_stars p";
		String[] reColsName = {"counts"};
		List<Map<String, Object>> rsList = DBUtil.getTableData(sql, null, reColsName);
		long count = (long) rsList.get(0).get("counts");
		
		long pageSize = 50000;
		int pages = (int) (count/pageSize + 1);
		String[] reColsName1 = {"user_login"};
		
		Set<String> set = new HashSet<String>();
		
		for(int i=1; i<=pages; i++) {
			LOG.info("page:" + i);
			long lastPage = ((i-1)*pageSize + 1);
//			if(i == pages){
//				lastPage = count;
//			}
			sql = "select user_login from insightdb.project_stars where id <= (select id from insightdb.project_stars order by id desc LIMIT " 
					+ lastPage + ",1) order by id desc LIMIT " + pageSize + ";";
			List<Map<String, Object>> rsList1 = null;
			try{
				rsList1 = DBUtil.getTableData(sql, null, reColsName1);
			} catch(Exception e){
				e.printStackTrace();
			}
			if(rsList1!=null){
				for(Map<String, Object> rsMap : rsList1){
//					USERS_QUEUE.offer((String) rsMap.get("user_login"));
					set.add((String) rsMap.get("user_login"));
				}
				FileUtils.writeLines(new File("E:\\gitinsight\\data\\users\\users_" + i + ".txt"), rsList1);
				rsList1 = null;
			}
			
		}
		USERS_QUEUE.addAll(set);
//		set = null;
		
//		List<String> reposList = new ArrayList<String>();
//		LOG.info("request size:" + rsList.size());
//		
//		for(Map<String, Object> rsMap : rsList){
//			reposList.add((String) rsMap.get("full_name"));
//			
//			USERS_QUEUE.offer((String) rsMap.get("full_name"));
//		}
		
		LOG.info("Queue size:" + USERS_QUEUE.size());
		
		TOKEN_QUEUE.offer("abc57c2a8ac4ede2fb1551dcf699257fa6dad13c");
		TOKEN_QUEUE.offer("04bb9a7506ab600ce53e5f004c2d35bb7f41dfd0");
		TOKEN_QUEUE.offer("f81ef6ec593ca3cea7b2cbddc72cc1d03f47e535");
		
		TOKEN_QUEUE.offer("6d12e500f661490fbe978100dfbd7ede32648f14");
		TOKEN_QUEUE.offer("cf8ee7d276e027ffe96808403881c6ceb5e7d104");
		TOKEN_QUEUE.offer("1975c6cc602fa5e422a4eeea739a96538c0f4725");
		
		TOKEN_QUEUE.offer("00737e62d82bf610f0b515189d43e09b8282ef47");
		TOKEN_QUEUE.offer("ede65a8a32b24acbb4d134f25c44c8c5a0a5cf4c");
		TOKEN_QUEUE.offer("b41e59078a547a53fd12eb1d3ff377218364101a");
		
		TOKEN_QUEUE.offer("b50b4fabe5930370a726a8a8b5b3bd17ca2de368");
		
//		reposList.add("0x00A/paramify");
//		reposList.add("0x00A/Porter");
		
//		request(reposList);
		
		for(int i=0; i< 10 ; i ++) {
			new CrawlThread().start();
		}
	}
	
	public static void request() {
		String savePath = "E:\\gitinsight\\data\\followers\\";
		String[] tokens = {TOKEN_QUEUE.poll()};
		while(!USERS_QUEUE.isEmpty()) {
			try {
				Thread.sleep(800);
				String user_login = USERS_QUEUE.poll();
				
				File saveFile = new File(savePath + user_login);
				if(!saveFile.exists()) {
					saveFile.mkdirs();
				}
				
				int fileNo = 2;
				
				String [] reInfo = HtmlUtil.requestPageByGetReLink("https://api.github.com/users/" + user_login + "/followers?page=1&per_page=100&access_token=" + getToken(tokens), 
						savePath + user_login + File.separator + user_login + "_followers_1" + ".json");
				
				String link = reInfo[1];
				
				int lastPage = 1;
				
				if(reInfo!=null && reInfo[1] != null) {
					lastPage = Integer.valueOf(getLastPageByLink(link));
				}
				
				FileUtils.writeStringToFile(new File(savePath + user_login + File.separator + "pages.txt"), String.valueOf(lastPage));

				for(int i=2; i<=lastPage; i++){
					HtmlUtil.requestPageByGet("https://api.github.com/users/" + user_login + "/followers?page=" + i +"&per_page=100&access_token=" + getToken(tokens), 
							savePath + user_login + File.separator + user_login+ "_followers_" + (fileNo++) + ".json");
					Thread.sleep(800);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("Exception", e);
			}
		}
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
				
				int lastPage = 1;
				
				if(reInfo!=null && reInfo[1] != null) {
					lastPage = Integer.valueOf(getLastPageByLink(link));
				}

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
	
	public static String getToken(String[] tokens){
		int i = (int)(1+Math.random()*(tokens.length-1+1)) - 1;
		
		return tokens[i];
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
	
	static class CrawlThread extends Thread {
		   // 第二个线程入口
		   public void run() {
			   RequestUserFollowers.request();
		   }
		}
}
