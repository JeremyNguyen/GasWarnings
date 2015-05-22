package data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WarningsDAO {
	
	private SQLiteDatabase db = null;
	private SQLiteOpenHelper helper;
		
	public WarningsDAO(Context context){
		this.helper = new SQLiteHelper(context);
	}
		
	public SQLiteDatabase open(){
		db = helper.getWritableDatabase();
		return db;
	}
		
	public void close(){
		db.close();
	}
		
	public void reset(){
		helper.onUpgrade(db, 0, 0);
	}
	
	public SQLiteDatabase getDb(){
		return db;
	}
		
	public void create(){
		db.execSQL(SQLiteHelper.WARNINGS_TABLE_CREATE);
	}
	
	public void drop(){
		db.execSQL(SQLiteHelper.WARNINGS_TABLE_DROP);
	}
		
	public void add(Warning warning){
		DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		ContentValues cv = new ContentValues();
		cv.put("start", df.format(warning.getStart()));
		if(warning.getEnd() == null){
			cv.put("end", "null");
		}
		else{
			cv.put("end", df.format(warning.getEnd()));
		}
		if(db == null){
			Log.d("NULL","AGAIN");
		}
		db.insert("warnings", null, cv);
	}
	
	public void update(Date end){
		DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		ContentValues cv = new ContentValues();
		cv.put("end", df.format(end));
		db.update("warnings", cv, "end = ?", new String[]{"null"});
	}
	
	public Vector<Warning> selectAll(){
		try{
			Cursor cursor = db.rawQuery("SELECT * FROM warnings", null);
			DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
			Vector<Warning> warnings = new Vector<Warning>();
			cursor.moveToFirst();
			for(int i=0; i < cursor.getCount(); i++){
				Date end = null;
				String end_s = cursor.getString(1);
				if(!end_s.equals("null")){
					end = df.parse(end_s);
				}
				warnings.add(new Warning(df.parse(cursor.getString(0)), end));
				cursor.moveToNext();
			}
			cursor.close();
			return warnings;
		}
		catch(Exception e){
			e.printStackTrace();
			Log.d("DB","no such tables: warnings");
			return null;
		}
	}
}
