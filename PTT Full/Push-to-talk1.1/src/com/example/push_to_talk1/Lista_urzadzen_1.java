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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Lista_urzadzen_1 extends Activity implements
		android.content.DialogInterface.OnClickListener {

	private ListView list_historia;
	public String logiin;
	private ArrayAdapter<String> listAdapter;
	public String[] wartosci;
	public String[] wartosci1;
	public String item;
	public String item1;

	public String[] listadousuniecia;

	private static String TAG = "wprowadzenie";
	public static final String POLACZENIA = "polaczenia";
	public static final String ID = "id";
	public SQLiteDatabase db;

	public String positionToRemove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_his);

		Baza_danych_uz db = new Baza_danych_uz(this);

		Log.d(TAG, "wypisanie danych");

		list_historia = (ListView) findViewById(R.id.list_historia);
		// String item = logiin + " " + currentDateandTime;

		ArrayList<String> his_urz = new ArrayList<String>();

		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, his_urz);
		list_historia.setAdapter(listAdapter);

		// String currentDateandTime = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		// Reading all contacts
		Log.d("Reading: ", "Reading all contacts..");
		List<Record> contacts = db.getAllRecords();

		int li = contacts.size();
		final String[] wartosci1 = new String[li];

		int lis = 0;
		for (Record cn : contacts) {
			String log = "Id: " + cn.getID() + "Login: " + cn.getLogin()
					+ " ,Data: " + cn.getData() /*
												 * + " ,Klienci: " +
												 * cn.getKlienci()
												 */;
			// Writing Contacts to log
			Log.d("Name: ", log);
			item = cn.getID() + " " + cn.getLogin() + " " + cn.getData();

			item1 = cn.getID() + "";
			wartosci = new String[] { item };
			wartosci1[lis] = item1;

			// listadousuniecia[lis]= "" + cn.getID();
			lis++;
			// listadousuniecia[0]="kkk";

			his_urz.addAll(Arrays.asList(wartosci));
		}
		list_historia.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				AlertDialog.Builder delete_row = new AlertDialog.Builder(
						Lista_urzadzen_1.this);

				delete_row.setTitle("Ussuwanie");
				// delete_row.setMessage("usun pozycje " +
				// listadousuniecia[(int)list_historia.getItemIdAtPosition(position)]);
				delete_row.setMessage("usun pozycje "
						+ wartosci1[(int) list_historia
								.getItemIdAtPosition(position)] /*
																 * list_historia.
																 * getItemIdAtPosition
																 * (position)
																 */);

				positionToRemove = wartosci1[(int) list_historia
						.getItemIdAtPosition(position)];
				delete_row.setNegativeButton("NIEEEE", Lista_urzadzen_1.this);
				delete_row.setPositiveButton("TAK !", Lista_urzadzen_1.this);
				delete_row.show();

			}
		});

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which) {

		case DialogInterface.BUTTON_POSITIVE:
			// funkcja do usuwania kogos tam
			Baza_danych_uz db = new Baza_danych_uz(this);
			db.deleteUser(positionToRemove);
			Toast.makeText(getApplicationContext(), "pozytywnie ",
					Toast.LENGTH_LONG).show();
			Intent debilny = new Intent(Lista_urzadzen_1.this,
					Lista_urzadzen_1.class);
			startActivity(debilny);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			Toast.makeText(getApplicationContext(), "negatywnie ",
					Toast.LENGTH_LONG).show();
			break;

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent main = new Intent(Lista_urzadzen_1.this, MainActivity.class);
		startActivity(main);
	
	/*	if (login.getText().toString().isEmpty() || haslo.getText().toString().isEmpty())
    	{
			// baza na androidzie
			Intent debilny = new Intent(Lista_urzadzen_1.this,
					MainActivity.class);
			startActivity(debilny);
		}
		else
		{
			// remote database
			
			// Creating user login session
            // For testing i am stroing name, email as follow
            // Use user real data
            session.createLoginSession(login.getText().toString());
			
			// baza na androidzie
			Toast.makeText(getApplicationContext(), "zalogowales sie ", Toast.LENGTH_LONG).show();
			Intent lista_uz = new Intent(Lista_urzadzen_1.this, Lista_uzytkownikow.class);
			startActivity(lista_uz);
			//login.setText("");
			//haslo.setText("");
		}
*/
	}

}
