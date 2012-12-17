package hack.sanitation.safe_sanitary_products;

import hack.sanitation.safe_sanitary_products.entity.Code;
import hack.sanitation.safe_sanitary_products.entity.Product;
import hack.sanitation.safe_sanitary_products.helper.IntentIntegrator;
import hack.sanitation.safe_sanitary_products.helper.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

public class SubmitCode extends Activity implements OnClickListener {
	Button btnScanCode;
	TextView tvVerificationCode;
	SharedPreferences settings = null;
	private ProgressDialog pd;
	public static final String PREFS_SANSAFE = "sansafe-app";
	public static final String DEV_URL = "http://safesan.herokuapp.com/verify";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocationLibrary.initialiseLibrary(getBaseContext(),
				"hack.sanitation.safe_sanitary_products");
		setContentView(R.layout.activity_submit_code);
		btnScanCode = (Button) findViewById(R.id.btnScanCode);
		btnScanCode.setOnClickListener(this);
		settings = getSharedPreferences(PREFS_SANSAFE, MODE_PRIVATE);
		tvVerificationCode = (TextView) findViewById(R.id.tvVerificationCode);
		LocationInfo latestInfo = new LocationInfo(getBaseContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_submit_code, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		IntentIntegrator integrator = new IntentIntegrator(SubmitCode.this);
		integrator.initiateScan();

	}

	void displayMessage(String message) {
		Toast messageToast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_LONG);
		messageToast.setGravity(Gravity.TOP, 0, 0);
		messageToast.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			tvVerificationCode.setText(scanResult.getContents());
			SendCodeToServerTask task = new SendCodeToServerTask();
			task.execute(new Code(scanResult.getContents(), new LocationInfo(
					getBaseContext())));
		}
	}

	class SendCodeToServerTask extends AsyncTask<Code, Void, Product> {

		@Override
		protected void onPostExecute(Product result) {
			super.onPostExecute(result);
			if (result.isFake()) {
				// Intent intent = new Intent(SubmitCode.this,
				// FakeItemActivity.class);
				// // intent.putExtra("id", position);
				// startActivity(intent);

				displayMessage("The code scanned is not valid. The product may not ne genuine.");
				tvVerificationCode.setBackgroundColor(Color.RED);

			} else {
				tvVerificationCode.setBackgroundColor(Color.GREEN);
				Intent intent = new Intent(SubmitCode.this,
						VerifiedItemActivity.class);
				Gson gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation().create();
				String jsonProduct = gson.toJson(result);

				intent.putExtra("product", jsonProduct);
				startActivity(intent);
			}
			pd.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(SubmitCode.this, "Loading..",
					"Authenticating the code", true, false);
		}

		@Override
		protected Product doInBackground(Code... params) {
			Gson gson = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create();
			String jsonCode = gson.toJson(params[0]);

			HttpPost post = new HttpPost(DEV_URL);

			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("actual_code", params[0]
					.getActualCode()));

			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						postParams);
				entity.setContentEncoding(HTTP.UTF_8);
				entity.setContentType("application/json");
				post.setEntity(entity);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			post.setHeader("Content-type", "application/json");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			try {
				response = client.execute(post);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String rep = getStringFromBuffer(br);
			Log.e("DERE", rep);
			Product obj = gson.fromJson(rep, Product.class);
			return obj;

		}

		public String getStringFromBuffer(BufferedReader bRead) {

			String line = null;
			StringBuffer theText = new StringBuffer();
			try {
				while ((line = bRead.readLine()) != null) {
					theText.append(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return theText.toString();
		}

	}

}
