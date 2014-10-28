package projectapp.braintangler.memorygame;

import projectapp.braintangler.R;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class MemoryGameActivity extends Activity {
	
	private MemoryGame mGame;
	private ImageButton[] buttons;
	private TextView scoreView;
	private TextView timeView;
	
	private int rows = 4;
	private int cols = 4;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        setButtons();
        scoreView = (TextView) findViewById(R.id.score);
        timeView = (TextView) findViewById(R.id.time);
        GameView gView = (GameView) findViewById(R.id.main);
        gView.setButtons(buttons);
        gView.setScoreView(scoreView);
        gView.setTimeView(timeView);
        
        mGame = gView.getGameThread();
    }


    @Override
    protected void onPause() {
    	super.onPause();
    	mGame.pause();
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
    
    @Override
    public void onBackPressed(){
    	if (mGame.onBack()) {
    		finish();
    	}
    }
    
    public void setButtons() {
    	buttons = new ImageButton[16];
		int idArray[] = {
				R.id.card00,
				R.id.card01,
				R.id.card02,
				R.id.card03,
				R.id.card04,
				R.id.card05,
				R.id.card06,
				R.id.card07,
				R.id.card08,
				R.id.card09,
				R.id.card10,
				R.id.card11,
				R.id.card12,
				R.id.card13,
				R.id.card14,
				R.id.card15
		};
		
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				final int m = i;
				final int n = j;
				
				buttons[rows*i+j] = (ImageButton) findViewById(idArray[rows*i+j]);
				
				buttons[rows*i+j].setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						Log.d("test", "button works");
						mGame.setButtonLocs(m,n);
					}
				});
			}
	}

}

