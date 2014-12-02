package projectapp.braintangler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jw_mainscreen);
        
        //arrayList<> = new arrayList<>;
        
        //int a[];
        
        //for(int i = 0; a[i]; i++) {
        
        setupGame1Button();
        setupGame2Button();
        setupGame3Button();
    }
    
    private void setupGame1Button() {
    	Button button = (Button) findViewById(R.id.button1);
    	button.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			startActivity(new Intent(MainActivity.this, Game1.class));
    		}
    	});
    }
    
    private void setupGame2Button() {
    	Button button = (Button) findViewById(R.id.button2);
    	button.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			startActivity(new Intent(MainActivity.this, Game2.class));
    		}
    	});
    }
    
    private void setupGame3Button() {
    	Button button = (Button) findViewById(R.id.button3);
    	button.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			startActivity(new Intent(MainActivity.this, Game3.class));
    		}
    	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
