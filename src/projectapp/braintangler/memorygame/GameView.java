package projectapp.braintangler.memorygame;

import android.content.Context;
import android.graphics.Canvas;
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
	
	private final int rows = 4;
	private final int cols = 4;

//	public GameView(Context context) {
//		super(context);
//		init();
//    }
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	 
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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
	 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
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
		
		
		// create the game loop thread
		mGame = new MemoryGame(holder, new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message m) {
					ImageButton imgBut = buttons[m.getData().getInt("button")];
					imgBut.setBackgroundResource(m.getData().getInt("image"));
					return true;
			}
			} ),
			new Handler( new Handler.Callback()  {
				@Override
				public boolean handleMessage(Message m) {
					scoreView.setText("score: " + m.getData().getInt("score"));
					return true;
				}
			} ),
			new Handler( new Handler.Callback()  {
				@Override
				public boolean handleMessage(Message m) {
					String msg = (String) m.obj;
					timeView.setText(msg);
					
					return true;
				}
			}), rows, cols);
		setFocusable(true);
	}
	
	public void setButtons(ImageButton[] buttons) {
		this.buttons = buttons;
	}
	
	public void setScoreView(TextView score) {
		this.scoreView = score;
	}
	public void setTimeView(TextView time) {
		this.timeView = time;
	}
	
	

}