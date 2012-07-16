package voja.android.vault;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite extends SQLiteOpenHelper {

	public static final String DB_NAME = "vault";
	public static final int DB_VERSION = 1;
	public static final String DB_CREATE = "CREATE TABLE data(key VARCHAR(32) PRIMARY KEY NOT NULL, value VARCHAR(100) NOT NULL)";
	
	public SQLiteDatabase db;
	public Context context;
	
	public Sqlite(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		this.db.execSQL(DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
	}
	
	@Override
	public synchronized void close() {
		if(this.db != null) {
			this.db.close();
			super.close();
		}
	}
	
	public List<VaultItem> getAll() throws Exception {
		// load items
		List<VaultItem> items = new ArrayList<VaultItem>();
		
		// query db
		Cursor c = db.query("data", new String[]{"key", "value"}, null, null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()) {
			items.add(new VaultItem(c.getString(0), SimpleCrypto.decrypt(c.getString(1))));
			c.moveToNext();
		}
		return items;
	}
	
	public VaultItem getOne(String key) throws Exception {
		Cursor c = db.query("data", new String[]{"key", "value"}, "key = '"+key+"'", null, null, null, null);
		c.moveToFirst();
		VaultItem item=null;
		while(!c.isAfterLast()) {
			item = new VaultItem(c.getString(0), SimpleCrypto.decrypt(c.getString(1)));
			c.moveToNext();
		}
		return item;
	}
}
