package com.apv.accelerate.miloAR.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.apv.accelerate.miloAR.views.Marker;

public abstract class NetworkDataSource extends DataSource {
    protected static final int MAX = 5;
    protected static final int READ_TIMEOUT = 10000;
    protected static final int CONNECT_TIMEOUT = 10000;

    protected List<Marker> markersCache = null;
    
    public abstract String createRequestURL(double lat, double lon, double alt,
                                            float radius, String locale);

    public abstract List<Marker> parse(JSONObject root);
    
    public abstract String parse_1(JSONObject root);
    

    public List<Marker> getMarkers() {
        return markersCache;
    }
    
    protected static InputStream getHttpGETInputStream(String urlStr) {
        if (urlStr == null)
            throw new NullPointerException();

        
        InputStream is = null;
        HttpURLConnection conn = null;

        try {
            if (urlStr.startsWith("file://"))
                return new FileInputStream(urlStr.replace("file://", ""));

            URL url = new URL(urlStr);
            
            if(url.getProtocol().toLowerCase().equals("https")){
            	trustAllHosts();
            	HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            	urlConn.setHostnameVerifier(DO_NOT_VERIFY);
            	conn = urlConn;
            }
            else{
            	conn = (HttpURLConnection) url.openConnection();
            }
 
            conn.setRequestMethod("GET");
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            
            is = conn.getInputStream();

            return is;
        } catch (Exception ex) {
            try {
                is.close();
            } catch (Exception e) {
                // Ignore
            }
            try {
                if (conn instanceof HttpURLConnection)
                    ((HttpURLConnection) conn).disconnect();
            } catch (Exception e) {
                // Ignore
            }
            ex.printStackTrace();
        }

        return null;
    }

 // always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    
    protected String getHttpInputString(InputStream is) {
        if (is == null)
            throw new NullPointerException();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is),
                8 * 1024);
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public List<Marker> parse(String url) {
        if (url == null)
            throw new NullPointerException();

        InputStream stream = null;
        stream = getHttpGETInputStream(url);
        if (stream == null)
            throw new NullPointerException();

        String string = null;
        string = getHttpInputString(stream);
        if (string == null)
            throw new NullPointerException();

        JSONObject json = null;
        try {
            json = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null)
            throw new NullPointerException();
        
        return parse(json);
    }
    
    public String parse_1(String url) {
        if (url == null)
            throw new NullPointerException();

        InputStream stream = null;
        stream = getHttpGETInputStream(url);
        if (stream == null)
            throw new NullPointerException();

        String string = null;
        string = getHttpInputString(stream);
        if (string == null)
            throw new NullPointerException();

        JSONObject json = null;
        try {
            json = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null)
            throw new NullPointerException();
        
        return parse_1(json);
    }
    
}
