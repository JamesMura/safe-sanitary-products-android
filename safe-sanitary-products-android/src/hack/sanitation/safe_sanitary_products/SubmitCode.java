package hack.sanitation.safe_sanitary_products;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SubmitCode extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_code);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_submit_code, menu);
		return true;
	}

}
