package com.gitinsight.github.check;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class CheckRepoStars {

	public CheckRepoStars() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		String s = "2016-02-17T19:27:26Z";
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date d = formatter.parse(s);

		System.out.println("Formatted Date in current time zone = " + formatter.format(d));
	}
	
	public static void check(String filePath) {
		File pf = new File(filePath);
		File[] sfs = pf.listFiles();
		for(File sf : sfs) {
			File pageFile = new File(sf.getPath() + File.separator + "pages.txt");
			if(pageFile.exists()) {
				try {
					String pages = FileUtils.readFileToString(pageFile);
					int page = Integer.valueOf(pages);
					if(page+1 != sf.list().length){
						for(int p=1; p<=page; p++){
							File jsonFile = new File(sf.getPath() + File.separator + sf.getName() + "stars_" + p + ".json");
							if(!jsonFile.exists()){
								System.out.println("error at: " + jsonFile.getName());
								FileUtils.writeStringToFile(new File("E:\\opensource\\github\\data\\stars\\error.txt"), sf.getName().replace("_qqq;;;_", "/") + "\r\n", true);
								break;
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				try {
					FileUtils.writeStringToFile(new File("E:\\opensource\\github\\data\\stars\\error.txt"), sf.getName().replace("_qqq;;;_", "/") + "\r\n", true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				try {
//					String[] reInfos = HtmlUtil.requestPageByGetReLink("https://api.github.com/repos/" + sf.getName().replace("_qqq;;;_", "/") + "/stargazers?page=1&per_page=100&access_token=", null);
//					int lastPage = 1;
//					if(reInfos!=null && reInfos.length>1){
//						String link = reInfos[1];
//						lastPage = Integer.valueOf(RequestProjectStars.getLastPageByLink(link));
//					}
//					FileUtils.writeStringToFile(new File(sf.getPath() + File.separator + "pages.txt"), String.valueOf(lastPage));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
	}

}
