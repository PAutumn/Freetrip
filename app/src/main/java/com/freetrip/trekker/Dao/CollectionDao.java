package com.freetrip.trekker.Dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.freetrip.trekker.DB.CollectionDB;
import com.freetrip.trekker.bean.CommonListBean;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.freetrip.trekker.utils.UIUtils;


public class CollectionDao {
	// 添加数据
	public void  addCollection(CommonInfo bean){
		CollectionDB db=new CollectionDB(UIUtils.getContext());
		SQLiteDatabase database = db.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("img", bean.img);
		values.put("text", bean.text);
		values.put("desc", bean.desc);
		values.put("url", bean.url);
		values.put("praise", bean.praise);
		values.put("star", bean.star);
		database.insert("liketable", null, values);
		db.close();
	}
	/**
	 * 根据 图片的url 删除
	 * @param img
	 */
	public void deleteCollection(String img){
		CollectionDB db=new CollectionDB(UIUtils.getContext());
		SQLiteDatabase database = db.getWritableDatabase();
		database.delete("liketable", "img=?", new String[]{img});
		db.close();
	}
	//img varchar,text varchar,desc varchar,url varchar,praise int,star float
	public List<CommonInfo> findAllCollection(){
		List<CommonInfo> list=new ArrayList<CommonInfo>();
		CollectionDB db=new CollectionDB(UIUtils.getContext());
		SQLiteDatabase database = db.getWritableDatabase();
		Cursor cursor = database.query("liketable", null, null, null, null, null, null);
		if(cursor!=null&&cursor.getCount()>0){
			while(cursor.moveToNext()){
				CommonListBean.CommonInfo info=new CommonListBean().new CommonInfo();
				info.img = cursor.getString(1);
				info.text = cursor.getString(2);
				info.desc = cursor.getString(3);
				info.url = cursor.getString(4);
				info.praise = cursor.getInt(5);
				info.star = cursor.getFloat(6);
				list.add(info);
			}
			cursor.close();
		}
		db.close();
		return list;
	}
	
	// 查询某条数据是否在数据库中
	public boolean findData(String img){
		CollectionDB db=new CollectionDB(UIUtils.getContext());
		SQLiteDatabase database = db.getWritableDatabase();
		Cursor cursor = database.query("liketable", null, "img=?", new String[]{img}, null, null, null);
		if(cursor!=null&&cursor.getCount()>0){
			return true;
		}
		return false;
	}
	
	
	
}
