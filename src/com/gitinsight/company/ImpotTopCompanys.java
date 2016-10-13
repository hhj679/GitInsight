package com.gitinsight.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gitinsight.util.DBUtil;

public class ImpotTopCompanys {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//insertForbesTop2000(new File("D:\\gitinsight\\doc\\ForbesGlobal2000.txt"));
		insertFortuneTop500(new File("D:\\gitinsight\\doc\\FortuneGlobal500.txt"));
	}
	
	public static void insertFortuneTop500(File file) throws IOException{
		List<String> linesList = FileUtils.readLines(file, "UTF-8");
		for(String line:linesList) {
			String[] lines = line.split("	");
			String[] names = lines[2].split("（");
			String name = names[1].replace(")", "");
			String alias = names[0];
			
			//insert to table
			String inertSQL = "insert into organization(name, alias, country, rank, type) values(?, ?, ?, ?,'fortuneglobal500')";
			List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
			Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
			pmap1.put(name, DBUtil.P_STRING);
			pmap2.put(alias, DBUtil.P_STRING);
			pmap3.put(lines[5], DBUtil.P_STRING);
//			System.out.println(lines[0].substring(1));
			if(name.endsWith("WAL-MART STORES"))
				pmap4.put(Integer.parseInt(lines[0].substring(1)), DBUtil.P_INT);
			else
				pmap4.put(Integer.parseInt(lines[0]), DBUtil.P_INT);
			insertParams.add(pmap1);
			insertParams.add(pmap2);
			insertParams.add(pmap3);
			insertParams.add(pmap4);
			
			DBUtil.insertTableData(inertSQL, insertParams);
			
			System.out.println("insert===>" + lines[0] + ":" + name + ":" + alias + ":" + lines[5] + " success!");
		}
	}

	public static void insertForbesTop2000(File pageSourceFile) {
		try {
			HtmlCleaner cleaner = new  HtmlCleaner();   
			TagNode node = cleaner.clean(pageSourceFile, "UTF-8" );  

			//image
			Object[] imagesNs = node.evaluateXPath("//table[@id='the_list']/tbody[@id='list-table-body']/tr[@class='data']/td[@class='image']/a/img");
			
			//rank
			Object[] rankNs = node.evaluateXPath("//table[@id='the_list']/tbody[@id='list-table-body']/tr[@class='data']/td[@class='rank']");
			
			//name
			Object[] nameNs = node.evaluateXPath("//table[@id='the_list']/tbody[@id='list-table-body']/tr[@class='data']/td[@class='name']/a");
			
			//name
			Object[] countryNs = node.evaluateXPath("//table[@id='the_list']/tbody[@id='list-table-body']/tr[@class='data']/td[4]");
			
			for(int i=0;i<nameNs.length;i++) {
				TagNode nameNode = (TagNode) nameNs[i];
				TagNode rankNode = (TagNode) rankNs[i];
				TagNode imageNode = (TagNode) imagesNs[i];
				TagNode countryNode = (TagNode) countryNs[i];
				
				String name = nameNode.getText().toString();
				String rank = rankNode.getText().toString().replace("#", "");
				String imageUrl = imageNode.getAttributeByName("src");
				String country = countryNode.getText().toString();
				
				//insert to table
				String inertSQL = "insert into organization(name, icon, country, rank, type) values(?, ?, ?, ?,'forbesglobal2000')";
				List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
				Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
				pmap1.put(name, DBUtil.P_STRING);
				pmap2.put(imageUrl, DBUtil.P_STRING);
				pmap3.put(country, DBUtil.P_STRING);
				pmap4.put(Integer.valueOf(rank), DBUtil.P_INT);
				insertParams.add(pmap1);
				insertParams.add(pmap2);
				insertParams.add(pmap3);
				insertParams.add(pmap4);
				
				DBUtil.insertTableData(inertSQL, insertParams);
				
				System.out.println("insert===>" + rank + ":" + name + ":" + country + ":" + imageUrl + " success!");
			}
//			String pageSource = FileUtils.readFileToString(pageSourceFile);
			
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			Document document = builder.parse(pageSourceFile);
//
//			XPathFactory xFactory = XPathFactory.newInstance();
//			  XPath xpath = xFactory.newXPath();
//			  
//			  //image
//			  XPathExpression expr = xpath.compile("//table[@id='the_list']/tbody[@id='list-table-body']/tr[@class='data']/td[@class='image']/img");
//			  Object result = expr.evaluate(document, XPathConstants.NODESET);
//			  NodeList imageNodes = (NodeList) result;
//			  
//			  //rank
//			  expr = xpath.compile("//table[@id='the_list']/tbody[@id='list-table-body']/tr[@class='data']/td[@class='rank']");
//			  result = expr.evaluate(document, XPathConstants.NODESET);
//			  NodeList rankNodes = (NodeList) result;
//			  
//			  //name
//			  expr = xpath.compile("//table[@id='the_list']/tbody[@id='list-table-body']/tr[@class='data']/td[@class='name']/a");
//			  result = expr.evaluate(document, XPathConstants.NODESET);
//			  NodeList nameNodes = (NodeList) result;
//			  
//			  for(int i=0;i<imageNodes.getLength();i++) {
//				  Node nImage = imageNodes.item(i);
//				  Node nRank = rankNodes.item(i);
//				  Node nName = nameNodes.item(i);
//				  
//				  String imageUrl = nImage.getNodeValue();
//				  String rank = nRank.getNodeValue();
//				  String name = nName.getNodeValue();
//				  
//				  System.out.println(imageUrl + ":" + rank + ":" + name);
//			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
