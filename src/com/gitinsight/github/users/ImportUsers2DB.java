package com.gitinsight.github.users;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.gitinsight.github.api.RequestDataByAPI;
import com.gitinsight.util.BlankUtil;
import com.gitinsight.util.DBUtil;
import com.gitinsight.util.HtmlUtil;

public class ImportUsers2DB {
	public static Logger LOG = Logger.getLogger(ImportUsers2DB.class);
	
	private static String TMP_DATA_PATH = "D:\\gitinsight\\github\\data\\users\\";
	
	private static String SEARCH_USER_API_URL = "https://api.github.com/search/users";
	
	private static String USERS_API_URL = "https://api.github.com/users/";
	
	private static String MYSQL_USER_SOURCE_TABLE_NAME = "users_source";
	
	//final private static long SLEEP_TIME = 2000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Map<String, String> map = getPorjectsAuthor("docker");
//		int i = 0;
//		for(Entry<String, String> e:map.entrySet()) {
//			System.out.println((++i) + "==>" + e.getKey() + ":" + e.getValue());
//		}
		
		createUserAndProjectLink("apache spark");
		
//		queryPorjectsAuthorAndImport("apache spark");
	}
	
	public static void requestPorjectsAuthorAndImport(String projectName) {
		Map<String, String> map = getPorjectsAuthor(projectName);
		LOG.info("project:" + projectName + "'s author count:" + map.size());
		
		int authorCount = 0;
		for(Entry<String, String> e:map.entrySet()) {
			try {
				LOG.info("Start query " + e.getValue() + "[" + (++authorCount) + "/" + map.size() + "]");
				
				String resultJSON = HtmlUtil.requestPageByGet(SEARCH_USER_API_URL + "?q=" + e.getValue().replace(" ", "%20") + "+type:user&" + RequestDataByAPI.API_ACCESS_TOKEN, 
						TMP_DATA_PATH + "search-" + e.getValue().replace(" ", "%20")); //如果实际数据少于json中的total_count，需要细化查询条件repos

				if(resultJSON!=null && resultJSON.trim().length()>0) {
					JSONObject jsona = JSONObject.fromObject(resultJSON);
					JSONArray itemsJson = jsona.optJSONArray("items");
					
					if(null==itemsJson) {
						continue;
					}
					
					if(jsona.optInt("total_count")!=itemsJson.size()){
						LOG.warn("query user's param need to more info!");
					}
					
					String sql = " insert into " + MYSQL_USER_SOURCE_TABLE_NAME 
							+ "(login,git_id,avatar_url,gravatar_id,url,html_url,followers_url,following_url,gists_url,starred_url,subscriptions_url,organizations_url,repos_url,events_url,received_events_url,type,site_admin,name,company,blog,location,email,hireable,bio,public_repos,public_gists,followers,following,created_at,updated_at)" 
							+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
					Connection conn = null;
					PreparedStatement pstmt = null;
					try{
						conn = DBUtil.openConnection();
						
						for(int i=0;i<itemsJson.size();i++) {
							try{
								JSONObject userObj = itemsJson.getJSONObject(i);
								String userLogin = userObj.optString("login");
								
								if(isLoginExistInDB(userLogin, conn, pstmt)){
									continue;
								}
								
								String usersJson = HtmlUtil.requestPageByGet(USERS_API_URL + userLogin + "?" + RequestDataByAPI.API_ACCESS_TOKEN, TMP_DATA_PATH + userLogin);
								if(usersJson == null || usersJson.trim().length()<=0){
									continue;
								}
								
								JSONObject userInfoJsonO = JSONObject.fromObject(usersJson);

								
								pstmt = conn.prepareStatement(sql);
								
								//insert into table
								pstmt.setString(1, userInfoJsonO.optString("login"));
								pstmt.setInt(2, userInfoJsonO.optInt("id"));
								pstmt.setString(3, BlankUtil.getString(userInfoJsonO.optString("avatar_url")));
								pstmt.setString(4, BlankUtil.getString(userInfoJsonO.optString("gravatar_id")));
								pstmt.setString(5, BlankUtil.getString(userInfoJsonO.optString("url")));
								pstmt.setString(6, BlankUtil.getString(userInfoJsonO.optString("html_url")));
								pstmt.setString(7, BlankUtil.getString(userInfoJsonO.optString("followers_url")));
								pstmt.setString(8, BlankUtil.getString(userInfoJsonO.optString("following_url")));
								pstmt.setString(9, BlankUtil.getString(userInfoJsonO.optString("gists_url")));
								pstmt.setString(10, BlankUtil.getString(userInfoJsonO.optString("starred_url")));
								pstmt.setString(11, BlankUtil.getString(userInfoJsonO.optString("subscriptions_url")));
								pstmt.setString(12, BlankUtil.getString(userInfoJsonO.optString("organizations_url")));
								pstmt.setString(13, BlankUtil.getString(userInfoJsonO.optString("repos_url")));
								pstmt.setString(14, BlankUtil.getString(userInfoJsonO.optString("events_url")));
								pstmt.setString(15, BlankUtil.getString(userInfoJsonO.optString("received_events_url")));
								pstmt.setString(16, userInfoJsonO.optString("type"));
								pstmt.setInt(17, userInfoJsonO.optBoolean("site_admin")?1:0);
								pstmt.setString(18, userInfoJsonO.optString("name"));
								pstmt.setString(19, BlankUtil.getString(userInfoJsonO.optString("company")));
								pstmt.setString(20, BlankUtil.getString(userInfoJsonO.optString("blog")));
								pstmt.setString(21, BlankUtil.getString(userInfoJsonO.optString("location")));
								pstmt.setString(22, BlankUtil.getString(userInfoJsonO.optString("email")));
								pstmt.setInt(23, userInfoJsonO.optBoolean("hireable")?1:0);
								pstmt.setString(24, BlankUtil.getObject(userInfoJsonO.get("bio")));
								pstmt.setInt(25, userInfoJsonO.optInt("public_repos"));
								pstmt.setInt(26, userInfoJsonO.optInt("public_gists"));
								pstmt.setInt(27, userInfoJsonO.optInt("followers"));
								pstmt.setInt(28, userInfoJsonO.optInt("following"));
								pstmt.setString(29, userInfoJsonO.optString("created_at"));
								pstmt.setString(30, userInfoJsonO.optString("updated_at"));
								
								pstmt.executeUpdate();
								LOG.info("insert user:" + userInfoJsonO.optString("login") + " success! ");
								
								//pstmt.clearParameters();
							} catch(Exception e2) {
								e2.printStackTrace();
							} 
//							finally {
//								DBUtil.closeStatement(pstmt);
//							}
						}
					} catch(Exception e3) {
						e3.printStackTrace();
					} finally {
						DBUtil.closeStatement(pstmt);
						DBUtil.closeConn(conn);
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static boolean isLoginExistInDB(String login, Connection conn, PreparedStatement pstmt) {
		int count = 0;
		
		ResultSet rs = null;
		try {
			String querySql = "select count(id) from users_source where login='" + login + "'";
			pstmt = conn.prepareStatement(querySql);
//			pstmt.setString(1, project);
			rs = pstmt.executeQuery(querySql);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			LOG.debug("login:" + login + "'s count in table users_source is" + count);
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
		}
		
		return count>0?true:false;//sort
	}
	
	public static boolean isLoginExistInDB(String login) {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String querySql = "select count(id) from users_source where login=?";
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, login);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			LOG.debug("login:" + login + "'s count in table users_source is " + count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return count>0?true:false;//sort
	}

	public static Map<String, String> getPorjectsAuthor(String project) {
		Map<String, String> membersMap = new HashMap<String, String>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String querySql = "select distinct author FROM gitlog_source where projectid=(select id from projects where name=?) order by author";
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, project);
			rs = pstmt.executeQuery();
			int count = 0;
			while (rs.next()) {
				count ++;
				
				String auther = rs.getString(1);
				String[] userTmp = auther.split("<");
				membersMap.put(userTmp[1].replace(">", "").trim(), userTmp[0].trim());
			}
			LOG.debug("project:" + project + "'s commit count:" + count);
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return new TreeMap<String, String>(membersMap);//sort
	}
	
	public static void createUserAndProjectLink(String projectName){
		String querySql = "select distinct author,projectid FROM gitlog_source where projectid=(select id from projects where name=?) order by author";
		String[] reColsName = {"author", "projectid"};
		List<Map<Object, Integer>> params = new ArrayList<Map<Object, Integer>>();
		Map<Object, Integer> pName = new HashMap<Object, Integer>();
		pName.put(projectName, DBUtil.P_STRING);
		params.add(pName);
		
		List<Map<String, Object>> rsList = DBUtil.getTableData(querySql, params, reColsName);
		LOG.debug("Project:" + projectName + "'s author count is " + rsList.size());
		
		querySql = "select * from users_source";
		reColsName = new String[] {"id", "email", "name", "company"};
		List<Map<String, Object>> userList = DBUtil.getTableData(querySql, null, reColsName);
		LOG.debug("All users count is " + userList.size());
		
		for(Map<String, Object> m : rsList) {
			String[] author = ((String) m.get("author")).split("<");// + ":" + m.get("projectid"));
			boolean isFoundUser = false;
			
			for(Map<String, Object> um:userList) {
				String umail = (String) um.get("email");
				if(umail.trim().equalsIgnoreCase(author[1].replace(">", ""))){
					if(!author[0].trim().equalsIgnoreCase(um.get("name").toString())){
						LOG.info("User " + umail + " take tow name :" + author[0] + " ppp;;; " + um.get("name"));
					}
					//insert to links table
					String inertSQL = "insert into projectmemberslink(projectId, userId) values(?, ?)";
					List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
					Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
					Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
					pmap1.put(m.get("projectid"), DBUtil.P_INT);
					pmap2.put(um.get("id"), DBUtil.P_INT);
					insertParams.add(pmap1);
					insertParams.add(pmap2);
					
					DBUtil.insertTableData(inertSQL, insertParams);
					isFoundUser = true;
					break;
				}
			}
			
			//not found commitor in user's table
			if(!isFoundUser) {
				LOG.warn("Not found user in user's table by email: " + m.get("author"));
				//look up by name and company compare with project's repo
				
			}
		}
	}
}
