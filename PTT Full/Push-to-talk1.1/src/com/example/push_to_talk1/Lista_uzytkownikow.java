package com.example.push_to_talk1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Lista_uzytkownikow extends Activity implements android.content.DialogInterface.OnClickListener {

	// private TextView textViewListaUzytkownikow;
	TextView statusZalogowanego;
	private ListView historia;
	private ListView historia1;
	private ArrayAdapter<String> listAdapter;
	private ArrayAdapter<String> listAdapter1;
	ProgressDialog pDialog;
	// Session Manager Class
    SessionManager session;
    // Button Logout
    Button btnLogout;
    Button refresh;
    
    public int positionToConnect;
    public String stringToConnect;
    public String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_uzytkownikow);
		
		// Session class instance
        session = new SessionManager(getApplicationContext());
        
     // Button logout
        btnLogout = (Button) findViewById(R.id.logoutButton);
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
		
        refresh = (Button) findViewById(R.id.odswiez);
        
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        name = user.get(SessionManager.KEY_NAME);
         
        statusZalogowanego = (TextView) findViewById(R.id.status);
        // displaying user data
        statusZalogowanego.setText(name);
        
		//textViewListaUzytkownikow = (TextView) findViewById(R.id.textViewListaUzytkownikow);
		historia = (ListView) findViewById(R.id.listView1);
		historia1 = (ListView) findViewById(R.id.listView2);

	
		historia.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					AlertDialog.Builder connect_row = new AlertDialog.Builder(Lista_uzytkownikow.this);
					
					connect_row.setTitle("Polacz");
					connect_row.setMessage("Polacz z " + historia1.getItemAtPosition(position));
					
				
					stringToConnect = (String) historia1.getItemAtPosition(position);
					connect_row.setNegativeButton("NIEEEE", Lista_uzytkownikow.this);
					connect_row.setPositiveButton("TAK !", Lista_uzytkownikow.this );
					connect_row.show();
					
				}
			});
	       
		historia1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				AlertDialog.Builder connect_row = new AlertDialog.Builder(Lista_uzytkownikow.this);
				
				connect_row.setTitle("Polacz");
				connect_row.setMessage("Polacz z " + historia1.getItemAtPosition(position));
				
				//String data=(String) historia1.getItemAtPosition(position);
				stringToConnect = (String) historia1.getItemAtPosition(position);
				connect_row.setNegativeButton("NIEEEE", Lista_uzytkownikow.this);
				connect_row.setPositiveButton("TAK !", Lista_uzytkownikow.this );
				connect_row.show();
				
			}
		});
	 
	
		
		//textViewListaUzytkownikow.setText("lista");
		/*
		Baza_danych_log db = new Baza_danych_log(this);
		Cursor cursor = db.getAllRecords();
		do {
			Login login = new Login();
			login.setLogin(cursor.getString(1));
			login.setPassword(cursor.getString(2));
			textViewListaUzytkownikow.setText(login.getLogin());
		}
		while (cursor.moveToNext());
		
		Baza_danych_log db = new Baza_danych_log(this);
		Login user01 = new Login("dominik", "pass01");
		db.addUser(user01);
		List<Login> login_list = db.getAllRecords();*/
		
		/*for(Login cn : login_list)
		{
			String row = "Name: " + cn.getLogin() + ", Password: " + cn.getPassword();
            
            // Writing users
            // textViewListaUzytkownikow.setText(row);
		}*/
		/*for(int i = login_list.size() - 1; i >= 0; i--) {
        	Login cn = login_list.get(i);
            String row = "Name: " + cn.getLogin() + ", Password: " + cn.getPassword();
            
            // Writing users
            textViewListaUzytkownikow.setText(row);
		}*/
		new GetAllCustomerTask().execute(new Polaczenie_baza());
		
		
		/**
         * Logout button click event
         * */
		btnLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Logout().execute(name);
				session.logoutUser();
			}
		});
		
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new GetAllCustomerTask().execute(new Polaczenie_baza());
			}
		});
	}

	


	private class GetAllCustomerTask extends AsyncTask<Polaczenie_baza, Long, JSONArray> {
		
		protected JSONArray doInBackground(Polaczenie_baza ...params) {
    		return params[0].GetAllCustomers();
    	}

		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			// TODO Auto-generated method stub
			Log.e("01: ", jsonArray.toString());
			setTextToTextView(jsonArray);
			setTextToTextView1(jsonArray);
			super.onPostExecute(jsonArray);
		}
		public void setTextToTextView1(JSONArray jsonArray) {
	    	String s1 = "";
	    	ArrayList<String> his_urz1 = new ArrayList<String>();
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject json = null;
	    		try {
	    			json = jsonArray.getJSONObject(i);
	    			s1 =   json.getString("ip_urzadzenia");
	    			
	    			if(!s1.isEmpty())
	    			{
	    				his_urz1.add(s1);
	    			}
	    			
	    		}
	    		catch (JSONException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	//textViewListaUzytkownikow.setText(s);
	    	listAdapter1 = new ArrayAdapter<String>(Lista_uzytkownikow.this, android.R.layout.simple_list_item_1, his_urz1);
	    	historia1.setAdapter(listAdapter1);
	    	
	    }
		
		public void setTextToTextView(JSONArray jsonArray) {
	    	String s = "";
	    	String s1 = "";
	    	ArrayList<String> his_urz = new ArrayList<String>();
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject json = null;
	    		try {
	    			json = jsonArray.getJSONObject(i);
	    			s =  json.getString("username");
	    			s1 =   json.getString("ip_urzadzenia");
	    			
	    			if(!s1.isEmpty())
	    			{
	    				his_urz.add(s);
	    			}
	    		}
	    		catch (JSONException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	//textViewListaUzytkownikow.setText(s);
	    	listAdapter = new ArrayAdapter<String>(Lista_uzytkownikow.this, android.R.layout.simple_list_item_1, his_urz);
	    	historia.setAdapter(listAdapter);
	    	
	    }
		
		
    }

private class Logout extends AsyncTask<String, String, String> {
		
		//JSON parser class
		JSONParser jsonParser = new JSONParser();
		
		//testing from a real server:
	  	private static final String url2 = "http://www.kaspersky.c0.pl/webservice/logout.php";
	  		 
	  	//JSON element ids from repsonse of php script:
	  	private static final String TAG_SUCCESS = "success";
	  	private static final String TAG_MESSAGE = "message";
		
		protected String doInBackground(String ...args) {
			int success;
			try {
    	    	// Building Parameters
    	        List<NameValuePair> params = new ArrayList<NameValuePair>();
    	        params.add(new BasicNameValuePair("user", args[0]));
    		 
    		    Log.d("request!", "starting");
    		    
    		    // Posting user data to script (getting product details by making HTTP request)
    		    JSONObject json = jsonParser.makeHttpRequest(url2, "POST", params);
    		 
    		    // check your log for json response
    		    Log.d("Login attempt", json.toString());
    		 
    		    // json success tag
    		    success = json.getInt(TAG_SUCCESS);
    		    if (success == 1) {
    		        Log.d("Login Successful!", json.toString());
    		        return json.getString(TAG_MESSAGE);
    		        /*Intent i = new Intent(MainActivity.this, ReadComments.class);
    		        finish();
    		        startActivity(i);*/
    		    }
    		    else {
    		      	Log.d("Login Failure!", json.getString(TAG_MESSAGE));
    		        return json.getString(TAG_MESSAGE);
  		        }
    		} 
    	    catch (JSONException e) {
    	    	e.printStackTrace();
    		} 
			return null;
    	}

		@Override
		protected void onPostExecute(String s) {
			// TODO Auto-generated method stub
			super.onPostExecute(s);
		}
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which)
		{
		
		case DialogInterface.BUTTON_POSITIVE:
			// funkcja do usuwania kogos tam
			connectItem(positionToConnect);
			Toast.makeText(getApplicationContext(), "pozytywnie ", Toast.LENGTH_LONG).show();
			Intent rozmowa = new Intent(Lista_uzytkownikow.this, WifiActivity.class);
			startActivity(rozmowa);
			
			Intent intent=new Intent(Lista_uzytkownikow.this,WifiActivity.class);
			 intent.putExtra("name", stringToConnect);
			 startActivity(intent);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			Toast.makeText(getApplicationContext(), "negatywnie ", Toast.LENGTH_LONG).show();
			break;
			
		}
	}




	private void connectItem(int positionToConnect2) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "polaczono !!! ", Toast.LENGTH_LONG).show();
	}


	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		new Logout().execute(name);
		session.logoutUser(); 
		Toast.makeText(getApplicationContext(), "wylogowano !!! ", Toast.LENGTH_LONG).show();
		super.onBackPressed();
		
	}

	

}
