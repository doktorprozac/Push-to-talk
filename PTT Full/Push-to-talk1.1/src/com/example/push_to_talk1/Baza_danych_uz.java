package com.example.push_to_talk1;

import java.sql.Date;
import java.util.ArrayList;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Baza_danych_uz extends SQLiteOpenHelper {
	
	private static final int VERSION = 1;
	
	private static final String NAME = "baza_polaczen"; // nazwa bazy danych
	private static final String POLACZENIA = "polaczenia"; // nazwa tabeli

	// Baza_danych_uz Table Columns names
	private static final String ID = "id";
	private static final String LOGIN_THIS = "login";
	private static final String DATA = "data";
	private static final String KLIENCI = "klienci";
	
	public static String KLIENCI_TEMP = "";

	public Baza_danych_uz(Context context) { //DatabaseHandler(Context context)
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + POLACZENIA + "(" + ID + " INTEGER PRIMARY KEY," + LOGIN_THIS
				+ " TEXT," + DATA + " DATE," + KLIENCI + "TEXT"+")");
		//"CREATE TABLE polaczenia(id INTEGER PRIMARY KEY, login TEXT, data DATE, klinci TEXT";
	}

	// funkcja dodaje nowy rekord do tabeli POLACZENIA
	public void addUser(Record record)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(LOGIN_THIS, record.getLogin());
		values.put(DATA, record.getData());
		//values.put(KLIENCI, record.getKlienci());
		

		db.insert(POLACZENIA, null, values);
		db.close();
	}
	
	public void deleteUser(String mid)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(POLACZENIA, ID + "=?", new String []{mid});
		
		db.close();
	}
	
	// funkcja zwraca określony po id rekord z tabeli POLACZENIA
	public Record readUser(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(POLACZENIA, 
				new String[] { ID, LOGIN_THIS, DATA, KLIENCI }, ID + "=?",
	            new String[] { String.valueOf(id) }, 
	            null, null, null, null);
		if (cursor != null)
	        cursor.moveToFirst();
		
		Record record = new Record(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
	    // return contact
	    return record;
	}
	
	// funkcja zwraca wszystkich użytkowników zapisanych w bazie POLACZENIA
	public ArrayList<Record> getAllRecords() {
		ArrayList<Record> people = new ArrayList<Record>();
		
		String selectQuery = "SELECT * FROM " + POLACZENIA;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Record record = new Record();
				record.setID(Integer.parseInt(cursor.getString(0)));
				record.setLogin(cursor.getString(1));
				record.setData(cursor.getString(2));
				//record.setKlienci(cursor.getString(3));
				people.add(record);
			}
			while (cursor.moveToNext());
		}
		return people;
	}
	
	//Updating Record
	//Deleting Record
	
	public void onUpdate(SQLiteDatabase db, int id) {
		
		Cursor c = db.rawQuery("SELECT" + KLIENCI + "FROM" + POLACZENIA + "WHERE" + ID + "==" + id, null);
		KLIENCI_TEMP = c.getString(c.getColumnIndex("content"));
		
		//db.execSQL("UPDATE " + POLACZENIA + "SET " + LOGIN_THIS + );
		//onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
