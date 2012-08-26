package ydp.weightrecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "weight_db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "weight_info";
	public final static String FIELD_ID = "_id";
	public final static String FIELD_WEIGHT = "weight";
	public final static String FIELD_DATE = "date";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "Create table " + TABLE_NAME + "(" + FIELD_ID
				+ " integer primary key autoincrement not null," + FIELD_WEIGHT
				+ " float, " + FIELD_DATE + " date );";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				" date desc");
		return cursor;
	}
	
	public boolean isEmpty() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				" date desc");
		while(cursor.moveToNext()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}
	
	public long insert(String date, float weight) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_WEIGHT, weight);
		cv.put(FIELD_DATE, date);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}

	public long delete(String date) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_DATE + "=?";
		String[] whereValue = { date };
		long num = db.delete(TABLE_NAME, where, whereValue);
		return num;
	}

	public long update(String date, float weight) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_DATE + "=?";
		String[] whereValue = { date };
		ContentValues cv = new ContentValues();
		cv.put(FIELD_WEIGHT, weight);
		long num = db.update(TABLE_NAME, cv, where, whereValue);
		return num;
	}

	public String existData(String date) {
		String tempStr = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, FIELD_DATE + "=\"" + date + "\"", null, null, null,
				" _id desc");
		if(cursor.moveToNext()){
			tempStr = cursor.getString(1);
		}
		cursor.close();
		return tempStr;
	}
	
	public int dataCount() {
		int num = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				" date desc");
		num = cursor.getCount();
		cursor.close();
		return num;
	}
	
	public int deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_NAME, null, null);
	}
	
	public float getMaxWeight() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{"max(\"" + FIELD_WEIGHT + "\")"}, null, null, null, null,
				null);
		float f = -1;
		while(cursor.moveToNext()) {
			f = Float.valueOf(cursor.getString(0));
		}
		cursor.close();
		return f;
	}
	
	public float getMinWeight() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{"min(\"" + FIELD_WEIGHT + "\")"}, null, null, null, null,
				null);
		float f = -1;
		while(cursor.moveToNext()) {
			f = Float.valueOf(cursor.getString(0));
		}
		cursor.close();
		return f;
	}
	public String getMaxDate() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{"max(\"" + FIELD_DATE + "\")"}, null, null, null, null,
				null);
		String str = null;
		while(cursor.moveToNext()) {
			str = cursor.getString(0);
		}
		cursor.close();
		return str;
	}
	
	public String getMinDate() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{"min(\"" + FIELD_DATE + "\")"}, null, null, null, null,
				null);
		String str = null;
		while(cursor.moveToNext()) {
			str = cursor.getString(0);
		}
		cursor.close();
		return str;
	}
	
	public float calMonthWeight(String startDate, String endDate) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, FIELD_DATE + ">=\""
				+ startDate + "\"" + " and " + FIELD_DATE + "<\"" + endDate
				+ "\"", null, null, null, null);
		float f = -1;
		float avg = -1, sum = 0;
		
		while(cursor.moveToNext()) {
			f = Float.valueOf(cursor.getString(1));
			sum += f;			
		}
		avg = sum / cursor.getCount();
		avg = (float)(Math.round(avg * 10)) / 10f;
		cursor.close();
		return avg;
	}
	
	public float getMonthWeigh(int year, int month) {
		String sDate = null;
		String eDate = null;
		sDate = year + "/" + month + "/01";
		if(month == 12) {
			eDate = (year + 1) + "/01/01"; 
		}else {
			eDate = year + "/" + (month + 1) + "/01"; 
		}
		float avgWeight = calMonthWeight(sDate, eDate);
		return avgWeight;
	}
}
