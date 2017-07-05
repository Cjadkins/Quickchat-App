package quickchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class HttpAsyncTask extends AsyncTask<String, Void, String> {
	private static String serverURL = "http://quickchat.t15.org/";
	private static HttpClient httpclient = new DefaultHttpClient();
	private static HttpContext localContext = new BasicHttpContext();
	private static boolean setupCookie = false;
	private CompletedTasks onComplete;

	public HttpAsyncTask(CompletedTasks onComplete) {
		this.onComplete = onComplete;

		if (!setupCookie) {
			localContext.setAttribute(ClientContext.COOKIE_STORE,
					new BasicCookieStore());
			setupCookie = true;
		}
	}

	@Override
	protected String doInBackground(String... data) {
		ArrayList<String> params = new ArrayList<String>(data.length - 1);

		for (int paramNum = 1; paramNum < data.length; ++paramNum) {
			params.add(data[paramNum]);
		}

		return POST(serverURL + data[0], params);
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		
		onComplete.callBack(result);

	}

	public static String POST(String url, ArrayList<String> params) {
		InputStream inputStream = null;
		String result = "";

		try {
			// create HttpClient
			httpclient = new DefaultHttpClient();

			HttpPost post = new HttpPost(url);

			if (!setupCookie) {
				// Create local HTTP context
				localContext = new BasicHttpContext();
				// Bind custom cookie store to the local context
				localContext.setAttribute(ClientContext.COOKIE_STORE,
						new BasicCookieStore());
				setupCookie = true;
			}

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			for (int paramNum = 0; paramNum < params.size(); paramNum += 2) {
				if (paramNum + 1 < params.size()) {
					postParameters.add(new BasicNameValuePair(params
							.get(paramNum), params.get(paramNum + 1)));
				}
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters, "UTF-8");
			post.setEntity(formEntity);

			// make POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(post, localContext);

			// receive response as inputStream
			HttpEntity he = httpResponse.getEntity();
			String realData = org.apache.http.util.EntityUtils.toString(he);
			//inputStream = he.getContent();

			
				result = realData;
			

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		String line = "";
		String result = "";

		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}

		inputStream.close();

		return result;
	}

	public boolean isConnected(Activity act) {
		ConnectivityManager connMgr = (ConnectivityManager) act
				.getSystemService(act.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

}