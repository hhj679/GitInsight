package com.gitinsight.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

public class HtmlUtil {
	public static Logger LOG = Logger.getLogger(HtmlUtil.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date d = new Date(1437069661);
		System.out.println(d);
	}

	public static String requestPageByGet(String urlStr, String outputFile) throws IOException {
		LOG.info("Start to get api:" + urlStr);
		InputStream is = null;
		BufferedReader reader = null;
		BufferedWriter bw = null;
		FileOutputStream writerStream = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("content-type", "text/html");

			conn.connect();
			if(conn.getResponseCode()!=200){
				return "";
			}
			
			is = conn.getInputStream();

			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String s;
			StringBuffer sb = new StringBuffer();
			while ((s = reader.readLine()) != null) {
				sb.append(s); 
			}
			writerStream = new FileOutputStream(outputFile);    
			bw = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
			bw.write(sb.toString());
			bw.flush();
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception",e);
		} finally {
			if(bw!=null) bw.close();
			if(writerStream!=null) writerStream.close();
			if(reader!=null) reader.close();
			if(is!=null) is.close();
		}
		return null;
	}

	public String requestPageByPost(String urlStr, String json, String outputFile) throws IOException{
		LOG.info("Start to post api:" + urlStr);
		LOG.debug("Post json:" + json);
		
		InputStream is = null;
		BufferedWriter out = null;
		BufferedReader reader = null;
		BufferedWriter bw = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			conn.connect();

			out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
			out.write("data="+json);
			out.flush();

			is=conn.getInputStream();
			reader=new BufferedReader(new InputStreamReader(is,"utf-8"));
			String s;
			StringBuffer sb = new StringBuffer();
			while ((s = reader.readLine()) != null) {
				sb.append(s); 
			}
			
			bw = new BufferedWriter(new FileWriter(outputFile));
			bw.write(sb.toString());
			bw.flush();
			
			is.close();
			
			return sb.toString();
		} catch (Exception e){
			e.printStackTrace();
			LOG.error("Exception",e);
		} finally {
			if(bw!=null) bw.close();
			if(out!=null) out.close();
			if(reader!=null) reader.close();
			if(is!=null) is.close();
		}
		return null;
	}
}
