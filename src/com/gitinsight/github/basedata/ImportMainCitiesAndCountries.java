package com.gitinsight.github.basedata;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import com.gitinsight.util.DBUtil;

public class ImportMainCitiesAndCountries {
	public static Logger LOG = Logger.getLogger(ImportMainCitiesAndCountries.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			importCities("D:\\gitinsight\\doc\\Listofcitiesbylongitude.txt", false);
//		} catch (XPatherException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			importCountry("D:\\gitinsight\\doc\\Listofnationalcapitals.html", false);
//		} catch (IOException | XPatherException | ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			insertCountryCodes("D:\\gitinsight\\doc\\IOCFIFASO3166countrycodes.html");
		} catch (IOException | XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean isCityExistInDB(String name) {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String querySql = "select count(id) from cities where cityname=?";
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			LOG.debug("city:" + name + "'s count in table cities is " + count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return count>0?true:false;//sort
	}
	
	public static boolean isCountryExistInDB(String name) {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String querySql = "select count(id) from countries where name=?";
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			LOG.debug("country:" + name + "'s count in table cities is " + count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return count>0?true:false;//sort
	}
	
	public static int getCityIdByName(String name) {
		int id = -1;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String querySql = "select id from cities where cityname=?";
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1);
			}
			LOG.debug("city:" + name + "'s id in table cities is " + id);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return id;//sort
	}

	public static void importCities(String filePath, boolean isUpdate) throws XPatherException, IOException, ParseException{
		HtmlCleaner cleaner = new  HtmlCleaner();   
		TagNode node = cleaner.clean(new File(filePath), "UTF-8" );  

		//Latitude
		Object[] latitudeNs = node.evaluateXPath("//div[@id='mw-content-text']/table[@class='wikitable sortable jquery-tablesorter']/tbody/tr/td[1]");
		
		//Longitude
		Object[] longitudeNs = node.evaluateXPath("//div[@id='mw-content-text']/table[@class='wikitable sortable jquery-tablesorter']/tbody/tr/td[2]");
		
		//City
		Object[] cityNs = node.evaluateXPath("//div[@id='mw-content-text']/table[@class='wikitable sortable jquery-tablesorter']/tbody/tr/td[3]");
		
		//State
		Object[] stateNs = node.evaluateXPath("//div[@id='mw-content-text']/table[@class='wikitable sortable jquery-tablesorter']/tbody/tr/td[4]");
		
		//Country
		Object[] countryNs = node.evaluateXPath("//div[@id='mw-content-text']/table[@class='wikitable sortable jquery-tablesorter']/tbody/tr/td[5]");
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(3);
		df.setMaximumFractionDigits(3);
		
		String inertSQL = "insert into cities(longitude, latitude, cityName, stateName, countryName) values(?, ?, ?, ?,?)";
		
		for(int i=0;i<latitudeNs.length;i++) {
			TagNode latitudeNode = (TagNode) latitudeNs[i];
			TagNode longitudeNode = (TagNode) longitudeNs[i];
			TagNode cityNode = (TagNode) cityNs[i];
			TagNode stateNode = (TagNode) stateNs[i];
			TagNode countryNode = (TagNode) countryNs[i];
			
			String latitude = latitudeNode.getText().toString().trim();
			String longitude = longitudeNode.getText().toString().trim();
			String city = cityNode.getText().toString().trim().replace("&nbsp;", "");
			String state = stateNode.getText().toString().trim().replace("&nbsp;", "");
			String country = countryNode.getText().toString().replace("&nbsp;", "");
			
			if(!isUpdate && isCityExistInDB(city)){
				continue;
			}
			
			double dLat = 0;
			double dLong = 0;
			if(longitude.contains("E")){
				String[] tmp = longitude.replace(" ", "").replace("′E", "").replace("'E", "").replace("/W", "").split("°");
				dLong = Integer.parseInt(tmp[0]) + Double.parseDouble(tmp[1])/60;
			} else {
				String[] tmp = longitude.replace(" ", "").replace("′W", "").replace("'W", "").split("°");
				dLong = Integer.parseInt(tmp[0]) + Double.parseDouble(tmp[1])/60;
				dLong = -dLong;
			}

			if(latitude.contains("N")){
				String[] tmp = latitude.replace(" ", "").replace("′N", "").replace("'N", "").split("°");
				dLat = Integer.parseInt(tmp[0]) + Double.parseDouble(tmp[1])/60;
			} else {
				String[] tmp = latitude.replace(" ", "").replace("′S", "").replace("'S", "").split("°");
				dLat = Integer.parseInt(tmp[0]) + Double.parseDouble(tmp[1])/60;
				dLat = -dLat;
			}
			
			String sLong = df.format(dLong);
			String sLat = df.format(dLat);
			
			Number lLong = df.parse(sLong);
			Number lLat = df.parse(sLat);
			
			//insert to table
			List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
			Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap5 = new HashMap<Object, Integer>();
			pmap1.put(lLong, DBUtil.P_STRING);
			pmap2.put(lLat, DBUtil.P_STRING);
			pmap3.put(city, DBUtil.P_STRING);
			pmap4.put(state, DBUtil.P_STRING);
			pmap5.put(country, DBUtil.P_STRING);
			insertParams.add(pmap1);
			insertParams.add(pmap2);
			insertParams.add(pmap3);
			insertParams.add(pmap4);
			insertParams.add(pmap5);
			
			DBUtil.insertTableData(inertSQL, insertParams);
			
			LOG.info("insert sucess:" + lLong + "," + lLat + "," + city + "," + state + "," + country);
		}
	}
	
	public static void importCountry(String filePath, boolean isUpdate) throws IOException, XPatherException, ParseException {
		HtmlCleaner cleaner = new  HtmlCleaner();   
		TagNode node = cleaner.clean(new File(filePath), "UTF-8" );  

		//capital
		Object[] capitalNs = node.evaluateXPath("//div[@id='mw-content-text']/table[@class='wikitable sortable']/tbody/tr/td[1]");
		//country
		Object[] countryNs = node.evaluateXPath("//div[@id='mw-content-text']/table[@class='wikitable sortable']/tbody/tr/td[2]");
		
		String inertSQL = "insert into countries(name, capital) values(?, ?)";
		
		for(int i=0;i<capitalNs.length;i++) {
			TagNode capitalNode = (TagNode) capitalNs[i];
			TagNode countryNode = (TagNode) countryNs[i];
			
			String capital = capitalNode.getText().toString().trim();
			if(capital.contains("(")){
				capital = capital.substring(0,capital.indexOf("(")).trim();
			} else if(capital.contains("（")){
				capital = capital.substring(0,capital.indexOf("（")).trim();
			}
			String country = countryNode.getText().toString().replace("&nbsp;", "").replace("&#160;", "");
			
			int id = getCityIdByName(capital);
			if(-1==id){
				continue;
			}
			
			if(!isUpdate && isCityExistInDB(country)){
				continue;
			}
			
			//insert to table
			List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
			Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
			pmap1.put(country, DBUtil.P_STRING);
			pmap2.put(id, DBUtil.P_STRING);
			insertParams.add(pmap1);
			insertParams.add(pmap2);
			
			DBUtil.insertTableData(inertSQL, insertParams);
			
			LOG.info("insert sucess:" + country + "," + capital + "," + id );
		}
	}
	
	public static void insertCountryCodes(String filePath) throws IOException, XPatherException{
		HtmlCleaner cleaner = new  HtmlCleaner();   
		TagNode node = cleaner.clean(new File(filePath), "UTF-8" );  

		//country
		Object[] countryNs = node.evaluateXPath("//div[@id='mw-content-text']/table[2]/tbody/tr/td[2]");
		//ICO
		Object[] iocNs = node.evaluateXPath("//div[@id='mw-content-text']/table[2]/tbody/tr/td[3]");
		//FIFA
		Object[] fifaNs = node.evaluateXPath("//div[@id='mw-content-text']/table[2]/tbody/tr/td[4]");
		//ISO
		Object[] isoNs = node.evaluateXPath("//div[@id='mw-content-text']/table[2]/tbody/tr/td[5]");
				
		String inertSQL = "UPDATE countries set IOC=?, FIFA=?, ISO=? where name=?";
		
		for(int i=0;i<iocNs.length;i++) {
			TagNode countryNode = (TagNode) countryNs[i];
			TagNode iocNode = (TagNode) iocNs[i];
			TagNode fifaNode = (TagNode) fifaNs[i];
			TagNode isoNode = (TagNode) isoNs[i];
			
			String country = countryNode.getText().toString().trim();
			String ioc = iocNode.getText().toString().trim();
			String fifa = fifaNode.getText().toString().trim();
			String iso = isoNode.getText().toString().trim();
			
			if(country.contains("[")){
				country = country.substring(0, country.indexOf("["));
			}
			if(ioc.contains("[")){
				ioc = ioc.substring(0, ioc.indexOf("["));
			}
			if(fifa.contains("[")){
				fifa = fifa.substring(0, fifa.indexOf("["));
			}
			if(iso.contains("[")){
				iso = iso.substring(0, iso.indexOf("["));
			}
			
			//insert to table
			List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
			Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
			pmap1.put(ioc, DBUtil.P_STRING);
			pmap2.put(fifa, DBUtil.P_STRING);
			pmap3.put(iso, DBUtil.P_STRING);
			pmap4.put(country, DBUtil.P_STRING);
			insertParams.add(pmap1);
			insertParams.add(pmap2);
			insertParams.add(pmap3);
			insertParams.add(pmap4);
			
			if(DBUtil.insertTableData(inertSQL, insertParams)>0){
				LOG.info("insert sucess:" + country + "," + ioc + "," + fifa + "," + iso );
			} else {
				LOG.info("insert fail:" + country + "," + ioc + "," + fifa + "," + iso );
			}
		}
	}
}
