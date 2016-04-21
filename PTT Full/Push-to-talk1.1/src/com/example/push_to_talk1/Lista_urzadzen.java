package com.example.push_to_talk1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Lista_urzadzen extends Activity implements android.content.DialogInterface.OnClickListener{

	private Button polacz;
	private ListView historia_log;
	public String logiin;
	private ArrayAdapter<String> listAdapter;
	public String[] wartosci;
	public String item;
	public String[] listadousuniecia;
	
	private static String TAG = "wprowadzenie";
	public static final String POLACZENIA = "polaczenia";
	public static final String ID = "id";
	public SQLiteDatabase db;
	
	public int positionToRemove;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.lista_urzadzen);
		 
		 Bundle koszyk1 = getIntent().getExtras();
		 if (koszyk1 != null)
		 {
			 logiin = koszyk1.getString("login");
		 }
		 
		// String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		 
		 
		 Baza_danych_uz db = new Baza_danych_uz(this);
		 
	
		 
		 Log.d(TAG, "wypisanie danych");
		
		 historia_log = (ListView) findViewById(R.id.historia_log);
//		 String item = logiin + " " + currentDateandTime;
		  
//		 String[] wartosci = new String[] {item};
		 
		 
		 
		 ArrayList<String> his_urz = new ArrayList<String>();
		 
		
		 listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, his_urz);
		 historia_log.setAdapter(listAdapter);
		 
		 // jak wpisaæ do bazy to z editText ???????? - do rozwi¹zania !!!!
		 
		 // linijska ma byc w logowaniu
		 //db.addUser(new Record(logiin, currentDateandTime));
		// db.addUser(new Record("bbba", "217"/*, "lel"*/));
		 
		 
		 // Reading all contacts
	        Log.d("Reading: ", "Reading all contacts..");
	        List<Record> contacts = db.getAllRecords();      
	        
	        int li = contacts.size();
	        int lis=0;
	        for (Record cn : contacts) {
	            String log = "Id: " + cn.getID() + "Login: "+cn.getLogin()+" ,Data: " + cn.getData() /*+ " ,Klienci: " + cn.getKlienci()*/;
	                // Writing Contacts to log
	        Log.d("Name: ", log);
	        item = cn.getLogin() + " " + cn.getData();
			  
	        wartosci = new String[] {item};

	        listadousuniecia[lis]= "" + cn.getID();
	        lis++;
	        
	        his_urz.addAll(Arrays.asList(wartosci));
	       }
	        
	        historia_log.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					AlertDialog.Builder delete_row = new AlertDialog.Builder(Lista_urzadzen.this);
					
					delete_row.setTitle("Ussuwanie");
					delete_row.setMessage("usun pozycje " + listadousuniecia[(int)historia_log.getItemIdAtPosition(position)]);
					
				
					positionToRemove = (int) historia_log.getItemIdAtPosition(position);
					delete_row.setNegativeButton("NIEEEE", Lista_urzadzen.this);
					delete_row.setPositiveButton("TAK !", Lista_urzadzen.this );
					delete_row.show();
					
				}
			});
	        
	 
	
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which)
		{
		
		case DialogInterface.BUTTON_POSITIVE:
			// funkcja do usuwania kogos tam
			Baza_danych_uz db = new Baza_danych_uz(this);
			 db.deleteUser(String.valueOf(positionToRemove));
			 Toast.makeText(getApplicationContext(), "pozytywnie ", Toast.LENGTH_LONG).show();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			Toast.makeText(getApplicationContext(), "negatywnie ", Toast.LENGTH_LONG).show();
			break;
			
		}
	}
}



