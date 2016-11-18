package com.gitinsight.issues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.gitinsight.util.BlankUtil;
import com.gitinsight.util.DBUtil;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ImportIsseus2 {
	public static Logger LOG = Logger.getLogger(ImportIsseus2.class);
	private static ConcurrentLinkedQueue<String> FILES_QUEUE = new ConcurrentLinkedQueue<String>();
	
	static ConcurrentLinkedQueue<List<File>> FILES_LISTS = new ConcurrentLinkedQueue<List<File>> ();
	
	public ImportIsseus2() {
		// TODO Auto-generated constructor stub
	}
	
	public String readByNIO(File file){
		//第一步 获取通道
		FileInputStream fis = null;
		FileChannel channel=null;
		try {
			fis = new FileInputStream(file);
			channel=fis.getChannel();
			//文件内容的大小
			int size=(int) channel.size();

			//第二步 指定缓冲区
			ByteBuffer buffer=ByteBuffer.allocate(1024*1024*2);
			//第三步 将通道中的数据读取到缓冲区中
			channel.read(buffer);

			buffer.flip();

			byte[] bt=buffer.array();

			String str = new String(bt,0,size);
			buffer.clear();
			buffer=null;
			
			return str;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				channel.close();
				fis.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<JSONObject> list = new ArrayList<JSONObject>();
		
//		String sql = "select s.git_id from insightdb.git_issues s";
//		String[] reColsName = {"git_id"};
//		List<Map<String, Object>> rsList = DBUtil.getTableData(sql, null, reColsName);
//		for(Map<String, Object> rsMap : rsList){
//			String projectName = String.valueOf(rsMap.get("git_id"));
//			idList.add(projectName);
//		}
//		try {
//			FileUtils.writeLines(new File("E:\\gitinsight\\data\\exist_issues_ids.txt"), idList);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		try {
			idList = FileUtils.readLines(new File("E:\\gitinsight\\data\\exist_issues_ids.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		LOG.info("database issues size:" + idList.size());
		try {
			List<String> filePaths = FileUtils.readLines(new File("E:\\gitinsight\\data\\issues_files.txt"));
			List<File> file1000 = new ArrayList<File>();
			for(int i=0;i<filePaths.size(); i++){
				String filePath = filePaths.get(i);
				if(filePath.endsWith(".txt")){
					continue;
				}
				file1000.add(new File(filePath));
				if(file1000.size()>=1000){
					FILES_LISTS.add(file1000);
					file1000 = new ArrayList<File>();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<10;i++){
			new ImportThread("Thread_" + i).start();
		}
		
		
//		LOG.info("size2:" + idList.size());
//		LOG.info("projects count: " + list.size());
//		try {
//			importIssuesBatch(list);
//		} catch (MySQLIntegrityConstraintViolationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	static List<String> idList = new Vector<>();
	public void importIs(){
		List<JSONObject> list = new ArrayList<JSONObject>();
		((ArrayList<JSONObject>) list).ensureCapacity(50000); 
		if(!FILES_LISTS.isEmpty()){
			List<File> files = FILES_LISTS.poll();
			

			for(File f: files){
//				LOG.info("start import file:" + f.getPath());
				JSONArray issues = new JSONArray();
				try {
					String jsonStr = FileUtils.readFileToString(f);
					issues = JSONArray.fromObject(jsonStr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Date start = new Date();
				for(int i=0; i<issues.size(); i++){
					try{
						JSONObject json = issues.getJSONObject(i);
						JSONObject issue = json.getJSONObject("issue");
						if(issue == null || issue.isNullObject() == true) {
							continue;
						}
						String id = String.valueOf(issue.optInt("id"));
						if(id == null || id.equalsIgnoreCase("null") || id.length()==0){
							continue;
						}
//						if(!idList.contains(id)){
							list.add(issue);
//						}
						json = null;
					} catch(Exception e1) {
						e1.printStackTrace();
						LOG.error("Exception", e1);
						LOG.error("error at file:" + f.getPath() + " items:" + i );
					}
				}
				Date end = new Date();
				LOG.info("read file: " + f.getPath() + " json array time:" + (end.getTime()-start.getTime())/1000 + " s.");
			}
			try {
				importIssuesBatch(list);
				if(!FILES_LISTS.isEmpty()){
					new ImportThread().start();getClass();
				}
			} catch (MySQLIntegrityConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void importIssuesBatch(List<JSONObject> list) throws MySQLIntegrityConstraintViolationException {
		String inertSQL = "insert into git_issues2(url, repository_url, labels_url, comments_url, events_url, html_url, git_id, number, title, user_login, labels, state, locked, assignee, assignees, milestone, comments, created_at, updated_at, closed_at, body)" 
				+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;
		PreparedStatement pstmt = null;

		try{
			conn = DBUtil.openConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(inertSQL);

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			
			for(JSONObject json : list){
				pstmt.setString(1, json.getString("url"));
				pstmt.setString(2, json.getString("repository_url"));
				pstmt.setString(3, json.getString("labels_url"));
				pstmt.setString(4, json.getString("comments_url"));
				pstmt.setString(5, json.getString("events_url"));
				pstmt.setString(6, json.getString("html_url"));
				pstmt.setInt(7, json.getInt("id"));
				pstmt.setInt(8, json.getInt("number"));
				pstmt.setString(9, BlankUtil.charString(json.getString("title")));
				pstmt.setString(10, json.getJSONObject("user").getString("login"));
				pstmt.setString(11, BlankUtil.getString(json.getString("labels")));
				pstmt.setString(12, json.getString("state"));
				pstmt.setInt(13, (json.getBoolean("locked")?1:0));
				pstmt.setString(14, BlankUtil.getString(json.getString("assignee")));
				pstmt.setString(15, BlankUtil.getString(json.getString("assignees")));
				pstmt.setString(16, BlankUtil.getString(BlankUtil.charString(json.getString("milestone"))));
				pstmt.setInt(17, json.getInt("comments"));
				pstmt.setTimestamp(18, new Timestamp(formatter.parse(json.getString("created_at")).getTime()));
				pstmt.setTimestamp(19, new Timestamp(formatter.parse(json.getString("updated_at")).getTime()));
				String closed_at = json.optString("closed_at");
				if(closed_at==null || closed_at.length()==0 || closed_at.equalsIgnoreCase("null")){
					pstmt.setTimestamp(20, null);
				} else {
					try{
						pstmt.setTimestamp(20, new Timestamp(formatter.parse(closed_at).getTime()));
					} catch(Exception e){
						LOG.error("closed at:" + closed_at);
					}
					
				}
				
				pstmt.setString(21, BlankUtil.getString(BlankUtil.charString(json.getString("body"))));
				
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			conn.commit();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
	}
	
	static class ImportThread extends Thread {
		public ImportThread(){
		}
		
		public ImportThread(String name){
			this.currentThread().setName(name);
		}
		   public void run() {
			   new ImportIsseus2().importIs();
		   }
		}

}
