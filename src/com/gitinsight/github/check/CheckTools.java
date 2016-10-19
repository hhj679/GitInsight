package com.gitinsight.github.check;

import java.io.File;

import org.apache.log4j.Logger;

import com.gitinsight.github.api.RequestProjectStars;

public class CheckTools {
	public static Logger LOG = Logger.getLogger(CheckTools.class);
	public CheckTools() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = "E:\\opensource\\github\\data\\stars";
		checkCommon(filePath);
	}

	public static void checkCommon(String filePath){
		File pf = new File(filePath);
		File[] sfs = pf.listFiles();
		for(File sf : sfs) {
			if(sf.list().length==0){
				LOG.info("delete file:" + sf.getName());
				sf.delete();
			}
		}
	}
}
