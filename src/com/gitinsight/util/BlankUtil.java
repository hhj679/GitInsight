package com.gitinsight.util;

public class BlankUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String getString(String str, boolean isTrim) {
		if(str == null || str.equals(null)){
			return "";
		} else {
			if(isTrim){
				return str.trim();
			} else {
				return str;
			}
		}
	}
	
	public static String getString(String str) {
		return getString(str, false);
	}

	public static String getObject(Object obj) {
		if(obj == null){
			return "";
		} else {
			return obj.toString();
		}
	}
	
	public static boolean isBlank(String str) {
		if(str==null || str.trim().length()<=0){
			return true;
		} else {
			return false;
		}
	}
}
