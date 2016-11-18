package com.gitinsight.github.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.gitinsight.util.DBUtil;
import com.gitinsight.util.HtmlUtil;

public class RequestProjectIssues {
	public static Logger LOG = Logger.getLogger(RequestProjectIssues.class);
	
	private static ConcurrentLinkedQueue<String> REPOS_QUEUE = new ConcurrentLinkedQueue<String>();
	private static ConcurrentLinkedQueue<String> TOKEN_QUEUE = new ConcurrentLinkedQueue<String>();
//	private static ConcurrentLinkedQueue<String> REPOS_QUEUE = new ConcurrentLinkedQueue<String>();
	
	public RequestProjectIssues() {
		// TODO Auto-generated constructor stub
	}
	
	public static String API_ACCESS_TOKEN[] = {"26eb310a1188c55d78ddd46e89886390ba98ae48", "7eb2ed4e458de4ad9c1e02e1e6c01fbfe143c801"};

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
//		String sql = "SELECT distinct p.full_name FROM insightdb.git_projects p";
//		String[] reColsName = {"full_name"};
//		List<Map<String, Object>> rsList = DBUtil.getTableData(sql, null, reColsName);
//		for(Map<String, Object> rsMap : rsList){
//			REPOS_QUEUE.offer((String) rsMap.get("full_name"));
//		}
		
		REPOS_QUEUE.addAll(FileUtils.readLines(new File("E:\\gitinsight\\data\\issues_error.txt")));
		
		LOG.info("Queue size:" + REPOS_QUEUE.size());
		
		
		//thx021
//		TOKEN_QUEUE.offer("04bb9a7506ab600ce53e5f004c2d35bb7f41dfd0");
//		TOKEN_QUEUE.offer("f9643d19c67b58e5d5a7e41b9cac64279026a378");
//		TOKEN_QUEUE.offer("eb866b4f06bbf84fbbd047a978442564d829f3b3");
//		TOKEN_QUEUE.offer("123ccc118eb3dabe80b5b5c087a572d063a3631f");
//		TOKEN_QUEUE.offer("40f99141c73394888e36fc9278754a1786ea7e5e");
//		TOKEN_QUEUE.offer("f4779b386cf87e0f14846f3274429238cfba6780");
//		TOKEN_QUEUE.offer("a2dad91ea6f17619ebf54cc769b7c674492a28d0");
//		TOKEN_QUEUE.offer("94986b525450dd61dbc32d65786d4eaca5631dc3");
//		TOKEN_QUEUE.offer("5ac6ac3937132ff2d42e1775c0fabd77548beceb");
//		TOKEN_QUEUE.offer("77830f0cd45e277add570ac200ff6ddde55cf2c2");
		
		//hhj679
//		TOKEN_QUEUE.offer("abc57c2a8ac4ede2fb1551dcf699257fa6dad13c");
//		TOKEN_QUEUE.offer("bf5d4c34c678a61b49765201be1b20b9e1771c85");
//		TOKEN_QUEUE.offer("ab41fb6c942eb2bf9b656a5489f45be46d8310c1");
//		TOKEN_QUEUE.offer("856c840c7cd6384b9f5a472b998b12e973e485c0");
//		TOKEN_QUEUE.offer("9f47cb99cd0d76e606f2971a053b01ffa25ebbf1");
//		TOKEN_QUEUE.offer("35a677c768577aa0de897fa96f1c0c22467c8133");
//		TOKEN_QUEUE.offer("9e507f59f9099eec8ed2bdb6aef83f8d1465bff8");
//		TOKEN_QUEUE.offer("b7610495b3efe8686bf06fe13478935fe16259b8");
//		TOKEN_QUEUE.offer("30adf01002b46824a9015b0c96be751c52b7ee42");
//		TOKEN_QUEUE.offer("e8b088384362fc43cd67da009f17111dc793f3ec");
		
		
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
		
		for(int i=0; i< 1 ; i ++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new CrawlThread("Thread_" + i).start();
		}
	}
	
	public static void request() {
		String savePath = "E:\\gitinsight\\data\\issues\\";
		//String[] tokens = {TOKEN_QUEUE.poll(), TOKEN_QUEUE.poll()};
		String token = TOKEN_QUEUE.poll();
		int tempNum = 0;
		while(!REPOS_QUEUE.isEmpty()) {
			try {
				Thread.sleep(800);
				String repo = REPOS_QUEUE.poll();
				
				String saveFilePath = repo.replace("/", "_qqq;;;_");
				File saveFile = new File(savePath + saveFilePath);
				if(!saveFile.exists()) {
					saveFile.mkdirs();
				} 
//				else {
//					continue;
//				}
				
				tempNum ++;
				
				if(tempNum%100 == 0 && tempNum != 0) {
					LOG.info("I still live! Have a rest!");
					Thread.sleep(30*1000);
				}
				
				int fileNo = 2;
				
				String [] reInfo = HtmlUtil.requestPageByGetReLink("https://api.github.com/repos/" + repo + "/issues/events?page=1&per_page=100&access_token=" + token,//getToken(tokens), 
						savePath + saveFilePath + "\\" + saveFilePath + "issues_1" + ".json");
				
				
				int lastPage = 1;
				
				if(reInfo!=null && reInfo[1] != null) {
					String link = reInfo[1];
					lastPage = Integer.valueOf(getLastPageByLink(link));
					
					FileUtils.writeStringToFile(new File(savePath + saveFilePath + "\\pages.txt"), String.valueOf(lastPage));
					
				} else {
					FileUtils.writeStringToFile(new File(savePath + saveFilePath + "\\pages.txt"), "1");
				}
				
				File checkFile = new File(savePath + saveFilePath);
				if(checkFile.list().length == 1) {
					checkFile.delete();
					REPOS_QUEUE.add(repo);
					LOG.error("connect 3 tims still fail. The url is:" + "https://api.github.com/repos/" + repo + "/issues/events?page=1&per_page=100&access_token=" + token);//getToken(tokens));
				}
				

				for(int i=2; i<=lastPage; i++){
					Thread.sleep(800);
					HtmlUtil.requestPageByGet("https://api.github.com/repos/" + repo + "/issues/events?page=" + i +"&per_page=100&access_token=" + token,//getToken(tokens), 
							savePath + saveFilePath + "\\" + saveFilePath + "issues" + (fileNo++) + ".json");
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
		public CrawlThread(){
		}
		
		public CrawlThread(String name){
			this.currentThread().setName(name);
		}
		   // ç¬¬äºŒä¸ªçº¿ç¨‹å…¥å�£
		   public void run() {
			   RequestProjectIssues.request();
		   }
		}
}
