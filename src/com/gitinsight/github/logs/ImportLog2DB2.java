package com.gitinsight.github.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.gitinsight.util.DBUtil;

public class ImportLog2DB2 {
	public static Logger LOG = Logger.getLogger(ImportLog2DB.class);
	
	static BlockingQueue<Hashtable<String, String>> QUEUE = null;

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		QUEUE = null;
		for(int i=1;i<7;i++){
			File file = new File("F:\\gitinsight\\github\\data\\projects\\part" + i);
			importGitlogs2DB(file);
		}
	}

	public static void importGitlogs2DB(File files) throws SQLException{
		if(files.isDirectory()){
			for(File f:files.listFiles()) {
				try {
					//importGitlog2DB(f);
					LogFilter filter = new LogFilter(f.getName() + "_logs"); 
					File[] logfiles = f.listFiles(filter);
					long lastModified = -1;
					File lastLogfile = null;

					for(File lf:logfiles) {
						if(lf.lastModified()>lastModified){
							lastModified = lf.lastModified();
							lastLogfile = lf;
						}
					}

					if(lastLogfile!=null) {
						if(lastLogfile.length()>100000000){
							LOG.error("File too lage, the file is " + f.getPath() +" " +  f.getName());
							continue;
						}
						
						importGitlog2DB2(lastLogfile);
					} else {
						LOG.error(f.getPath() + " no log's file, please run git log --stat to create log's file!");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				importGitlog2DB(files);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	static class LogFilter implements FilenameFilter{  
		private String type;  
		public LogFilter(String type){  
			this.type = type;  
		}  
		public boolean accept(File dir,String name){  
			return name.startsWith(type);  
		}  
	}  
	
	public static void importGitlog2DB2(File file) throws IOException {
		if(file.isFile()){
			String projectName = file.getParent();
			projectName = projectName.substring(projectName.lastIndexOf(File.separator) + 1);
			int projectid = getProjectIdByName(projectName);
			if(projectid<0){
				LOG.error("Can not found project while import git log:" + projectName);
				return;
			}
			
			while(QUEUE!=null && !QUEUE.isEmpty()){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			QUEUE = null;
			QUEUE = new LinkedBlockingQueue<Hashtable<String, String>>();
			
			LOG.info("start to read file:" + file.getPath());
			
			String commit = "";
			String merge = "";
			String author = "";
			String date = "";
			String marks = "";
			String changefiles = "";
			String addline = "";
			String delline = "";
			
			FileReader reader = null;
			BufferedReader br = null;
			
			try{
				reader = new FileReader(file);
				br = new BufferedReader(reader);

				String line = null;

				//read log file start
				while((line = br.readLine()) != null) {
					if(line.startsWith("commit")){
						//insert last commit info
						if(commit.trim().length()>0){
							Hashtable<String, String> ht = new Hashtable<String, String>();
							ht.put("projectid", String.valueOf(projectid));
							ht.put("commit", commit);
							ht.put("merge", merge);
							ht.put("author", author);
							ht.put("date", date);
							ht.put("changefiles", changefiles);
							ht.put("addline", addline);
							ht.put("delline", delline);
							ht.put("marks", marks);
							QUEUE.put(ht);
							//insert to db
//							pstmt.setInt(1, projectid);
//							pstmt.setString(2, commit);
//							pstmt.setString(3, merge);
//							pstmt.setString(4, author);
//							pstmt.setString(5, date);
//							pstmt.setString(6, changefiles);
//							pstmt.setString(7, addline);
//							pstmt.setString(8, delline);
//							pstmt.setString(9, marks);

							//reset params
							commit = line.replace("commit ", "");
							merge = "";
							author = "";
							date = "";
							marks = "";
							changefiles = "";
							addline = "";
							delline = "";
						} else {
							commit = line.replace("commit ", "");
						}
					} else if(line.startsWith("Merge:")){
						merge = line.replace("Merge:", "").trim();
					} else if(line.startsWith("Author:")){
						author = line.replace("Author:", "").trim();
					} else if(line.startsWith("Date:")){
						date = line.replace("Date:", "").trim();
					} else if(line.contains("files changed,") && (line.contains("deletions(-)")||line.contains("insertions(+)"))){
						line = line.trim();
						changefiles = line.substring(0, line.indexOf(" ")).trim();
						if(line.contains("insertions(+)")){
							addline = line.substring(line.indexOf(",")+1, line.indexOf("insertions(+)")).trim();
						}

						if(line.contains("deletions(-)")){
							delline = line.substring(line.lastIndexOf(",")+1, line.indexOf("deletions(-)")).trim();
						}
					} else if(line.startsWith("    ")){
						marks += line;
					}
		        }//read log file end
				commit = null;
				merge = null;
				author = null;
				date = null;
				marks = null;
				changefiles = null;
				addline = null;
				delline = null;
				
				LOG.info("start to import file:" + file.getPath());
				for(int tNo=0; tNo<35; tNo++) {
					new Thread() {
			            @Override
			            public void run() {
			            	String sql = " insert into gitlog_source(projectid,commit,merge,author,date,changefiles,addline,delline,marks)values(?,?,?,?,?,?,?,?,?) ";
			    			Connection conn = null;
			    			PreparedStatement pstmt = null;
			    			
			            	try {
			            		conn = DBUtil.openConnection();
			    				pstmt = conn.prepareStatement(sql);
			    				while(!QUEUE.isEmpty()){
			    					try{
			    						Hashtable<String, String> ht = QUEUE.take();
			    						pstmt.setString(1, ht.get("projectid"));
			    						pstmt.setString(2, ht.get("commit"));
			    						pstmt.setString(3, ht.get("merge"));
			    						pstmt.setString(4,  ht.get("author"));
			    						pstmt.setString(5,  ht.get("date"));
			    						pstmt.setString(6,  ht.get("changefiles"));
			    						pstmt.setString(7,  ht.get("addline"));
			    						pstmt.setString(8,  ht.get("delline"));
			    						pstmt.setString(9,  ht.get("marks"));

			    						pstmt.executeUpdate();
			    						
			    						ht = null;
			    					} catch (Exception e) {
			    						// TODO Auto-generated catch block
										e.printStackTrace();
									}
			    				}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								DBUtil.closeStatement(pstmt);
								DBUtil.closeConn(conn);
							}
			            }
			        }.start();
				}
			} catch(Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage());
			} finally {
				br.close();
				reader.close();
			}
		}
	}


	public static void importGitlog2DB(File file) throws IOException, SQLException {
		if(file.isFile()){
			LOG.info("start to import file:" + file.getPath());
//			List<String> fileLines = FileUtils.readLines(file);

			String projectName = file.getParent();
			projectName = projectName.substring(projectName.lastIndexOf(File.separator) + 1);
			int projectid = getProjectIdByName(projectName);
			if(projectid<0){
				LOG.error("Can not found project while import git log:" + projectName);
				return;
			}

			String commit = "";
			String merge = "";
			String author = "";
			String date = "";
			String marks = "";
			String changefiles = "";
			String addline = "";
			String delline = "";
			
			FileReader reader = null;
			BufferedReader br = null;
					
			String sql = " insert into gitlog_source(projectid,commit,merge,author,date,changefiles,addline,delline,marks)values(?,?,?,?,?,?,?,?,?) ";
			Connection conn = null;
			PreparedStatement pstmt = null;
			try{
				conn = DBUtil.openConnection();
				pstmt = conn.prepareStatement(sql);

				int lineNo = 0;
				int ltag = 0;
				reader = new FileReader(file);
		        br = new BufferedReader(reader);
		        String line = null;
		        
		        while((line = br.readLine()) != null) {
		        	try{
						lineNo ++;
						ltag ++;
						if(ltag>100000){
							ltag = 0;
							DBUtil.closeStatement(pstmt);
							DBUtil.closeConn(conn);
							
							conn = DBUtil.openConnection();
							pstmt = conn.prepareStatement(sql);
						}
						if(line.startsWith("commit")){
							//insert last commit info
							if(commit.trim().length()>0){
								//insert to db
								pstmt.setInt(1, projectid);
								pstmt.setString(2, commit);
								pstmt.setString(3, merge);
								pstmt.setString(4, author);
								pstmt.setString(5, date);
								pstmt.setString(6, changefiles);
								pstmt.setString(7, addline);
								pstmt.setString(8, delline);
								pstmt.setString(9, marks);

								pstmt.executeUpdate();
								LOG.debug("insert commit:" + commit + " success! The merge is " + merge);

								//reset params
								commit = line.replace("commit ", "");
								merge = "";
								author = "";
								date = "";
								marks = "";
								changefiles = "";
								addline = "";
								delline = "";
							} else {
								commit = line.replace("commit ", "");
							}
						} else if(line.startsWith("Merge:")){
							merge = line.replace("Merge:", "").trim();
						} else if(line.startsWith("Author:")){
							author = line.replace("Author:", "").trim();
						} else if(line.startsWith("Date:")){
							date = line.replace("Date:", "").trim();
						} else if(line.contains("files changed,") && (line.contains("deletions(-)")||line.contains("insertions(+)"))){
							line = line.trim();
							changefiles = line.substring(0, line.indexOf(" ")).trim();
							if(line.contains("insertions(+)")){
								addline = line.substring(line.indexOf(",")+1, line.indexOf("insertions(+)")).trim();
							}

							if(line.contains("deletions(-)")){
								delline = line.substring(line.lastIndexOf(",")+1, line.indexOf("deletions(-)")).trim();
							}
						} else if(line.startsWith("    ")){
							marks += line;
						}
					} catch(Exception e) {
						e.printStackTrace();
						LOG.error("Error at line:" + lineNo + ". The error is:\r\n" + e.getMessage());
					}
		        }
				LOG.info("Import file:" + file.getName() + " finish!");
			} catch(Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage());
			} finally {
				 br.close();
			        reader.close();
			        
				DBUtil.closeStatement(pstmt);
				DBUtil.closeConn(conn);
			}
		} else {
			LOG.error("It must be a file not a directory!");
			throw(new IOException("It must be a file not a directory!"));
		}
	}

	public static int getProjectIdByName(String name) {
		int projectid = -1;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String querySql = "select id from git_projects where name=?";
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				projectid = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}

		return projectid;//sort
	}
}
