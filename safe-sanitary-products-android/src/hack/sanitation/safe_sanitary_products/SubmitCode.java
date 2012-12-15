package hack.sanitation.safe_sanitary_products;

import hack.sanitation.safe_sanitary_products.helper.IntentIntegrator;
import hack.sanitation.safe_sanitary_products.helper.IntentResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitCode extends Activity implements OnClickListener {
	Button btnScanCode;
	TextView tvVerificationCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_code);
		btnScanCode = (Button) findViewById(R.id.btnScanCode);
		btnScanCode.setOnClickListener(this);
		tvVerificationCode = (TextView) findViewById(R.id.tvVerificationCode);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
			displayMessage(scanResult.toString());
			tvVerificationCode.setText(scanResult.getContents());
		}
		// else continue with any other code you need in the method
	}

	class SendCodeToServerTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPostExecute(String[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
