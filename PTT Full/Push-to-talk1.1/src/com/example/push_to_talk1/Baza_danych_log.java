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

public class Baza_danych_log extends SQLiteOpenHelper {
	
	private static final int VERSION = 1;
	
	private static final String NAME = "baza_polaczen"; // nazwa bazy danych
	private static final String UZYTKOWNICY = "uzytkownicy"; // nazwa tabeli

	// Baza_danych_log Table Columns names
	private static final String ID = "id";
	private static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
	
	private static final String LOGIN = "login";
	private static final String LOGIN_OPTIONS = "TEXT";
	
	private static final String PASSWORD = "password";
	private static final String PASSWORD_OPTIONS = "PASSWORD";

	
	public Baza_danych_log(Context context) { //DatabaseHandler(Context context)
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + UZYTKOWNICY + "(" + ID + " " + ID_OPTIONS + " " + LOGIN
				+ LOGIN_OPTIONS + " " + PASSWORD + " " + PASSWORD_OPTIONS +")");
	}

	// funkcja dodaje nowy rekord do tabeli UZYTKOWNICY
	public void addUser(Login login)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(LOGIN, login.getLogin());
		values.put(PASSWORD, login.getPassword());
		
		db.insert(UZYTKOWNICY, null, values);
		db.close();
	}
	
	// funkcja zwraca określony po id rekord z tabeli UZYTKOWNICY
	public Login readUser(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(UZYTKOWNICY, 
				new String[] { ID, LOGIN, PASSWORD }, ID + "=?",
	            new String[] { String.valueOf(id) }, 
	            null, null, null, null);
		
		if (cursor != null)
	        cursor.moveToFirst();
		
		Login login = new Login(cursor.getString(1), cursor.getString(2));
	    // return contact
	    return login;
	}
	
	// funkcja zwraca wszystkich użytkowników zapisanych w tabeli UZYTKOWNICY
	/*public ArrayList<Login> getAllRecords() {
		ArrayList<Login> people = new ArrayList<Login>();
		
		String selectQuery = "SELECT * FROM " + UZYTKOWNICY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Login login = new Login();
				login.setLogin(cursor.getString(1));
				login.setPassword(cursor.getString(2));
				people.add(login);
			}
			while (cursor.moveToNext());
		}
		return people;
	}
	*/
	/*
	public Cursor getAllRecords() {
		String selectQuery = "SELECT * FROM " + UZYTKOWNICY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;			
	}*/
	
	//Updating Record
	//Deleting Record
	
	public void onUpdate(SQLiteDatabase db, int id) {
		
		/*Cursor c = db.rawQuery("SELECT" + KLIENCI + "FROM" + POLACZENIA
				+ "WHERE" + ID + "==" + id, null);
		KLIENCI_TEMP = c.getString(c.getColumnIndex("content"));
		*/
		//db.execSQL("UPDATE " + POLACZENIA + "SET " + LOGIN_THIS + );
		//onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
