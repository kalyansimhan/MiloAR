package com.apv.accelerate.miloAR.data;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.apv.accelerate.miloAR.views.IconMarker;
import com.apv.accelerate.miloAR.views.Marker;
import com.apv.accelerate.miloAR.R;


/**
 * This class extends DataSource to fetch data from Milo.
 * 
 */
public class MiloDataSource extends NetworkDataSource {
	private static final String PRODUCTS_URL = "https://api.x.com/milo/v3/products";
	
	private static final String MERCHANTS_STORE_URL = "https://api.x.com/milo/v3/store_addresses";

	private static Bitmap icon = null;
	
	private static final String API_KEY = "";
	
	private double latitude = 0;
	private double longitude = 0;
	private float radius = 0;	
	
	public static Map<String, Merchant> merchantDetails = new HashMap<String, Merchant>();
	
	public class Merchant{
		String merchant_name;
		String city;
		String phone_no;
		String descp;
		String street;
		Double lat,lon;
		
		public String getMerchant_name() {
			return merchant_name;
		}

		public String getCity() {
			return city;
		}

		public String getPhone_no() {
			return phone_no;
		}

		public String getDescp() {
			return descp;
		}

		public String getStreet() {
			return street;
		}

		
		
		public Merchant(String merchant_name,String city, String phone_no, String descp, String street, Double lat, Double lon) {
			this.merchant_name = merchant_name;
			this.city = city;
			this.phone_no = phone_no;
			this.descp = descp;
			this.street = street;
			this.lat = lat;
			this.lon = lon;
		}

		public Double getLat() {
			return lat;
		}

		public Double getLon() {
			return lon;
		}
	}
	

	public MiloDataSource(Resources res) {
		if (res==null) throw new NullPointerException();
		
		createIcon(res);
	}
	
	protected void createIcon(Resources res) {
		if (res==null) throw new NullPointerException();
		
		icon=BitmapFactory.decodeResource(res, R.drawable.milo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createRequestURL(double lat, double lon, double alt, float rad, String query) {
		
		latitude = lat;
		longitude = lon;
		radius = rad;
		
		return PRODUCTS_URL+"?key=" + API_KEY + "&" + "q=" + URLEncoder.encode(query) + "&" + "latitude=" + lat + "&" + "longitude=" + lon + "&" + "radius=" + rad;
	}

	public String createMerchantsURL(double lat, double lon, float radius, String merchantIds){
		return MERCHANTS_STORE_URL + "?key=" + API_KEY + "&" + "latitude=" + latitude + "&" + "longitude=" + longitude + "&" + "radius=" + radius + "&" + "merchant_ids=" + merchantIds; 
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Marker> parse(String url) {
		if (url==null) throw new NullPointerException();
		
		InputStream stream = null;
    	stream = getHttpGETInputStream(url);
    	if (stream==null) throw new NullPointerException();
    	
    	String string = null;
    	string = getHttpInputString(stream);
    	if (string==null) throw new NullPointerException();
    	
    	JSONObject json = null;
    	try {
    		json = new JSONObject(string);
    	} catch (JSONException e) {
    	    e.printStackTrace();
    	}
    	if (json==null) throw new NullPointerException();
    	
    	return parse(json);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Marker> parse(JSONObject root) {
		if (root==null) throw new NullPointerException();
		
		JSONObject jo = null;
		JSONArray dataArray = null;
    	List<Marker> markers=new ArrayList<Marker>();

		try {
			 if(root.has("store_addresses")){
				dataArray = root.getJSONArray("store_addresses");
				int top = Math.min(MAX, dataArray.length());
				for (int i = 0; i < top; i++) {					
					jo = dataArray.getJSONObject(i);
					Marker ma = processJSONObject(jo);
					if(ma!=null) markers.add(ma);
				}
			}
			
		} catch (JSONException e) {
		    e.printStackTrace();
		}
		return markers;
	}
	
	private String purify(String merchantIds) {
		StringBuilder sb = new StringBuilder(merchantIds);
		sb.replace(merchantIds.lastIndexOf(","), merchantIds.lastIndexOf(",") + 1, "");
		return sb.toString();
	}

	private Marker processJSONObject(JSONObject jo) {
		if (jo==null) throw new NullPointerException();
				
		Marker ma = null;
		try {
			Double lat = null, lon = null;
			String merchant_name, city, phone, descp, street;
			
			lon = Double.parseDouble(jo.getString("longitude"));
			lat = Double.parseDouble(jo.getString("latitude"));
			merchant_name = jo.getString("merchant_name");
			city = jo.getString("city");
			phone = jo.getString("phone");
			descp = jo.getString("descriptor");
			street = jo.getString("street");
			
			Merchant merchant = new Merchant(merchant_name, city, phone, descp, street,lat,lon);
			merchantDetails.put(merchant_name, merchant);
			
			if(lat!=null) {

				ma = new IconMarker(
						merchant_name, 
						lat, 
						lon, 
						0,
						Color.RED,
						icon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ma;
	}

	@Override
	public String parse_1(JSONObject root) {
		if (root==null) throw new NullPointerException();
		
		JSONObject jo = null;
		JSONArray dataArray = null;

		try {
			if(root.has("merchants")){
				dataArray = root.getJSONArray("merchants");
				int top = Math.min(MAX, dataArray.length());
				String merchantIds = "";
				for (int i = 0; i < top; i++) {					
					jo = dataArray.getJSONObject(i);
					merchantIds += jo.getString("merchant_id");
					merchantIds += ",";					
					}				
				return createMerchantsURL(latitude, longitude, radius, purify(merchantIds));
				}
			}catch (JSONException e) {
		    e.printStackTrace();
		}
			return null;
	}
}
		
