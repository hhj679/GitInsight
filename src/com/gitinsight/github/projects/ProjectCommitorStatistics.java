package com.gitinsight.github.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gitinsight.util.DBUtil;

public class ProjectCommitorStatistics {
	public static Logger LOG = Logger.getLogger(ProjectCommitorStatistics.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		createProjectCommitorLinks("apache spark");
	}

	public static void createProjectCommitorLinks(String projectName) {
		String sql ="SELECT distinct g.projectid,g.author FROM gitlog_source g,projects p where g.projectid=p.id and p.name=?;";
		String[] reColsName = {"projectid", "author"};
		List<Map<Object, Integer>> params = new ArrayList<Map<Object, Integer>>();
		Map<Object, Integer> pName = new HashMap<Object, Integer>();
		pName.put(projectName, DBUtil.P_STRING);
		params.add(pName);
		
		List<Map<String, Object>> rsList = DBUtil.getTableData(sql, params, reColsName);
		LOG.debug("Project:" + projectName + "'s commitor count is " + rsList.size());
		
		String sql2 ="SELECT distinct id,company,location FROM users_source where email=?;";
		String[] reColsName2 = {"id", "company", "location"};
		
		String inertSQL = "";// "insert into project_commitor(projectId, userId, commitorName, commitorMail, company, companyType, location) values(?, ?, ?, ?, ?, ?, ?)";
		
		for(Map<String, Object> rmap:rsList) {
			int projectid = (int) rmap.get("projectid");
			String author = (String) rmap.get("author");
			String[] user = author.split("<");
			
			List<Map<Object, Integer>> params2 = new ArrayList<Map<Object, Integer>>();
			Map<Object, Integer> pName2 = new HashMap<Object, Integer>();
			pName2.put(user[1].replace(">", "").trim(), DBUtil.P_STRING);
			params2.add(pName2);
			
			List<Map<String, Object>> rsList2 = DBUtil.getTableData(sql2, params2, reColsName2);
			
			List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
			Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap5 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap6 = new HashMap<Object, Integer>();
			Map<Object, Integer> pmap7 = new HashMap<Object, Integer>();
			
			pmap1.put(projectid, DBUtil.P_INT);
			pmap3.put(user[0].trim(), DBUtil.P_STRING);
			pmap4.put(user[1].replace(">", "").trim(), DBUtil.P_STRING);
			
			insertParams.add(pmap1);
			
			if(rsList2.size()>0) {
				inertSQL = "insert into project_commitor(projectId, userId, commitorName, commitorMail, company, companyType, location) values(?, ?, ?, ?, ?, ?, ?)";
				Map<String, Object> rmap2 = rsList2.get(0);
				
				pmap2.put(rmap2.get("id"), DBUtil.P_INT);
				if(rmap2.get("company")!=null && rmap2.get("company").toString().trim().length()>0){
					pmap5.put(rmap2.get("company"), DBUtil.P_STRING);
					pmap6.put("github", DBUtil.P_STRING);
				} else {
					pmap5.put("", DBUtil.P_STRING);
					pmap6.put("", DBUtil.P_STRING);
				}
				pmap7.put(rmap2.get("location"), DBUtil.P_STRING);
				
				insertParams.add(pmap2);
				insertParams.add(pmap3);
				insertParams.add(pmap4);
				insertParams.add(pmap5);
				insertParams.add(pmap6);
				insertParams.add(pmap7);
			} else {
				inertSQL = "insert into project_commitor(projectId, commitorName, commitorMail) values(?, ?, ?)";
				
				insertParams.add(pmap3);
				insertParams.add(pmap4);
			}
			
			DBUtil.insertTableData(inertSQL, insertParams);
			
			LOG.info("Create project commitor links:" + author + " success!");
		}
	}
}
