package com.freetrip.trekker.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectionDB extends SQLiteOpenHelper {

	public CollectionDB(Context context){
		this(context,"likedb",null,1);
	}
	
	public CollectionDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, "likedb", null, 1);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="create table liketable(_id int primary key,img varchar,text varchar,desc varchar,url varchar,praise int,star float);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
    
}
