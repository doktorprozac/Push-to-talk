package com.example.push_to_talk1;

import java.sql.Date;

public class Record {
	
	private int _id;
	private String _login;
	private String _data;
	private String _klienci;

	public Record(int id, String login, String data /*,String klienci*/) {
		//setLogin(_login);
		//setData(_data);
		//setKlienci(_klienci);
		
		this._id=id;
		this._login=login;
		this._data=data;
		//this._klienci=klienci;
		
	}
	
	public Record(String login, String data /*,String klienci*/) {
		//setLogin(_login);
		//setData(_data);
		//setKlienci(_klienci);
		
		this._login=login;
		this._data=data;
		/*this._klienci=klienci;*/
		
	}

	
	public Record() {
		//this("","", "" ,"");
	}
	
	// getting ID
	public int getID(){
		return this._id;
	}
		
	// setting id
	public void setID(int id){
		this._id = id;
	}

	public String getLogin() {
		return _login;
	}
	
	public void setLogin(String login)
	{
		this._login = login;
	}

	public String getData() {
		return _data;
	}
	
	public void setData(String data)
	{
		this._data = data;
	}
	
/*	public String getKlienci() {
		return _klienci;
	}
	
	public void setKlienci(String klienci)
	{
		this._klienci = klienci;
	}
*/
}
