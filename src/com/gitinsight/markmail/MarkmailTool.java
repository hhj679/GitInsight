package com.gitinsight.markmail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

public class MarkmailTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		getListMonthDate("D:\\用户目录\\Documents\\markmail", "D:\\gitinsight\\markmail\\mm\\markmail.org\\browse\\");
		
//		listsCountensURLXPath("D:\\gitinsight\\doc\\test", "D:\\gitinsight\\doc\\test\\");
		
		new Thread(){
			public void run(){
				listsCountensURLXPath("D:\\gitinsight\\doc\\markmailall", "D:\\gitinsight\\doc\\markmail-urls2\\");
			}
		}.start();
		
		new Thread(){
			public void run(){
				listsCountensURLXPath("D:\\gitinsight\\doc\\markmailall2", "D:\\gitinsight\\doc\\markmail-urls3\\");
			}
		}.start();
		
		new Thread(){
			public void run(){
				listsCountensURLXPath("D:\\gitinsight\\doc\\markmailall3", "D:\\gitinsight\\doc\\markmail-urls4\\");
			}
		}.start();
	}

	public static void getListMonthDate(String filePath, String savePath){
		File fileDirectory = new File(filePath);
		File[] files = fileDirectory.listFiles();
		int fileNum = 0;
		int hrefCount = 0;
		for(File file:files){
			HtmlCleaner cleaner = new  HtmlCleaner();   
			try {
				TagNode node = cleaner.clean(file, "UTF-8");
				//country
				Object[] hrefNs = node.evaluateXPath("//div[@id='browse']/table/tbody/tr/td/a");
				Object[] countNs = node.evaluateXPath("//div[@id='browse']/table/tbody/tr/td[2]");
				
				for(int i=0;i<hrefNs.length;i++) {
					TagNode hrefNode = (TagNode) hrefNs[i];
					TagNode countNode = (TagNode) countNs[i];
					String href = hrefNode.getAttributeByName("tppabs");
//					href = href.substring(href.lastIndexOf("=")+1).replace("'", "");
					String count = countNode.getText().toString();
					//FileUtils.write(new File(savePath + "Months_" + fileNum + ".txt"), href + "\r\n", true);
					System.out.println(href + ":" + count);
					hrefCount ++;
					
					if(hrefCount>=10000){
						hrefCount = 0;
						fileNum ++;
					}
				}
			} catch (IOException | XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
	}
	
	public static String formatDate(String date) {
		if(date.contains("January")){
			date = date.replace("January", "-01").replace(" ", "");
		} else if(date.contains("February")){
			date = date.replace("February", "-02").replace(" ", "");
		} else if(date.contains("March")){
			date = date.replace("March", "-03").replace(" ", "");
		} else if(date.contains("April")){
			date = date.replace("April", "-04").replace(" ", "");
		} else if(date.contains("May")){
			date = date.replace("May", "-05").replace(" ", "");
		} else if(date.contains("June")){
			date = date.replace("June", "-06").replace(" ", "");
		} else if(date.contains("July")){
			date = date.replace("July", "-07").replace(" ", "");
		} else if(date.contains("August")){
			date = date.replace ("August", "-08").replace(" ", "");
		} else if(date.contains("September")){
			date = date.replace("September", "-09").replace(" ", "");
		} else if(date.contains("October")){
			date = date.replace("October", "-10").replace(" ", "");
		} else if(date.contains("November")){
			date = date.replace("November", "-11").replace(" ", "");
		} else if(date.contains("December")){
			date = date.replace("December", "-12").replace(" ", "");
		}
		
		return date;
	}
	
	public static void listsCountensURLXPath(String filesPath, String savePath) {
		File deFile = new File(filesPath);
//		LinkedList<File> files = (LinkedList<File>) Arrays.asList(deFile.listFiles());
		File[] files = deFile.listFiles();
		
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		Map<String, Integer> numMap = new HashMap<String, Integer>();
		Map<String, String> pageMap = new HashMap<String, String>();
		
		for(File f: files) {
			System.out.println(Thread.currentThread().getName() + ":" + "start " + f.getName());
			HtmlCleaner cleaner = new  HtmlCleaner();
			try {
				TagNode node = cleaner.clean(f);
				Object[] listNameNs = node.evaluateXPath("//div[@id='browse']/h2");
				Object[] pageNs = node.evaluateXPath("//div[@id='browse']/h4");
				Object[] countNs = node.evaluateXPath("//div[@id='browse']/h1");
				Object[] dateNs = node.evaluateXPath("//div[@id='browse']/h3");
				Object[] mailNs = node.evaluateXPath("//div[@id='browse']/table/tbody/tr/td/span[1]/a");
				Object[] authorNs = node.evaluateXPath("//div[@id='browse']/table/tbody/tr/td/span[2]");
				
				String listName = ((TagNode)listNameNs[0]).getText().toString();
				listName = listName.substring(0, listName.indexOf("[")).trim();
				
				String page = "1";
				if(pageNs != null && pageNs.length>0){
					page = ((TagNode)pageNs[0]).getText().toString();
					page = page.substring(0, page.indexOf("(")).replace("Page", "").trim();
				}
				
				String date = ((TagNode)dateNs[0]).getText().toString();
				date = date.substring(0, date.indexOf("[")).trim();
				date = formatDate(date);
				String count = ((TagNode)countNs[0]).getText().toString();
				count = count.replace("messages", "").replace("message", "").replace(",", "").trim();
				
				countMap.put(listName+date, Integer.valueOf(count));
				
				String spage = pageMap.get(listName+date);
				if(spage == null){
					spage = "";
				}
				spage += page+"ppp;;;";
				pageMap.put(listName+date, spage);
				
				Integer iNum = numMap.get(listName+date);
				if(iNum == null){
					iNum = 0;
				}
				
				for(int i=0;i<mailNs.length;i++) {
					
					String title = ((TagNode)mailNs[i]).getText().toString();
					String url = ((TagNode)mailNs[i]).getAttributeByName("tppabs");
					if(url==null || url.equalsIgnoreCase("null")){
						url = ((TagNode)mailNs[i]).getAttributeByName("href");
					}
					String author = ((TagNode)authorNs[i]).getText().toString();
					
					FileUtils.write(new File(savePath + listName + "-" + date + "-" + page), listName + "	" + date + "	" + title + "	" + author + "	" + url + "	" + f.getName() + " \r\n", true);
					
					iNum ++;
				}
				numMap.put(listName+date, iNum);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(f.getName());
			}
		}
		
		System.out.println(countMap);
		System.out.println(numMap);
		System.out.println(pageMap);
		System.out.println();
	}
}
