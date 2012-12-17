package hack.sanitation.safe_sanitary_products;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.fedorvlasov.lazylist.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import hack.sanitation.safe_sanitary_products.entity.Code;
import hack.sanitation.safe_sanitary_products.entity.Product;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VerifiedItemActivity extends Activity {
	ImageView imgProduct;
	TextView tvProductName;
	TextView tvProductDescription;
	TextView tvProductDateManufacture;
	TextView tvProductDateExpiry;
	TextView tvProductManufacturer;
	ImageLoader imageLoader;
	Button btnItemTrue;
	Button btnItemFalse;
	private ProgressDialog pd;
	private static final String DEV_URL = "http://safesan.herokuapp.com/alert";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verified_item);
		tvProductName = (TextView) findViewById(R.id.tvProductName);
		tvProductDescription = (TextView) findViewById(R.id.tvProductDescription);
		tvProductDateManufacture = (TextView) findViewById(R.id.tvProductDateManufactur);
		tvProductDateExpiry = (TextView) findViewById(R.id.tvProductDateExpiry);
		tvProductManufacturer = (TextView) findViewById(R.id.tvProductManufacturer);
		imgProduct = (ImageView) findViewById(R.id.imgProduct);
		btnItemTrue = (Button) findViewById(R.id.btnItemTrue);
		btnItemTrue.setBackgroundColor(Color.GREEN);
		btnItemFalse = (Button) findViewById(R.id.btnItemFalse);
		btnItemFalse.setBackgroundColor(Color.RED);

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		final String jsonProduct = getIntent().getExtras().getString("product");
		final Product product = gson.fromJson(jsonProduct, Product.class);
		imageLoader = new ImageLoader(this);
		imageLoader.DisplayImage(product.getPhoto_url(), imgProduct);
		tvProductName.setText(product.getName());
		tvProductDescription.setText(product.getDescription());
		tvProductDateManufacture.setText(product.getManufactureDate());
		tvProductDateExpiry.setText(product.getExpiryDate());
		tvProductManufacturer.setText(product.getManufactuers_name());

		btnItemTrue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displayMessage("Thank you for using  sansafe.");
				Intent intent = new Intent(VerifiedItemActivity.this,
						SubmitCode.class);
				startActivity(intent);
			}
		});

		btnItemFalse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SendComplaintToServerTask task = new SendComplaintToServerTask();
				task.execute(new Code(product.getProduct_code(),
						new LocationInfo(getBaseContext())));
			}
		});

	}

	void displayMessage(String message) {
		Toast messageToast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_LONG);
		messageToast.setGravity(Gravity.TOP, 0, 0);
		messageToast.show();
	}

	class SendComplaintToServerTask extends AsyncTask<Code, Void, Product> {

		@Override
		protected void onPostExecute(Product result) {
			super.onPostExecute(result);

			pd.dismiss();
			Intent intent = new Intent(VerifiedItemActivity.this,
					SubmitCode.class);
			displayMessage("Alert sent");
			startActivity(intent);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(VerifiedItemActivity.this, "Loading..",
					"Sending alert", true, false);
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
			postParams.add(new BasicNameValuePair("latitude", params[0]
					.getLatitude() + ""));
			postParams.add(new BasicNameValuePair("longitude", params[0]
					.getLongitude() + ""));

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

			return null;

		}

		public String getStringFromBuffer(BufferedReader bRead) {

			String line = null;
			StringBuffer theText = new StringBuffer();
			try {
				while ((line = bRead.readLine()) != null) {
					theText.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return theText.toString();
		}

	}
}
