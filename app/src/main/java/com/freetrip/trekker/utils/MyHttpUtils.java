package com.freetrip.trekker.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyHttpUtils {
	
	static long overTime=1000*6000;//单位毫秒
	
	/**
     * 该方法没有另启线程 注意自己起线程
     * @param url 地址   isCache 是否缓存  true 缓存
     * @return
     */
	public static String loadJson(String url,boolean isCache){
		String json;
    	File file=FileUtils.getCacheDir();
    	//本地获取
    	json =getJsonFromLocal(file,url);
    	
    	if(json==null){
    		// 网络获取 并写入本地缓存
    	//	System.out.println("从网络获取");
    		json=getJsonFromNet(file,url,isCache);
    	}
		return json;
    }
    
    public static String loadJson(String url){
		return loadJson(url, true);
    }

	public static String getJsonFromNet(File file, String url, boolean isCache) {
		try {
			URL u=new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.connect();
			if(conn.getResponseCode()==200){
				InputStream stream = conn.getInputStream();
				String json = StreamUtils.getStringFromStream(stream);
				//写入本地
				if(isCache){
				readJsonTOLocal(file,url,json);
				}
				return json;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
     //带过期时间的写入本地
	private static void readJsonTOLocal(File file, String url, String json,boolean istemp){
		String md5 = MD5Utils.encode(url);
		File f=new File(file, md5);
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(f));
			PrintStream ps=new PrintStream(f);
			if(istemp){
				// 当前默认过期时间为10分钟
				bw.write((System.currentTimeMillis()+overTime)+"");
				bw.newLine();
			}
			bw.write(json);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	// 直接写入本地不带过期时间
	private static void readJsonTOLocal(File file, String url, String json) {
		readJsonTOLocal(file, url, json, false);
	}
	// 直接读取
	private static String getJsonFromLocal(File file,String url) {
		String md5 = MD5Utils.encode(url);
		File f=new File(file, md5);
		if(f.exists()){
		try {
			String json = StreamUtils.fileToString(f);
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		return null;
	}
}
