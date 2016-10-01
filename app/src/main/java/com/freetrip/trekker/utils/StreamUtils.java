package com.freetrip.trekker.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 读取文本流的工具类
 * @author mwp
 *
 */
public class StreamUtils {

	/**
	 * 将输入流里的内容读取为一个字符串
	 * @param stream 传入一个输入流
	 * @return 返回一个字符串
	 * @throws IOException  IO异常由调用方法处理
	 */
	public static String getStringFromStream(InputStream stream) throws IOException{
		//先将输入流里的内容写到一个字节数组输出流方便转为字符串
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len=stream.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		
		String text = new String(baos.toByteArray());
		//关闭流
		stream.close();
		baos.close();
		
		return text;
	}
	
	/**
	 * 将一个json数据输入流里的内容读取为一个JSON对象
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject getJsonFromStream(InputStream stream) throws IOException, JSONException{
		
		String text = getStringFromStream(stream);
		JSONObject root = new JSONObject(text);
		
		return root;
	}
	  
	  /**
	   * 带过期时间的json
	   * @param f 文件
	   * @return   当文件存在 并且时间没有过期时返回json 其他情况返回null
	   * @throws IOException
	   */
	public static String fileToStringTime(File f) throws IOException{
		  FileInputStream in=new FileInputStream(f);
		  BufferedReader br=new BufferedReader(new InputStreamReader(in));
		  String line = br.readLine();
		  long time = Long.parseLong(line);
		  if(System.currentTimeMillis()<time){
			  return readStream(br);
		  }
		  br.close();
		return null;
	  }
	  public static String  fileToString(File f) throws IOException{
		  FileInputStream in=new FileInputStream(f);
		  return getStringFromStream(in);
	  }
	
	private static String readStream(BufferedReader br) throws IOException {
		StringBuffer localjson=new StringBuffer();
		  String temp="";
		  while((temp=br.readLine())!=null){
			  localjson.append(temp);
		  }
		  br.close();
		return localjson.toString();
	}
	
}