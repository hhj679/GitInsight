package com.gitinsight.github.api;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.gitinsight.util.HtmlUtil;

public class RequestDataByAPI {
	public static Logger LOG = Logger.getLogger(RequestDataByAPI.class);
	
	public static String API_ACCESS_TOKEN = "access_token=90d0b39eddcc1dba44c5305b3324d325b4293429";
	 
	public static void main(String[] args) throws IOException {
		//test
		System.out.println(HtmlUtil.requestPageByGet("https://api.github.com/search/users?q=Sven%20Dowideit&" + API_ACCESS_TOKEN, "D:\\gitinsight\\github\\data\\users\\SvenDowideit.json"));
	}
}
