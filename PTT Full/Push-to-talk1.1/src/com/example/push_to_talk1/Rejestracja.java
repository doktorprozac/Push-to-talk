package com.example.push_to_talk1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Rejestracja extends Activity implements OnClickListener{

	private EditText login;
	private EditText haslo;
	private EditText haslo1;
	private Button rejestracja;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	//php login script
	
	//localhost :
	//testing on your device
	//put your local ip instead,  on windows, run CMD > ipconfig
	//or in mac's terminal type ifconfig and look for the ip under en0 or en1
	// private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/register.php";
	
	private static final String url = "http://www.kaspersky.c0.pl/webservice/register.php";
	
	//testing from a real server:
	//private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/register.php";
	
	//ids
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rejestracja);
		
		login = (EditText) findViewById(R.id.login);
		haslo = (EditText) findViewById(R.id.haslo);
		//haslo1 = (EditText) findViewById(R.id.haslo1);
		rejestracja = (Button) findViewById(R.id.rejestracja);
		rejestracja.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.rejestracja:
				//emptyEditText();
			
				//utworzenie uzytkownika
//				if (TextUtils.isEmpty(login.getText().toString()) || TextUtils.isEmpty(haslo.getText().toString()) || TextUtils.isEmpty(haslo1.getText().toString()) )
//				{
//					Toast.makeText(getApplicationContext(), "tak", Toast.LENGTH_LONG).show();
//				}
				
				new CreateUser().execute();
				break;
		}
	}
	
	private void emptyEditText() {
		// TODO Auto-generated method stub
		if (login.getText().toString().isEmpty() || haslo.getText().toString().isEmpty() || haslo1.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(), "wypelnij wszystkie pola", Toast.LENGTH_LONG).show();
		}
		else if (haslo.getText().toString().equals(haslo1.getText().toString())) {
			// zapis do bazy
			new CreateUser().execute();
			Toast.makeText(getApplicationContext(), "hasla sa zgodne ", Toast.LENGTH_LONG).show();
			Intent powrot = new Intent(Rejestracja.this, MainActivity.class);
			//powrot.putExtra("login", login.getText().toString());
			//powrot.putExtra("haslo", haslo.getText().toString());
			
			startActivity(powrot);
			
		}
		else if (haslo.getText().toString() != haslo1.getText().toString() ) {
			//login.setText(login.getText().toString());
			//haslo.setText("");
			//haslo1.setText("");
			Toast.makeText(getApplicationContext(), " hasla nie sa zgodne ", Toast.LENGTH_LONG).show();
		}
	}
	
	class CreateUser extends AsyncTask<String, String, String> {
		/**
		 ** Before starting background thread Show Progress Dialog
         **/
        boolean failure = false;
		
        @Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Rejestracja.this);
			pDialog.setMessage("Tworzenie uzytkownika");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			// Check for success tag
			int success;
			String username = login.getText().toString();
			String password = haslo.getText().toString();
			
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("user", username));
				params.add(new BasicNameValuePair("pass", makeDigest(password)));
				
				Log.d("request!", "starting");
				
				// Posting user data to script (getting product details by making HTTP request)
				JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);
				
				// full json response
				Log.d("Login attempt", json.toString());
					 
				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("User Created!", json.toString());
					finish();
					return json.getString(TAG_MESSAGE);
				}
				else {
				     Log.d("Login Failure!", json.getString(TAG_MESSAGE));
				     return json.getString(TAG_MESSAGE);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			/*catch (JSONException e)
			{
				e.printStackTrace();
			}*/
			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		
		@Override
		protected void onPostExecute(String file_url) {
			// TODO Auto-generated method stub
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(Rejestracja.this, file_url, Toast.LENGTH_LONG).show();
			}
		}
		
		protected String makeDigest(String inputString) throws NoSuchAlgorithmException, IOException
		{
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			final InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
			
			final byte[] buffer = new byte[4];
			int bufferLength;
			while ((bufferLength = inputStream.read(buffer)) != -1) {
				messageDigest.update(buffer, 0, bufferLength);
			}
			final byte[] digest = messageDigest.digest();

			// wypisywanie wyniku w czytelnej postaci
			final Formatter formatter = new Formatter();
			for (final byte digestByte : digest) {
				formatter.format("%02x", digestByte);
			}
			return formatter.toString();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent main2 = new Intent(Rejestracja.this, MainActivity.class);
		startActivity(main2);
		super.onBackPressed();
	}
	
}
