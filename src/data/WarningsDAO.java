/**
 * Data Access Ojbect : Warning
 * Performs database operations for the Warning object
 */

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
	
	/* Adds a Warning to the table 'warnings' (end can be null) */
	public void add(Warning warning){
		DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		ContentValues cv = new ContentValues();
		cv.put("id_gas", warning.getId_gas());
		cv.put("start", df.format(warning.getStart()));
		if(warning.getEnd() == null){
			cv.put("end", "null");
		}
		else{
			cv.put("end", df.format(warning.getEnd()));
		}
		db.insert("warnings", null, cv);
	}
	
	/* Sets the end date of a specific Warning */
	public void endWarning(int id_gas, Date end){
		DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		ContentValues cv = new ContentValues();
		cv.put("end", df.format(end));
		db.update("warnings", cv, "id_gas = ? AND end = ?", new String[]{Integer.toString(id_gas),"null"});
	}
	
	/* Selects a Warning with a specific id and a null end date */
	public Date selectWarningStart(int id_gas){
		DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		Date start;
		Cursor cursor = db.rawQuery("SELECT start FROM warnings WHERE id_gas = ? AND end = ?", new String [] {Integer.toString(id_gas), "null"});
		cursor.moveToFirst();
		try{
			start = df.parse(cursor.getString(0));
			return start;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/* Selects all Warnings */
	public Vector<Warning> selectAll(){
		try{
			Cursor cursor = db.rawQuery("SELECT * FROM warnings", null);
			DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
			Vector<Warning> warnings = new Vector<Warning>();
			cursor.moveToFirst();
			for(int i=0; i < cursor.getCount(); i++){
				Date end = null;
				String end_s = cursor.getString(2);
				if(!end_s.equals("null")){
					end = df.parse(end_s);
				}
				warnings.add(new Warning(cursor.getInt(0), df.parse(cursor.getString(1)), end));
				cursor.moveToNext();
			}
			cursor.close();
			return warnings;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
