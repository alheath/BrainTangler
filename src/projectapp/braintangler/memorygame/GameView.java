package projectapp.braintangler.memorygame;

import projectapp.braintangler.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	 
	private ImageButton[] buttons;
	private TextView timeView;
	private TextView scoreView;
	private MemoryGame mGame;
	
	private int rows = 0;
	private int cols = 0;
	private int width = 0;
	private int height = 0;
	
	private Handler scoreH = null;
	private Handler timeH = null;
	private Handler buttonsH = null;
	
	private Drawable[] imgs = null;
	
	int[] cardIds = {
			R.drawable.card0,
			R.drawable.card1,
			R.drawable.card2,
			R.drawable.card3,
			R.drawable.card4,
			R.drawable.card5,
			R.drawable.card6,
			R.drawable.card7,
			R.drawable.card8,
			R.drawable.card9,
			R.drawable.card10,
			R.drawable.card11,
			R.drawable.card12,
			R.drawable.card13,
			R.drawable.card14,
			R.drawable.card15,
			R.drawable.card16,
			R.drawable.card17,
			R.drawable.cardhidden
	};
	//TODO: create array/object that maps level to size of button
//	public GameView(Context context) {
//		super(context);
//		init();
//    }
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mGame.setRunning(true);
		mGame.start();
	}
	 
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		mGame.setRunning(false);
	    while (retry) {
	    	try {
	    		mGame.join();
	    		retry = false;
	    	} catch (InterruptedException e) {
	    		// try again shutting down the thread
	    	}
	    }
	}
	 
	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP && buttonsH != null) {
			mGame.onTouch(event);
		}
		return true;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
	}
	    
	public MemoryGame getGameThread() {
		return mGame;
	}
	
	private void init() {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		setBackgroundResource(R.drawable.woodtexture);
		initHandlers();
		// create the game loop thread
		mGame = new MemoryGame(holder, buttonsH, scoreH, timeH, 0);
		rows = mGame.getRows();
		cols = mGame.getCols();
		
		initImgArray(rows, cols);
		//TODO: pass int "level" to game: this determines # of rows and cols.
		setFocusable(true);
	}
	
	private void initHandlers() {
		scoreH = new Handler( new Handler.Callback()  {
			@Override
			public boolean handleMessage(Message m) {
				scoreView.setText("score: " + m.getData().getInt("score"));
				if (m.getData().getBoolean("allsolved")) 
				{
					mGame.nextLevel();
					mGame.startGame(30000);
					rows = mGame.getRows();
					cols = mGame.getCols();	
					initImgArray(rows, cols);
				}
				return true;
			}
		} );
		
		buttonsH = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message m) {
					int temp = m.getData().getInt("button");
					ImageButton imgBut = buttons[temp];
					imgBut.setBackground(imgs[m.getData().getInt("image")]);
					return true;
			}
		} );
		
		timeH = new Handler( new Handler.Callback()  {
			@Override
			public boolean handleMessage(Message m) {
				String msg = (String) m.obj;
				timeView.setText(msg);
				if (timeView.getText().toString().equals("00:00")) {
					//if time == 0;
					mGame.pause();
					createAlertDialog();
				}
				return true;
			}
		} );
		
	}

	private void initImgArray(int rows, int cols) {
		imgs = new Drawable[19];
		
		if (width == 0 || height == 0) {
			for (int i = 0; i < cardIds.length; i++)
				imgs[i] = loadImage(cardIds[i], 90, 150);
		}
		else
			for (int i = 0; i < cardIds.length; i++)
				imgs[i] = loadImage(cardIds[i], width / cols, height / rows);
	}

	public void setButtons(ImageButton[] buttons) {
		this.buttons = buttons;
		//Log.d("test", "are the buttons equal to nil? ..." + Boolean.valueOf(buttons == null));
	}
	
	public void setScoreView(TextView score) {
		this.scoreView = score;
	}
	public void setTimeView(TextView time) {
		this.timeView = time;
	}
	
	public void createAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getContext());
 
		// set title
		alertDialogBuilder.setTitle("You are out of time!");
 
		// set dialog message
		alertDialogBuilder
			.setMessage("Play again?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					//restart the game from beginning
					dialog.cancel();
					mGame.startGame();
				} 
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, restart from the main menu
					Activity a = (Activity) getContext();
					
					Intent i = a.getIntent();
					i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					
					a.finish();
					
					a.overridePendingTransition(0, 0);
					getContext().startActivity(i);
					
					
				}
			});
 
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
		// show it
		alertDialog.show();
	}
	

	
    public Drawable loadImage(int picId, int newWidth, int newHeight) {
    	Bitmap bMap = BitmapFactory.decodeResource(getResources(), picId);
    	Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, true);

    	//Log.d("test", "" + picId + " " + newWidth + "");
    	return new BitmapDrawable(getResources(),bMapScaled);
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
    
	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  // TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (width == 0 || height == 0) {
			width = getWidth();
			height = getHeight();
			initImgArray(rows, cols);
		}
		
	 }
}

