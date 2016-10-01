package com.freetrip.trekker.utils;


import java.io.File;

import com.freetrip.trekker.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.cache.FileNameGenerator;

import android.view.View;

public class MyBitmapUtils {
	
	
    public static void loadBitmap(View v,final String url){
		// �ڶ������� ����ͼƬ��·�� // ����ͼƬ ������Ķ��ٱ������ڴ� 0.05-0.8f
    	BitmapUtils bitmaputils=new BitmapUtils(UIUtils.getContext(),
    			FileUtils.getIconDir().getAbsolutePath(), 0.3f);
    	bitmaputils.configDefaultLoadFailedImage(R.drawable.default_pic);
    	bitmaputils.configDefaultLoadingImage(R.drawable.default_pic);
//    	bitmaputils.configDiskCacheFileNameGenerator(new FileNameGenerator() {
//			@Override
//			public String generate(String arg0) {
//				//System.out.println("========="+arg0);
//				return arg0+url;
//			}
//		});
//    	System.out.println((FileUtils.getIconDir().getAbsolutePath()+"/"+url));
//    	File bitmapFileFromDiskCache = bitmaputils.getBitmapFileFromDiskCache(FileUtils.getIconDir().getAbsolutePath()+"/"+url);
//    	
//    	if(bitmapFileFromDiskCache!=null&&bitmapFileFromDiskCache.exists()){
//    	    System.out.println("复用缓存");
//    		bitmaputils.display(v, bitmapFileFromDiskCache.getAbsolutePath());
//    	}else{
    	//v.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
    		bitmaputils.display(v, url);
    	//	System.out.println("网络下载");
    //	}
    	
    	
    }
}
