package com.example.push_to_talk1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {
	
	public String[] wartosci1;
	public String[] wartosci2;
	
	private EditText login;
	private EditText haslo;
	
	private TextView textView3;
	private TextView status;
	private TextView parametry;
	private TextView wolny_port;
	
	private Button logowanie;
	private Button rejestracja;
   	public boolean login_true;
	public boolean haslo_true;
	
	//private Button buttonToUsersList;
	private Button button_historia;
	
	// Prograss Dialog
	private ProgressDialog pDialog;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	// Session Manager Class
    SessionManager session;
	
	// php login script location:
		 
	// localhost :
	// testing on your device
	// put your local ip instead,  on windows, run CMD > ipconfig
	// or in mac's terminal type ifconfig and look for the ip under en0 or en1
	// private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/login.php";
	 
	//testing from a real server:
	private static final String url = "http://www.kaspersky.c0.pl/webservice/login.php";
		 
	//JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	public Baza_danych_uz db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.glowne);
        
        //setup input fields
        login = (EditText) findViewById(R.id.login);
    	haslo = (EditText) findViewById(R.id.haslo);
        
    	
        status = (TextView) findViewById(R.id.status);
        textView3 = (TextView) findViewById(R.id.textView3);
        parametry = (TextView) findViewById(R.id.parametry);
        wolny_port = (TextView) findViewById(R.id.wolny_port);
        
    	// polaczenie wifi
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    	if (mWifi.isConnected()) {
    		status.setText("polaczono z siecia wifi");
    	}
    	else if (!mWifi.isConnected()) {
    		status.setText(" nie polaczono z siecia wifi");
    	}
    	
    	// Session Manager
        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
           
        Bundle koszyk = getIntent().getExtras();
        if (koszyk != null) { 	
        	String wartosc = koszyk.getString("haslo");
           	String wartosc1 = koszyk.getString("login");
           	Toast.makeText(getApplicationContext(), "odebrano: " + wartosc1 + " "  + wartosc, Toast.LENGTH_LONG).show();
        }
    	
        // setup button
        logowanie = (Button) findViewById(R.id.logowanie);
        rejestracja = (Button) findViewById(R.id.rejestracja);
       // buttonToUsersList = (Button)findViewById(R.id.buttonToUsersList);
        button_historia = (Button) findViewById(R.id.button_historia);
        
        //register listeners
        logowanie.setOnClickListener(this);
        rejestracja.setOnClickListener(this);
        //buttonToUsersList.setOnClickListener(this);
        //button_historia.setOnClickListener(this);
        
        getWifiIpAddress();
        
        try {
            ServerSocket s = create(new int[] { 55984 });
            wolny_port.setText("" + s.getLocalPort());
        } catch (IOException ex) {
        	 wolny_port.setText("no available ports");
        }
        
        button_historia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent rejestr1 = new Intent(MainActivity.this, Lista_urzadzen_1.class);
        		startActivity(rejestr1);
			}
		});
        
        db = new Baza_danych_uz(this);
      
    }
    
    public ServerSocket create(int[] ports) throws IOException {
        for (int port : ports) {
            try {
                return new ServerSocket(port);
            } catch (IOException ex) {
                continue; // try next port
            }
        }

        // if the program gets here, no port in the range was found
        throw new IOException("no free port found");
    }
    
    // zwraca adres ip urzadzenia
    
	private String getWifiIpAddress() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format(Locale.getDefault(), "%d.%d.%d.%d",
				(ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
				(ip >> 24 & 0xff));

		parametry.setText(ipString);
		return ipString;
	}

    @Override
    public void onClick(View v) {
    	// TODO Auto-generated method stub
        switch (v.getId()) {
         	case R.id.logowanie:
         		new AttemptLogin().execute();
         		emptyEditText();
                break;
            case R.id.rejestracja:
                Intent i = new Intent(this, Rejestracja.class);
                startActivity(i);
                break;
            /*case R.id.textViewListaUzytkownikow:
            	Intent rejestr = new Intent(MainActivity.this, Lista_uzytkownikow.class);
        		startActivity(rejestr);
        		break;*/
           /* case R.id.button_historia:
            	Intent rejestr1 = new Intent(MainActivity.this, Lista_urzadzen_1.class);
        		startActivity(rejestr1);
            	break;*/
            default:
               	break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {
    	/**
    	 ** Before starting background thread Show Progress Dialog
    	 **/
    	boolean failure = false;
    		 
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		pDialog = new ProgressDialog(MainActivity.this);
    	    pDialog.setMessage("Attempting login...");
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
    	    String ip = parametry.getText().toString();
    	    String wolny_por = wolny_port.getText().toString();
    	    
    	    try {
    	    	// Building Parameters
    	        List<NameValuePair> params = new ArrayList<NameValuePair>();
    	        params.add(new BasicNameValuePair("user", username));
    		    params.add(new BasicNameValuePair("pass", makeDigest(password)));
    		    params.add(new BasicNameValuePair("ip", ip));
    		    params.add(new BasicNameValuePair("numer_portu", wolny_por));
    		 
    		    Log.d("request!", "starting");
    		    
    		    // Posting user data to script (getting product details by making HTTP request)
    		    JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);
    		 
    		    // check your log for json response
    		    Log.d("Login attempt", json.toString());
    		 
    		    // json success tag
    		    success = json.getInt(TAG_SUCCESS);
    		    if (success == 1) {
    		        Log.d("Login Successful!", json.toString());
    		        login_true=true;
    		        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
					db.addUser(new Record(username, currentDateandTime));
    		        session.createLoginSession(login.getText().toString());
    		        Intent lista_uz = new Intent(MainActivity.this, Lista_uzytkownikow.class);
					startActivity(lista_uz);
    		        return json.getString(TAG_MESSAGE);
					/*Intent i = new Intent(MainActivity.this, ReadComments.class);
    		        finish();
    		        startActivity(i);*/
    		    }
    		    else {
    		    	login_true=false;
    		      	Log.d("Login Failure!", json.getString(TAG_MESSAGE));
    		        return json.getString(TAG_MESSAGE);
  		        }
    		} 
    	    catch (JSONException e) {
    	    	e.printStackTrace();
    		} 
    	    catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    	    catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return null;	 
    	}
    	
    	/**
    	  * After completing background task Dismiss the progress dialog
    	  * **/
    	protected void onPostExecute(String file_url) {
    		// dismiss the dialog once product deleted
    	    pDialog.dismiss();
    	    if (file_url != null) {
    	    	Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
    	    }
    	}
    	
    	protected String makeDigest(String inputString) throws NoSuchAlgorithmException, IOException {
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
    
    private void emptyEditText() {
    	JSONArray jsonArray = new JSONArray();
    	
    	  int li = jsonArray.length();
  		final String[] wartosci1 = new String[li];
  		final String[] wartosci2 = new String[li];
  		
  		int lis = 0;
    	
    	JSONObject json = null;
    	if (login.getText().toString().isEmpty() || haslo.getText().toString().isEmpty())
    	{
			// baza na androidzie
			Toast.makeText(getApplicationContext(), "wypelnij pola:" + " login i haslo", Toast.LENGTH_LONG).show();
		}
		else
		{
			
			/*String s = "";
			String s1 = "";
			for (int i = 0; i < jsonArray.length(); i++) {
	    		
	    		try {
	    			json = jsonArray.getJSONObject(i);
	    			s =  json.getString("username");
	    			s1 = json.getString("password");
	    			wartosci1[lis] = s;
	    			wartosci2[lis] = s1;
	    			lis++;
	    			if (login.getText().toString().equals(s))
	    			{
	    				login_true = true;
	    				Log.d("login", "login poprawny");
	    			}
	    			if (haslo.getText().toString().equals(s1))
	    			{
	    				haslo_true = true;
	    				Log.d("haslo", "haslo poprawny");
	    			}
	    		}
	    		catch (JSONException e) {
	    			e.printStackTrace();
	    		}
	    	}*/
//			if(login_true == true)
//			{
//				if(haslo_true==true)
//				{
//					session.createLoginSession(login.getText().toString());
//					
//					// baza na androidzie
//					Toast.makeText(getApplicationContext(), "zalogowales sie ", Toast.LENGTH_LONG).show();
//					Intent lista_uz = new Intent(MainActivity.this, Lista_uzytkownikow.class);
//					startActivity(lista_uz);
//				}
//				else
//				{
//					Toast.makeText(getApplicationContext(), "zly login lub haslo ", Toast.LENGTH_LONG).show();
//				}
				//sprawdzamy haslo
				/*String s1 = "";
				for (int i = 0; i < jsonArray.length(); i++) {
		    		
		    		try {
		    			json = jsonArray.getJSONObject(i);
		    			s1 =  json.getString("username");
		    			wartosci1[lis] = s1;
		    			lis++;
		    			if (login.getText().toString().equals(s1))
		    			{
		    				login_true = true;
		    				Log.d("login", "login poprawny");
		    			}
		    		}
		    		catch (JSONException e) {
		    			e.printStackTrace();
		    		}
		    	}*/
			}
//			else
//			{
//				Toast.makeText(getApplicationContext(), "zly login lub haslo ", Toast.LENGTH_LONG).show();
//				// toast bledny login lub haslo
//			}
			/*
			baza zew_login -> string[]
			poownujemy podany login z tym stringiem ^
			if(zfadza się)
			{
			sprawdzamy to jedno hasło
				if (hasło się zgadza) Intent ...
				else toast złe hasło
			}else toast nie ma takiego usera
			
			*/
			// remote database
			
			// Creating user login session
            // For testing i am stroing name, email as follow
            // Use user real data
            
			//login.setText("");
			//haslo.setText("");
		}
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onBackPressed();
		finish();
	}
    
}
