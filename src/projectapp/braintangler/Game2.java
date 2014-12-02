package projectapp.braintangler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Game2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game2);
		
	//	setupDifficultyButton();
	//	setupInstructionsButton();
	}
	/*
	private void setupDifficultyButton() {
    	Button button = (Button) findViewById(R.id.button1);
    	button.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			startActivity(new Intent(Game2.this, DifficultySettings.class));
    		}
    	});
    }*/
	/*
	private void setupInstructionsButton() {
    	Button button = (Button) findViewById(R.id.button2);
    	button.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			startActivity(new Intent(Game2.this, Instructions1.class));
    		}
    	});
    }*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
