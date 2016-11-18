package com.gitinsight.openhub.projects;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gitinsight.util.DBUtil;
import com.gitinsight.util.HtmlUtil;

public class SearchProjectByName {
	public static Logger LOG = Logger.getLogger(SearchProjectByName.class);
	
	public static String API_KEY[] = {"4c1acbb9bc3f64f7695905caed062c3b13e0781cc40578f920f68c8279dc089d", "ac24b4bf1cfeffddf27f8172b7a947a4d5ff96bce7bd5e9ee0ae8f51928820e9",
			"50e800d0054609d3e454f019ddd02b41905f948c2427be3d329df93a48bfc180", "b2286c1e6b8d0e15cc70574182465035f75e8a970a3859a4ebd3244a44d4a3ea",
			"9cc185eae51ab8b1a3bf29f78af84b178beb929aefd859031e282b9f9c647907", "b1883f391e2b7a4ca99a969fe3189a63523d03d1469e67dda3aa03dc2479353a",
			"889b1dbbd4d74f4c5ff035936f74b32fd4dd3c221cc6c34a4238fd851891768f", "ea11befbabafa90a896a943f0b4e4d471961cb071f8185fb1167c05991677cc7",
			"1e1d5a39d5636b6d016b11332a76454779db8399edeef38d98194da467a271df", "6acc760ac5476895e8d87901f679f7f7279239811cc18f921f9b11490f5b8a19"};
	
	private static int requireNo = 0;
	
	public SearchProjectByName() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String savePath = "E:\\gitinsight\\data\\openhub\\projects\\";
		File saveFile = new File(savePath);
		if(!saveFile.exists()) {
			saveFile.mkdirs();
		}
		
		String sql = "SELECT distinct p.name FROM insightdb.git_projects p";
		String[] reColsName = {"name"};
		List<Map<String, Object>> rsList = DBUtil.getTableData(sql, null, reColsName);
		LOG.info("request size:" + rsList.size());
		
		for(Map<String, Object> rsMap : rsList){
			try {
				String projectName = (String) rsMap.get("name");
				File f = new File(savePath + projectName + ".xml");
				if(f.exists()){
					continue;
				}
				
				String api_key = getToken();
				if(api_key == null) {
					break;
				}
				
				HtmlUtil.requestPageByGet("https://www.openhub.net/projects.xml?query=" + projectName + "&api_key=" + api_key,
						savePath + projectName + ".xml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getToken(){
		int no = (int)(requireNo++/1000);
		if(no>=API_KEY.length){
			no = 0;
			requireNo = 0;
			
			return null;
		}
		return API_KEY[no];
	}
}