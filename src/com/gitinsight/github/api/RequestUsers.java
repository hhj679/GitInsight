package com.gitinsight.github.api;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.gitinsight.util.HtmlUtil;

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

		String savePath = "F:\\gitinsight\\github\\data\\users\\";
		File saveFile = new File(savePath);
		if(!saveFile.exists()) {
			saveFile.mkdirs();
		}
		String usersJson = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		//>=1000 important
		for(int i=1;i<10; i++) {
			try{
				String tempStr = HtmlUtil.requestPageByGet("https://api.github.com/search/users?q=followers:%3E=1000+type:user&per_page=100&page=" + i + "&sort=followers&access_token=" + getToken(), 
						savePath + "user1000_" + i);
				Thread.sleep(4000);
				if(tempStr == null || tempStr.trim().length()<=0){
					break;
				}
				usersJson = tempStr;
			} catch(Exception e) {
				LOG.error("request user error", e);	
				e.printStackTrace();
			}
		}
		LOG.debug(usersJson);
		
		//>=50
		Date startDate = formatter.parse("2008-01-01");
		Date nowDate = new Date();
		
		int fileNo = 1;
		
		HtmlUtil.requestPageByGet("https://api.github.com/search/users?q=followers:%3E=50+type:user+created:<" + formatter.format(startDate) + "&per_page=100&page=1&sort=joined&access_token=" + getToken(), 
		savePath + "user_0");
		
		while(startDate.before(nowDate)){
			try {
				Calendar cd = Calendar.getInstance();
				cd.setTime(startDate);
				cd.add(Calendar.MONTH, 1);
				
				String startStr = formatter.format(startDate);
				startDate = cd.getTime();
				
				cd.add(Calendar.DATE, -1);
				
				String tempStr = HtmlUtil.requestPageByGet("https://api.github.com/search/users?q=followers:%3E=50+type:user+created:\"" + startStr
						+ "+..+" + formatter.format(cd.getTime()) + "\"&per_page=100&page=1&sort=joined&access_token=" + getToken(), 
						savePath + "user_" + fileNo++);
				JSONObject usersJsonObj = JSONObject.fromObject(tempStr);
				int userCount = usersJsonObj.getInt("total_count");
				
				if(userCount>1000){
					LOG.error("date: " + formatter.format(startDate) + " to " + formatter.format(cd.getTime()) + "return more than 1000!");
					continue;
				}
				
				int pageCount = userCount/100 + 1;
				for(int i=2;i<=pageCount; i++) {
					try{
						tempStr = HtmlUtil.requestPageByGet("https://api.github.com/search/users?q=followers:%3E=50+type:user+created:\"" + startStr
						+ "+..+" + formatter.format(cd.getTime()) + "\"&per_page=100&page=" + i + "&sort=joined&access_token=" + getToken(), 
								savePath + "user_" + fileNo++);
						Thread.sleep(4000);
						if(tempStr == null || tempStr.trim().length()<=0){
							break;
						}
						usersJson = tempStr;
					} catch(Exception e) {
						LOG.error("request user error", e);	
						e.printStackTrace();
					}
				}
				
				Thread.sleep(4000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static String getToken(){
		int no = (int)(requireNo++/30);
		if(no>=API_ACCESS_TOKEN.length){
			no = 0;
			requireNo = 0;
		}
		return API_ACCESS_TOKEN[no];
	}
}
