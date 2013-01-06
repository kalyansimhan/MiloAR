package com.apv.accelerate.miloAR.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apv.accelerate.miloAR.data.ARData;
import com.apv.accelerate.miloAR.data.MiloDataSource;
import com.apv.accelerate.miloAR.data.MiloDataSource.Merchant;
import com.apv.accelerate.miloAR.R;

public class MerchantDialog extends Dialog {

	private String merchant_name = "";
	
	public MerchantDialog(Context context, String name) {
		super(context);
		
		setContentView(R.layout.dialog);
		
		setTitle("Merchant Details");
		
		merchant_name = name;
		
		final Merchant merchant = MiloDataSource.merchantDetails.get(merchant_name);
		
		if(merchant == null)
			System.out.println("Boom" + merchant_name);
		
		TextView large = (TextView) findViewById(R.id.dialog_textView1);
		large.setText(merchant_name);
		large.setTextColor(Color.RED);
		
		TextView med1 = (TextView) findViewById(R.id.med_textView1);
		med1.setText(merchant.getCity());
		
		TextView med2 = (TextView) findViewById(R.id.med_textView2);
		med2.setText(merchant.getDescp());
		
		TextView med4 = (TextView) findViewById(R.id.med_textView3);
		med4.setText(merchant.getStreet());
		
		Button getDirections = (Button) findViewById(R.id.dialog_button1);
		getDirections.setText("Get Directions");
		
		final Context cont = context;
		getDirections.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(cont, MapRouteActivity.class);
				intent.putExtra("FROM_LAT", ARData.hardFix.getLatitude());
				intent.putExtra("FROM_LON", ARData.hardFix.getLongitude());
				intent.putExtra("TO_LAT", merchant.getLat());
				intent.putExtra("TO_LON", merchant.getLon());
				cont.startActivity(intent);
				dismiss();
			}
		});
		
	}
	

}
