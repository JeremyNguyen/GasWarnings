package data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EntryDAO {
	
	private SQLiteDatabase db = null;
	private SQLiteOpenHelper helper;
	
	public EntryDAO(Context context){
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
		db.execSQL(SQLiteHelper.ENTRY_TABLE_CREATE);
	}
	
	public void drop(){
		db.execSQL(SQLiteHelper.ENTRY_TABLE_DROP);
	}
	
	
	
	public void add(Entry entry){
		DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
		ContentValues cv = new ContentValues();
		cv.put("date", df.format(entry.getDate()));
		cv.put("id_temp", entry.getId_temp());
		cv.put("temperature", entry.getTemperature());
		cv.put("id_gas", entry.getId_gas());
		cv.put("gas", entry.getGas());
		db.insert("entries", null, cv);
	}
	
	/* Récupération du dernier taux de gaz en base */
	public float selectLastGas(int id_gas){
		Cursor cursor = db.rawQuery("SELECT MAX(date), gas FROM entries WHERE id_gas = ?", new String [] {Integer.toString(id_gas)});
		cursor.moveToFirst();
		try{
			float last_gas = cursor.getFloat(1);
			return last_gas;
		}
		catch(Exception e){
			return 0;
		}
	}
	
	public Vector<Entry> selectAll(){
		try{
			Cursor cursor = db.rawQuery("SELECT * FROM entries", null);
			DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
			Vector<Entry> entries = new Vector<Entry>();
			cursor.moveToFirst();
			for(int i=0; i < cursor.getCount(); i++){
				entries.add(new Entry(df.parse(cursor.getString(0)), cursor.getInt(1), cursor.getFloat(2), cursor.getInt(3), cursor.getFloat(4)));
				cursor.moveToNext();
			}
			cursor.close();
			return entries;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
