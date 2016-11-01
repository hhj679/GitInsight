package com.gitinsight.github.api;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.gitinsight.util.HtmlUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RequestUsers {

	public RequestUsers() {
		// TODO Auto-generated constructor stub
	}
	public static Logger LOG = Logger.getLogger(RequestUsers.class);
	public static String API_ACCESS_TOKEN[] = {"b50b4fabe5930370a726a8a8b5b3bd17ca2de368", "04bb9a7506ab600ce53e5f004c2d35bb7f41dfd0", "f81ef6ec593ca3cea7b2cbddc72cc1d03f47e535", "6d12e500f661490fbe978100dfbd7ede32648f14"
			, "cf8ee7d276e027ffe96808403881c6ceb5e7d104", "1975c6cc602fa5e422a4eeea739a96538c0f4725", "00737e62d82bf610f0b515189d43e09b8282ef47"
			, "ede65a8a32b24acbb4d134f25c44c8c5a0a5cf4c", "b41e59078a547a53fd12eb1d3ff377218364101a", "abc57c2a8ac4ede2fb1551dcf699257fa6dad13c"};
	private static int requireNo = 0;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub

		Set<String> userSet = new TreeSet<String>();
		String filePath = "F:\\gitinsight\\github\\data\\users\\";
		String savePath = "F:\\gitinsight\\github\\data\\userinfo\\";
		File saveFile = new File(savePath);
		if(!saveFile.exists()) {
			saveFile.mkdirs();
		}
		File userFile = new File(filePath);
		for(File uf : userFile.listFiles()) {
			JSONObject uobj = JSONObject.fromObject(FileUtils.readFileToString(uf));
			JSONArray uarray = uobj.getJSONArray("items");
			for(int i=0; i<uarray.size();i++){
				JSONObject u = uarray.getJSONObject(i);
				userSet.add(u.getString("login"));
			}
		}
		
		LOG.info("user set size:" + userSet.size());
		int fileNo = 0;
		for(String login : userSet) {
			try{
				HtmlUtil.requestPageByGet("https://api.github.com/users/" + login + "?access_token=" + getToken(), 
						savePath + "user_" + login + "_" + fileNo + ".json");
				LOG.info("user set size:" + fileNo++);
			} catch(Exception e) {
				LOG.error("request user error", e);	
				e.printStackTrace();
			}
		}
		
	}

	public static String getToken(){
		int no = (int)(requireNo++/5000);
		if(no>=API_ACCESS_TOKEN.length){
			no = 0;
			requireNo = 0;
		}
		return API_ACCESS_TOKEN[no];
	}
}
