package dk.au.cs.skatespots;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SkateSpotsHttpClient {

	private static final String BASE_URL = "http://bufo.avalonia.dk:11337";
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void post(Context context, JsonObject jsonobj,
			AsyncHttpResponseHandler responseHandler) {
		try {
			String bodyAsJson = jsonobj.toString();
			ByteArrayEntity entity = new ByteArrayEntity(bodyAsJson.getBytes("UTF-8"));
			client.post(context, BASE_URL, entity, "application/json", responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
