package com.hope.commonsense;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.Video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class DBAdapter {
	
	String DATABASE_NAME = "commonsense.db";
	int DATABASE_VERSION = 1;
	String TABLE_LIKES = "liketbl";

	String KEY_LIKE_ID = "_id";
	String KEY_LIKE_TYPE = "type";
	String KEY_LIKE_STATE = "state";
	String KEY_LIKE_TITLE = "title";
	String KEY_LIKE_CONTENT = "content";
	String KEY_LIKE_IMAGE = "image";

	int TYPE_QUESTION = 1;
	int TYPE_ARTICLE = 2;
	int TYPE_VIDEO = 3;

	String CREATE_TABLE_LIKE = "CREATE TABLE "+ TABLE_LIKES 		+" ("+
												KEY_LIKE_ID  	 		+" INTEGER, "+
												KEY_LIKE_TYPE		 	+" INTEGER,"+
												KEY_LIKE_STATE	 		+" INTEGER,"+
												KEY_LIKE_TITLE	 		+" TEXT,"+
												KEY_LIKE_CONTENT	 	+" TEXT,"+
												KEY_LIKE_IMAGE	 		+" TEXT"+
																	" )";


	String DATABASE_UPGRADE_LIKE = "DROP TABLE IF EXISTS "+ TABLE_LIKES;


	Context context;
	DatabaseHelper DBHelper;
	SQLiteDatabase db;
	SharedPreferences prefs;
	
// -------------------------------------------------------------------------------------------------
	public DBAdapter(Context context) {
		this.context = context;
		DBHelper = new DatabaseHelper(context);
	}

	private class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				Log.e("DBAdapter", "Creating database...");
				db.execSQL(CREATE_TABLE_LIKE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.e("DBAdapter", "Upgrading database from version " + oldVersion + " to " + newVersion);
			db.execSQL(DATABASE_UPGRADE_LIKE);
			onCreate(db);
		}
	}
	
	public DBAdapter open() {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		DBHelper.close();
	}



// -------------------------------------------------------------------------------------------------

	public void setLike(Object object, boolean isLiked){
		try{
			if(isLiked) {
				insertNewItem(object);
			} else {
				removeItem(object);
			}
		} catch (Exception ex){
		}
	}

	private void insertNewItem(Object object){
		if(object instanceof Article){
			insertNewItem((Article)object);
		} else if(object instanceof Question){
			insertNewItem((Question) object);
		} else {
			insertNewItem((Video)object);
		}
	}




	private void insertNewItem(Question question) {
		Log.e("db","insert -> question");
		ContentValues values = new ContentValues();
		values.put(KEY_LIKE_TYPE, TYPE_QUESTION);
		values.put(KEY_LIKE_ID, question.getId());
		values.put(KEY_LIKE_TITLE, question.getTitle());
		values.put(KEY_LIKE_CONTENT, question.getAnswer());
		values.put(KEY_LIKE_IMAGE, "");
		db.insert(TABLE_LIKES, null, values);
		Log.e("db","insert -> done");
	}

	private void insertNewItem(Article article) {
		ContentValues values = new ContentValues();
		values.put(KEY_LIKE_TYPE, TYPE_ARTICLE);
		values.put(KEY_LIKE_ID, article.getId());
		values.put(KEY_LIKE_TITLE, article.getTitle());
		values.put(KEY_LIKE_CONTENT, article.getContent());
		values.put(KEY_LIKE_IMAGE, article.getImagePath());
		db.insert(TABLE_LIKES, null, values);
	}

	private void insertNewItem(Video video) {
		ContentValues values = new ContentValues();
		values.put(KEY_LIKE_TYPE, TYPE_VIDEO);
		values.put(KEY_LIKE_ID, video.getId());
		values.put(KEY_LIKE_TITLE, video.getTitle());
		values.put(KEY_LIKE_CONTENT, "");
		values.put(KEY_LIKE_IMAGE, video.getImage());
		db.insert(TABLE_LIKES, null, values);
	}

	private void removeItem(Object object){
		int id;
		if(object instanceof Article){
			id = ((Article) object).getId();
		} else if(object instanceof Question) {
			id = ((Question) object).getId();
		} else {
			id = ((Video) object).getId();
		}
		removeItem(id);
	}

	private void removeItem(int id){
		db.delete(TABLE_LIKES, KEY_LIKE_ID + "=" + id+" AND "+KEY_LIKE_TYPE+"="+TYPE_ARTICLE, null);
	}


	public boolean isLiked(Object object){
		int type;
		int id;
		if(object instanceof Article){
			type = TYPE_ARTICLE;
			id = ((Article) object).getId();

		} else if(object instanceof Video){
			type = TYPE_VIDEO;
			id = ((Video) object).getId();
		}
		else {
			type = TYPE_QUESTION;
			id = ((Question)object).getId();
		}

//		Log.e("db", "where: "+KEY_LIKE_TYPE+"="+type+" AND "+KEY_LIKE_ID+"="+id);
		Cursor c = db.query(TABLE_LIKES, new String[]{KEY_LIKE_ID}, KEY_LIKE_TYPE+"="+type+" AND "+KEY_LIKE_ID+"="+id, null, null, null, null,null);
		c.moveToNext();
		return c.getCount()>0?true:false;
	}


}
