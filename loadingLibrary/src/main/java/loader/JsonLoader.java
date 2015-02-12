package loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;


//import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

public class JsonLoader extends AsyncTask<String, Void, JSONObject> {
	DefaultHttpClient httpClient;
	boolean tryAgain = false;
	//public static Context context1;
	//private ProgressDialog dialog; 
	
	public JsonLoader(Context context2) {
		/*if (context2 != null) {
			context1 = context2;
			dialog =  new ProgressDialog(context1);
		}*/
	}

	@Override
    protected void onPreExecute() {
		/*if (dialog != null) {
	        dialog.setMessage("Please wait..");
	        dialog.show();
		}*/
    }
    	
	@Override
	protected void onPostExecute(JSONObject result) {
		/*if (dialog != null) {
	    	if (dialog.isShowing()) {
	        	dialog.dismiss();
	    	}
		}*/
		
		super.onPostExecute(result);
	}

	public static JSONObject GetResponseObject(String url, Context context2) {
		JSONObject jsonObject;
		
		if (Looper.myLooper() == null) {
	        Looper.prepare();
	    }
		
		try {
			//url = java.net.URLEncoder.encode(url, "UTF-8");
			JsonLoader distributionServiceClient = new JsonLoader(context2);
			jsonObject = distributionServiceClient.execute(url).get();
			return jsonObject;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
			Log.i("TAG", e.getMessage());
		} finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return sb.toString();
    }
		
	@Override
    protected JSONObject doInBackground(String... webRequestUrl) {
		Log.i("TAG", webRequestUrl[0]);
		int tryAgainNumbers = 1;
		int connectionTimeout = 5000;
		
		if (webRequestUrl.length > 1) {
			tryAgainNumbers = Integer.valueOf(webRequestUrl[1]);
		}
		
		if (webRequestUrl.length > 2) {
			connectionTimeout = Integer.valueOf(webRequestUrl[2]);
		}
		
		for (int i = 0; i < tryAgainNumbers; i++) {
			try {
				HttpGet request = new HttpGet(webRequestUrl[0]);
				request.setHeader("Accept", "application/json");
				request.setHeader("Content-type", "application/json");
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
				HttpConnectionParams.setSoTimeout(httpParameters, connectionTimeout);
				
				if (httpClient == null) { 
					httpClient = new DefaultHttpClient(httpParameters);
				}
				
				HttpResponse response = httpClient.execute(request);
				HttpEntity entity = response.getEntity();
				
				if (entity != null) {
				    InputStream instream = entity.getContent();
				    String resultStr= convertStreamToString(instream);
				    instream.close();                   
				    return new JSONObject(resultStr);
				}
			
	    	} catch(HttpHostConnectException e){
	    		Log.i("TAG", "HttpHostConnectException: " + e.getMessage());
		    } catch(ConnectTimeoutException e){
		    	Log.i("TAG", "ConnectTimeoutException: " + e.getMessage());
			}catch(Exception e){
				Log.i("TAG", "Exception: " + e.getMessage());
			}
		}
    	
    	return null;
    }	
}