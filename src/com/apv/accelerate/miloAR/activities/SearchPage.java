package com.apv.accelerate.miloAR.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apv.accelerate.miloAR.data.ARData;
import com.apv.accelerate.miloAR.R;

public class SearchPage extends Activity {
	
	private TextView locationTextView;
	
	EditText searchQuery; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.main);
		
		searchQuery = (EditText)findViewById(R.id.editText1);
		
		Button searchButton = (Button) findViewById(R.id.button1);

		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!searchQuery.getText().toString().equals("")){
					Intent intent = new Intent();
					intent.putExtra("SEARCH_QUERY", searchQuery.getText().toString());
					intent.setClass(getApplicationContext(), MiloAugmentedReality.class);
					startActivity(intent);
				}
				else{
					Toast.makeText(getApplicationContext(), "Please Enter Product Name", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		locationTextView = (TextView)findViewById(R.id.textView1);
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(
            this, R.array.locations_array,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		searchQuery.setText("");
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	
	    	// #kolveri code.. 
	    	
	    	if(parent.getItemAtPosition(pos).toString().equalsIgnoreCase("San Jose, California")){
	    		ARData.hardFix.setLatitude(37.3393857);
	    		ARData.hardFix.setLongitude(-121.8949555);
	    		ARData.hardFix.setAltitude(1);	
	    		locationTextView.setText("Current Location : San Jose, California");
	    	}
	    	else if(parent.getItemAtPosition(pos).toString().equalsIgnoreCase("Washington")){
	    		ARData.hardFix.setLatitude(38.8951118);
	    		ARData.hardFix.setLongitude(-77.0363658);
	    		ARData.hardFix.setAltitude(1);
	    		locationTextView.setText("Current Location : Washington");
	    	}
	    	else if(parent.getItemAtPosition(pos).toString().equalsIgnoreCase("Chicago, Illinois")){
	    		ARData.hardFix.setLatitude(41.8781136);
	    		ARData.hardFix.setLongitude(-87.6297982);
	    		ARData.hardFix.setAltitude(1);
	    		locationTextView.setText("Current Location : Chicago, Illinois");
	    	}
	    	else if(parent.getItemAtPosition(pos).toString().equalsIgnoreCase("New York")){
	    		ARData.hardFix.setLatitude(40.7143528);
	    		ARData.hardFix.setLongitude(-74.0059731);
	    		ARData.hardFix.setAltitude(1);
	    		locationTextView.setText("Current Location : New York");
	    	}
	    }

	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
}
