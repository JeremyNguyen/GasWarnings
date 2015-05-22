package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{
	
	protected final static int VERSION = 1;
	protected final static String DB_NAME = "GasWarnings.db";
	
	public static final String ENTRY_TABLE_CREATE = "CREATE TABLE entries (date TEXT, temperature REAL, gas REAL);";
	public static final String ENTRY_TABLE_DROP = "DROP TABLE IF EXISTS entries;";
	
	public static final String WARNINGS_TABLE_CREATE = "CREATE TABLE warnings (start TEXT, end TEXT);";
	public static final String WARNINGS_TABLE_DROP = "DROP TABLE IF EXISTS warnings";
	
	public SQLiteHelper(Context context){
		super(context, DB_NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(ENTRY_TABLE_CREATE);
		db.execSQL(WARNINGS_TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		onCreate(db);
	}
	
}
