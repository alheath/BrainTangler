package projectapp.braintangler.memorygame;

import projectapp.braintangler.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;


public class MemoryGameActivity extends Activity {
	
	private MemoryGame mGame;
	private ImageButton[] buttons;
	private ImageButton[] test;
	private TextView scoreView;
	private TextView timeView;
	
	private int rows = 6;
	private int cols = 6;
	
	
	private RelativeLayout rl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        //setButtons();
        //addButtons();
        addButtons();
        scoreView = (TextView) findViewById(R.id.score);
        timeView = (TextView) findViewById(R.id.time);
        GameView gView = (GameView) findViewById(R.id.main);
        
        gView.setScoreView(scoreView);
        gView.setTimeView(timeView);
        gView.setButtons(test);
        mGame = gView.getGameThread();
//        rows = mGame.getRows();
//        cols = mGame.getCols();
        
        
        //gView.setButtons(test);
    }


    @Override
    protected void onPause() {
    	super.onPause();
    	mGame.pause();
    }

    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "New Game");
        menu.add(0, 2, 0, "Resume");
        menu.add(0, 3, 0, "Exit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
        	case 1:
        		mGame.startGame();
        		break;
        	case 2:
	        	mGame.unPause();
	            break;
	        case 3:
	            finish();
	    }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed(){
    	if (mGame.onBack()) {
    		finish();
    	}
    }
    
//    public void setButtons() {
//    	buttons = new ImageButton[16];
//		int idArray[] = {
//				R.id.card00,
//				R.id.card01,
//				R.id.card02,
//				R.id.card03,
//				R.id.card04,
//				R.id.card05,
//				R.id.card06,
//				R.id.card07,
//				R.id.card08,
//				R.id.card09,
//				R.id.card10,
//				R.id.card11,
//				R.id.card12,
//				R.id.card13,
//				R.id.card14,
//				R.id.card15
//		};
//		
//		
//		for (int i = 0; i < rows; i++)
//			for (int j = 0; j < cols; j++) {
//				final int m = i;
//				final int n = j;
//				
//				buttons[rows*i+j] = (ImageButton) findViewById(idArray[rows*i+j]);
//				//buttons[rows*i+j].set
//				
//				buttons[rows*i+j].setOnClickListener(new View.OnClickListener() {
//					public void onClick(View view) {
//						mGame.setButtonLocs(m,n);
//					}
//				});
//			}
//	}
    

    public void addButtons()
    {
    	rl = (RelativeLayout) findViewById(R.id.rl);

    	test = new ImageButton[rows*cols];
    	for (int i = 0; i < rows; i++) {
    		//RelativeLayout row = new RelativeLayout(this);
    		//LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	    //row.setLayoutParams(lp);
    		for (int j = 0; j < cols; j++) {
    			final int m = i;
    			final int n = j;
    			
    			ImageButton myButton = new ImageButton(this);
    			myButton.setId(cols*i+j + 1);
    			myButton.setContentDescription(getResources().getString(R.string.desc));
    			
    			LayoutParams l = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    			if (i == 0) l.addRule(RelativeLayout.BELOW, R.id.score);
    			else l.addRule(RelativeLayout.BELOW, test[cols*(i-1)+j].getId()); //rows*(i-1)+j + 1);
    			if (j == 0) l.addRule(RelativeLayout.ALIGN_LEFT, R.id.main);
    			else l.addRule(RelativeLayout.RIGHT_OF, test[cols*i+j-1].getId()); //rows*i + j -1 );
    			
    			myButton.setLayoutParams(l);
    			myButton.setBackgroundResource(android.R.color.transparent);
    			myButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						mGame.setButtonLocs(m,n);
					}
				});
    			
    			test[cols*i+j] = myButton;
    			//row.addView(myButton);
    			
    			Log.d("test", "i is "+ i + "; j is " + j + ".");
    			rl.addView(myButton);
    		}
    		
    	}
    }
    

}
