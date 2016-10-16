package com.gitinsight.github.projects;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.gitinsight.util.DBUtil;
import com.gitinsight.util.HtmlUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrgsAndProjectsImport {
	
	public static Logger LOG = Logger.getLogger(OrgsAndProjectsImport.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//从github抓取组织信息
//		spiderOrgsInfo(new File("D:\\gitinsight\\doc\\orgs\\orgs-urls.txt"), "D:\\gitinsight\\doc\\orgs\\datas");
		
		//导入组织信息
//		importOrgs("D:\\gitinsight\\doc\\orgs\\datas");
		
		//从github抓取项目信息
//		spiderProjectsInfo("D:\\gitinsight\\doc\\orgs\\orgs-info.txt", "D:\\gitinsight\\doc\\orgs\\projects");
		
		//导入项目信息
//		importProjects("D:\\gitinsight\\doc\\orgs\\projects");
		
		String savePath = "E:\\opensource\\github\\data\\projects\\";
		File pfiles = new File(savePath);
		
		File[] languageFiles = pfiles.listFiles();
		for(File lFile:languageFiles) {
			File[] jsonFiles = lFile.listFiles();
			for(File jsonFile:jsonFiles) {
				try {
					List<String> lines = FileUtils.readLines(jsonFile);
					JSONObject tjson = JSONObject.fromObject(lines.get(0));
					JSONArray projects = tjson.getJSONArray("items");
					for(int i=0; i<projects.size(); i++){
						try{
							JSONObject json = projects.getJSONObject(i);
							importProject(json);
						} catch(Exception e1) {
							e1.printStackTrace();
							LOG.error("Exception", e1);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOG.error("Exception", e);
				}
			}
		}
	}
	
	public static void spiderOrgsInfo(File directfile, String savePath) {
		try {
			List<String> lines = FileUtils.readLines(directfile, "UTF-8");
			for(String l:lines) {
				String login = l.replace("https://github.com/", "");
				HtmlUtil.requestPageByGet("https://api.github.com/orgs/" + login, savePath + File.separator + login);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void spiderOrgsInfo(String login, String savePath) {
		try {
			login = login.replace("https://github.com/", "");
			HtmlUtil.requestPageByGet("https://api.github.com/orgs/" + login, savePath + File.separator + login);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void importOrgs(String directFilePath) {
		File directFile = new File(directFilePath);
		File[] files = directFile.listFiles();
		
		String inertSQL = "insert into git_organizations(login, git_id, url, repos_url, events_url, members_url, public_members_url, avatar_url, description, name, company, blog, location, email, public_repos, public_gists, followers, following, html_url, created_at, updated_at, type)" 
				+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		for(File f:files) {
			try {
				String jsonStr = FileUtils.readFileToString(f);
				JSONObject json = JSONObject.fromObject(jsonStr);
				
				List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
				Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap5 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap6 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap7 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap8 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap9 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap10 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap11 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap12 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap13 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap14 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap15 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap16 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap17 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap18 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap19 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap20 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap21 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap22 = new HashMap<Object, Integer>();
				
				pmap1.put(json.getString("login"), DBUtil.P_STRING);
				pmap2.put(json.getInt("id"), DBUtil.P_STRING);
				pmap3.put(json.getString("url"), DBUtil.P_STRING);
				pmap4.put(json.getString("repos_url"), DBUtil.P_STRING);
				pmap5.put(json.getString("events_url"), DBUtil.P_STRING);
				pmap6.put(json.getString("members_url"), DBUtil.P_STRING);
				pmap7.put(json.getString("public_members_url"), DBUtil.P_STRING);
				pmap8.put(json.getString("avatar_url"), DBUtil.P_STRING);
				pmap9.put(getString(json.getString("description")), DBUtil.P_STRING);
				pmap10.put(json.getString("name"), DBUtil.P_STRING);
				pmap11.put(getString(json.getString("company")), DBUtil.P_STRING);
				pmap12.put(getString(json.getString("blog")), DBUtil.P_STRING);
				pmap13.put(getString(json.getString("location")), DBUtil.P_STRING);
				pmap14.put(getString(json.getString("email")), DBUtil.P_STRING);
				pmap15.put(json.getInt("public_repos"), DBUtil.P_STRING);
				pmap16.put(json.getInt("public_gists"), DBUtil.P_STRING);
				pmap17.put(json.getInt("followers"), DBUtil.P_STRING);
				pmap18.put(json.getInt("following"), DBUtil.P_STRING);
				pmap19.put(json.getString("html_url"), DBUtil.P_STRING);
				pmap20.put(json.getString("created_at"), DBUtil.P_STRING);
				pmap21.put(json.getString("updated_at"), DBUtil.P_STRING);
				pmap22.put(json.getString("type"), DBUtil.P_STRING);
				
				insertParams.add(pmap1);
				insertParams.add(pmap2);
				insertParams.add(pmap3);
				insertParams.add(pmap4);
				insertParams.add(pmap5);
				insertParams.add(pmap6);
				insertParams.add(pmap7);
				insertParams.add(pmap8);
				insertParams.add(pmap9);
				insertParams.add(pmap10);
				insertParams.add(pmap11);
				insertParams.add(pmap12);
				insertParams.add(pmap13);
				insertParams.add(pmap14);
				insertParams.add(pmap15);
				insertParams.add(pmap16);
				insertParams.add(pmap17);
				insertParams.add(pmap18);
				insertParams.add(pmap19);
				insertParams.add(pmap20);
				insertParams.add(pmap21);
				insertParams.add(pmap22);
				
				
				DBUtil.insertTableData(inertSQL, insertParams);
				
				LOG.info("Create organization :" + json.getString("name") + " success!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static void spiderProjectsInfo(String orgUrlsFilePath, String savePath) {
		try {
			List<String> lines = FileUtils.readLines(new File(orgUrlsFilePath));
			for(int i=0;i<lines.size();i++) {
				String l = lines.get(i);
				if(l.trim().length()<=0) {
					i++;
					continue;
				}
				String[] tmps = l.split("\\|");
				if(tmps.length<=1){
					continue;
				}
				
				String login = tmps[1];
				HtmlUtil.requestPageByGet("https://api.github.com/repos" + login + "?access_token=339bcbdb52fb8ba81fcc4352a393a0acd064b566", savePath + File.separator + login.substring(1,  login.length()).replace("/", "--"));
				
				Thread.sleep(3000);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//从git clone下来的项目文件夹中抽取项目url，格式为：docker/docker，暂无使用本方法
	public static void getProjectsUrl(String projecSavePath, String saveUrlPath) {
		File directFile = new File(projecSavePath);
		File[] projectFiles = directFile.listFiles();
		for(File projectFile:projectFiles){
			File configFile = new File(projectFile.getPath() + File.separator + ".git" + File.separator + "config");
			if(configFile.exists()) {
				List<String> lines;
				try {
					lines = FileUtils.readLines(configFile);
					for(String l : lines) {
						if(l.startsWith("url =")){
							String url = l.substring(l.indexOf("=")+1).trim();
							url = url.replace("https://github.com/", "");
							url = url.substring(0, url.lastIndexOf(".git"));
							FileUtils.write(new File(saveUrlPath), url + "\r\n", true);
							break;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				LOG.error("Project's config file not found! Please git clone again!");
			}
		}
	}
	
	public static void importProject(JSONObject json) {
		String inertSQL = "insert into git_projects(git_id, name, full_name, owner_id, private, html_url, description, fork, url, git_url, ssh_url, clone_url, svn_url, homepage, created_at, updated_at, pushed_at, size, stargazers_count, watchers_count, language, has_issues, has_downloads, has_wiki, has_pages, forks_count, mirror_url, forks, open_issues, watchers, default_branch, network_count, subscribers_count)" 
				+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
		Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap5 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap6 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap7 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap8 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap9 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap10 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap11 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap12 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap13 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap14 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap15 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap16 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap17 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap18 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap19 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap20 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap21 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap22 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap23 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap24 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap25 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap26 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap27 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap28 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap29 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap30 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap31 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap32 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap33 = new HashMap<Object, Integer>();
		Map<Object, Integer> pmap34 = new HashMap<Object, Integer>();
		
		pmap1.put(json.getInt("id"), DBUtil.P_STRING);
		pmap2.put(json.getString("name"), DBUtil.P_STRING);
		pmap3.put(json.getString("full_name"), DBUtil.P_STRING);
		pmap4.put(json.getJSONObject("owner").get("id"), DBUtil.P_STRING);
		pmap6.put((json.getBoolean("private")?1:0), DBUtil.P_INT);
		pmap7.put(json.getString("html_url"), DBUtil.P_STRING);
		pmap8.put(getString(json.getString("description")), DBUtil.P_STRING);
		pmap9.put((json.getBoolean("fork")?1:0), DBUtil.P_INT);
		pmap10.put(json.getString("url"), DBUtil.P_STRING);
		pmap11.put(getString(json.getString("git_url")), DBUtil.P_STRING);
		pmap12.put(getString(json.getString("ssh_url")), DBUtil.P_STRING);
		pmap13.put(getString(json.getString("clone_url")), DBUtil.P_STRING);
		pmap14.put(getString(json.getString("svn_url")), DBUtil.P_STRING);
		pmap15.put(getString(json.getString("homepage")), DBUtil.P_STRING);
		pmap16.put(json.getString("created_at"), DBUtil.P_STRING);
		pmap17.put(json.getString("updated_at"), DBUtil.P_STRING);
		pmap18.put(json.getString("pushed_at"), DBUtil.P_STRING);
		pmap19.put(json.getInt("size"), DBUtil.P_INT);
		pmap20.put(json.getInt("stargazers_count"), DBUtil.P_INT);
		pmap21.put(json.getInt("watchers_count"), DBUtil.P_INT);
		pmap22.put(json.getString("language"), DBUtil.P_STRING);
		pmap23.put((json.getBoolean("has_issues")?1:0), DBUtil.P_INT);
		pmap24.put((json.getBoolean("has_downloads")?1:0), DBUtil.P_INT);
		pmap25.put((json.getBoolean("has_wiki")?1:0), DBUtil.P_INT);
		pmap26.put((json.getBoolean("has_pages")?1:0), DBUtil.P_INT);
		pmap27.put(json.getInt("forks_count"), DBUtil.P_INT);
		pmap28.put(getString(json.getString("mirror_url")), DBUtil.P_STRING);
		pmap29.put(json.getInt("forks"), DBUtil.P_INT);
		pmap30.put(json.getInt("open_issues"), DBUtil.P_INT);
		pmap31.put(json.getInt("watchers"), DBUtil.P_INT);
		pmap32.put(json.getString("default_branch"), DBUtil.P_STRING);
		pmap33.put(json.optInt("network_count"), DBUtil.P_INT);
		pmap34.put(json.optInt("subscribers_count"), DBUtil.P_INT);
		
		insertParams.add(pmap1);
		insertParams.add(pmap2);
		insertParams.add(pmap3);
		insertParams.add(pmap4);
//		insertParams.add(pmap5);
		insertParams.add(pmap6);
		insertParams.add(pmap7);
		insertParams.add(pmap8);
		insertParams.add(pmap9);
		insertParams.add(pmap10);
		insertParams.add(pmap11);
		insertParams.add(pmap12);
		insertParams.add(pmap13);
		insertParams.add(pmap14);
		insertParams.add(pmap15);
		insertParams.add(pmap16);
		insertParams.add(pmap17);
		insertParams.add(pmap18);
		insertParams.add(pmap19);
		insertParams.add(pmap20);
		insertParams.add(pmap21);
		insertParams.add(pmap22);
		insertParams.add(pmap23);
		insertParams.add(pmap24);
		insertParams.add(pmap25);
		insertParams.add(pmap26);
		insertParams.add(pmap27);
		insertParams.add(pmap28);
		insertParams.add(pmap29);
		insertParams.add(pmap30);
		insertParams.add(pmap31);
		insertParams.add(pmap32);
		insertParams.add(pmap33);
		insertParams.add(pmap34);
		
		
		
		DBUtil.insertTableData(inertSQL, insertParams);
		
		LOG.info("Create project :" + json.getString("full_name") + " success!");
	}
	
	public static void importProjects(String directFilePath) {
		File directFile = new File(directFilePath);
		File[] files = directFile.listFiles();
		
		String inertSQL = "insert into git_projects(git_id, name, full_name, owner_id, organization_id, private, html_url, description, fork, url, git_url, ssh_url, clone_url, svn_url, homepage, created_at, updated_at, pushed_at, size, stargazers_count, watchers_count, language, has_issues, has_downloads, has_wiki, has_pages, forks_count, mirror_url, forks, open_issues, watchers, default_branch, network_count, subscribers_count)" 
				+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		for(File f:files) {
			try {
				String jsonStr = FileUtils.readFileToString(f);
				JSONObject json = JSONObject.fromObject(jsonStr);
				
				String owner_login = json.getJSONObject("owner").getString("login");
				String org_login = json.getJSONObject("organization").getString("login");
				
				int owner_id = isOrgExistInDB(owner_login);
				int org_id = isOrgExistInDB(org_login);

				if(owner_id<0 || org_id<0){
					if(owner_id<0){
						LOG.warn("Not found org:" + owner_login + "while import project:" + json.getString("full_name"));
					}

					if(org_id<0){
						LOG.warn("Not found org:" + org_login + "while import project:" + json.getString("full_name"));
					}
					
					continue;
				}
				
				List<Map<Object, Integer>> insertParams = new ArrayList<Map<Object, Integer>>();
				Map<Object, Integer> pmap1 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap2 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap3 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap4 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap5 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap6 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap7 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap8 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap9 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap10 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap11 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap12 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap13 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap14 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap15 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap16 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap17 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap18 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap19 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap20 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap21 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap22 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap23 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap24 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap25 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap26 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap27 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap28 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap29 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap30 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap31 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap32 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap33 = new HashMap<Object, Integer>();
				Map<Object, Integer> pmap34 = new HashMap<Object, Integer>();
				
				pmap1.put(json.getInt("id"), DBUtil.P_STRING);
				pmap2.put(json.getString("name"), DBUtil.P_STRING);
				pmap3.put(json.getString("full_name"), DBUtil.P_STRING);
				pmap4.put(owner_id, DBUtil.P_INT);
				pmap5.put(org_id, DBUtil.P_INT);
				pmap6.put((json.getBoolean("private")?1:0), DBUtil.P_INT);
				pmap7.put(json.getString("html_url"), DBUtil.P_STRING);
				pmap8.put(getString(json.getString("description")), DBUtil.P_STRING);
				pmap9.put((json.getBoolean("fork")?1:0), DBUtil.P_INT);
				pmap10.put(json.getString("url"), DBUtil.P_STRING);
				pmap11.put(getString(json.getString("git_url")), DBUtil.P_STRING);
				pmap12.put(getString(json.getString("ssh_url")), DBUtil.P_STRING);
				pmap13.put(getString(json.getString("clone_url")), DBUtil.P_STRING);
				pmap14.put(getString(json.getString("svn_url")), DBUtil.P_STRING);
				pmap15.put(getString(json.getString("homepage")), DBUtil.P_STRING);
				pmap16.put(json.getString("created_at"), DBUtil.P_STRING);
				pmap17.put(json.getString("updated_at"), DBUtil.P_STRING);
				pmap18.put(json.getString("pushed_at"), DBUtil.P_STRING);
				pmap19.put(json.getInt("size"), DBUtil.P_INT);
				pmap20.put(json.getInt("stargazers_count"), DBUtil.P_INT);
				pmap21.put(json.getInt("watchers_count"), DBUtil.P_INT);
				pmap22.put(json.getString("language"), DBUtil.P_STRING);
				pmap23.put((json.getBoolean("has_issues")?1:0), DBUtil.P_INT);
				pmap24.put((json.getBoolean("has_downloads")?1:0), DBUtil.P_INT);
				pmap25.put((json.getBoolean("has_wiki")?1:0), DBUtil.P_INT);
				pmap26.put((json.getBoolean("has_pages")?1:0), DBUtil.P_INT);
				pmap27.put(json.getInt("forks_count"), DBUtil.P_INT);
				pmap28.put(getString(json.getString("mirror_url")), DBUtil.P_STRING);
				pmap29.put(json.getInt("forks"), DBUtil.P_INT);
				pmap30.put(json.getInt("open_issues"), DBUtil.P_INT);
				pmap31.put(json.getInt("watchers"), DBUtil.P_INT);
				pmap32.put(json.getString("default_branch"), DBUtil.P_STRING);
				pmap33.put(json.getInt("network_count"), DBUtil.P_INT);
				pmap34.put(json.getInt("subscribers_count"), DBUtil.P_INT);
				
				insertParams.add(pmap1);
				insertParams.add(pmap2);
				insertParams.add(pmap3);
				insertParams.add(pmap4);
				insertParams.add(pmap5);
				insertParams.add(pmap6);
				insertParams.add(pmap7);
				insertParams.add(pmap8);
				insertParams.add(pmap9);
				insertParams.add(pmap10);
				insertParams.add(pmap11);
				insertParams.add(pmap12);
				insertParams.add(pmap13);
				insertParams.add(pmap14);
				insertParams.add(pmap15);
				insertParams.add(pmap16);
				insertParams.add(pmap17);
				insertParams.add(pmap18);
				insertParams.add(pmap19);
				insertParams.add(pmap20);
				insertParams.add(pmap21);
				insertParams.add(pmap22);
				insertParams.add(pmap23);
				insertParams.add(pmap24);
				insertParams.add(pmap25);
				insertParams.add(pmap26);
				insertParams.add(pmap27);
				insertParams.add(pmap28);
				insertParams.add(pmap29);
				insertParams.add(pmap30);
				insertParams.add(pmap31);
				insertParams.add(pmap32);
				insertParams.add(pmap33);
				insertParams.add(pmap34);
				
				
				
				DBUtil.insertTableData(inertSQL, insertParams);
				
				LOG.info("Create project :" + json.getString("full_name") + " success!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static int isOrgExistInDB(String login) {
		int orgId = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String querySql = "select id from git_organizations where login=?";
			conn = DBUtil.openConnection();
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, login);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				orgId = rs.getInt(1);
			}
			LOG.debug("login:" + login + "'s count in table users_source is " + orgId);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(pstmt);
			DBUtil.closeConn(conn);
		}
		
		return orgId;//sort
	}
	
	public static String getString(String str) {
		if(str == null || str.equals(null)){
			return "";
		} else if(str.equalsIgnoreCase("null")) {
			return "";
		} else {
			return str;
		}
	}

}
