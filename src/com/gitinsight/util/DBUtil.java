package com.gitinsight.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author Huang Haijing
 *ݿ⹤����
 */
public class DBUtil {
	public static Logger LOG = Logger.getLogger(DBUtil.class);
	
	final public static int P_STRING = 1;
	final public static int P_DATE = 3;
	final public static int P_INT = 2;
	final public static int P_BOOLEAN = 4;
	final public static int P_DOUBLE = 5;
	
	public static void closeConn(Connection conn){
		try {
			if(conn!=null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeResultSet(ResultSet rs){
		try {
			if(rs!=null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeStatement(Statement pstmt){
		try {
			if(pstmt!=null)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection openConnection() {
		Properties prop = new Properties();
		String driver = null;
		String url = null;
		String username = null;
		String password = null;

		try {
			prop.load(DBUtil.class.getClassLoader().getResourceAsStream(
					"DBConfig.properties"));

			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			username = prop.getProperty("username");
			password = prop.getProperty("password");
			
			Class.forName(driver);
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Map<String, Object>> getTableData(String sql, List<Map<Object, Integer>> params, String[] reColsName) {
		if(BlankUtil.isBlank(sql)){
			return null;
		}
		if(reColsName == null || reColsName.length<=0){
			return null;
		}
		
		List<Map<String, Object>> listR= new ArrayList<Map<String, Object>>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(sql);
			if(params!=null && params.size()>0){
				for(int i=0;i<params.size();i++) {
					Map<Object, Integer> p = params.get(i);
					Object e = p.keySet().toArray()[0];
					int type = p.get(e);
					switch (type) {
						case P_STRING:
							pstmt.setString(i+1, e.toString());
							break;
						case P_INT:	
							pstmt.setInt(i+1, (int) e);
							break;
						case P_DATE:
							pstmt.setDate(i+1, (Date) e);
							break;
						case P_BOOLEAN:
							pstmt.setBoolean(i+1, (boolean) e);
							break;
						case P_DOUBLE:
							pstmt.setDouble(i+1, (double) e);
							break;
							
					}
				}
			}
			rs = pstmt.executeQuery();
			int count = 0;
			while (rs.next()) {
				count ++;
				Map<String, Object> m = new HashMap<String, Object>();
				for(String s:reColsName){
//					try{
						m.put(s, rs.getObject(s));
//					} catch(SQLException se) {
//						if(se.getMessage().startsWith("Column") && se.getMessage().endsWith("not found.")){
//							continue;
//						} else {
//							se.printStackTrace();
//						}
//					}
				}
				
				listR.add(m);
				
//				rs.getObject(arg0)
//				
//				String auther = rs.getString(1);
//				String[] userTmp = auther.split("<");
//				membersMap.put(userTmp[1].replace(">", "").trim(), userTmp[0].trim());
			}
			LOG.debug("Query:\"" + sql + "\" result count:" + count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return listR;
	}
	
	public static int insertTableData(String sql, List<Map<Object, Integer>> params) {
		int rsb = -1;
		
		if(BlankUtil.isBlank(sql)){
			return -1;
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Object e = null;
		
		try {
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(sql);
			if(params!=null && params.size()>0){
				for(int i=0;i<params.size();i++) {
					Map<Object, Integer> p = params.get(i);
					e = p.keySet().toArray()[0];
					int type = p.get(e);
					switch (type) {
					case P_STRING:
						pstmt.setString(i+1, e.toString());
						break;
					case P_INT:	
						pstmt.setInt(i+1, (int) e);
						break;
					case P_DATE:
						pstmt.setDate(i+1, (Date) e);
						break;
					case P_BOOLEAN:
						pstmt.setBoolean(i+1, (boolean) e);
						break;
					case P_DOUBLE:
						pstmt.setDouble(i+1, (double) e);
						break;
					}
				}
			}
			rsb = pstmt.executeUpdate();
			LOG.debug("Insert:\"" + sql + "\" success!");
		} catch (Exception ex) {
			//LOG.error(ex.getMessage(), ex.fillInStackTrace());
			LOG.error( "error key is : " + e.toString());
			ex.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return rsb;
	}
}
