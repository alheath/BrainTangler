package projectapp.braintangler.memorygame;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import projectapp.braintangler.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MemoryGame extends Thread {
	
	private final SurfaceHolder sHolder;
	private final Handler mButtonHandler;
	private final Handler mScoreHandler;
	private final Handler mTimeHandler;
	
	private static final int READY = 0;
	private static final int PLAYING = 1;
	private static final int PAUSE = 2;
	private static final int GAME_OVER = 3;
	
	private int solved;
	private int points;
	private int state;
	private boolean run;
	private final Object runLock;
	private GameGrid gGrid;
	private MotionEvent mTouchEvent;
	
	private int rows;
	private int cols;
	private int bRow;
	private int bCol;

	private GameTimer gTimer;
	private int level;
	
	//private BTCountDownTimer gameTimer;
	
	public MemoryGame(SurfaceHolder holder, Handler buttonHandler, Handler scoreHandler, 
			Handler timeHandler, int level) {
		this.sHolder = holder;
		this.mButtonHandler = buttonHandler;
		this.mScoreHandler = scoreHandler;	
		this.mTimeHandler = timeHandler;
		
		bRow = -1;
		bCol = -1;
		
		run = false;
		runLock = new Object();
		
		state = READY;
		
		gGrid = GameGrid.BLANK;
		solved = 0;
		points = 0;
		
		gTimer = new GameTimer();
		
		setGridDims(level);
		this.level = level;
	}
	
	@Override
	public void run() {
		//int skipTicks = 1000 / 20;
		//long mNextGameTick = SystemClock.uptimeMillis();
		while (run) {
			Canvas canvas = null;
			try {
				canvas = sHolder.lockCanvas(null);
				if (canvas != null) {
					synchronized (sHolder) {
						if (state == PLAYING) {
							updateGame();
						}
						synchronized (runLock) {
							if (run) {
								updateDisplay();
							}
						}
					}
				}
			} finally {
				if (canvas != null) {
					sHolder.unlockCanvasAndPost(canvas);
				}
			}
//			mNextGameTick += skipTicks;
//			long sleepTime = mNextGameTick - SystemClock.uptimeMillis();
//			if (sleepTime > 0) {
//				try {
//					Thread.sleep(sleepTime);
//				} catch (InterruptedException e) {
//				// don't care
//				}
//			}
			
		}
	}
	
	public int getScore() { return solved * 10; }
	
	private void initGrid() {
		gGrid = new GameGrid(rows, cols);
		
		int[] colors = new int[rows*cols];
		
		for(int i = 0; i < (rows*cols / 2); i++){
			colors[2*i] = i;
			colors[2*i + 1] = i;
		}
		
		shuffleArray(colors);
		
		Log.d("test", "size of colors array is: " + colors.length);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				MemoryCard x = new MemoryCard(colors[cols*i+j], i, j);
				gGrid.setCardAt(x, i, j);
				//Log.d("test", "" + colors[cols*i+j]);
			}
		}
		
		
	}
	
	public void startGame() {
		synchronized (sHolder) {
			initGrid();
			gTimer.start();
			setState(PLAYING);
		}
	}
	
	public void startGame(long addTime) {
		synchronized(sHolder) {
			initGrid();
			gTimer.add(30000);
			setState(PLAYING);
		}
	}
	
	private void updateGame() {
		//Log.d("test", "update game");
		MemoryCard mc = gGrid.getCardAt(bRow,bCol);
		if (mc != null && mc.getState() == MemoryCard.FACE_DOWN) {
			MemoryCard selected = gGrid.getSelectedCard();
			if (selected != null) {
				gGrid.clearSelectedCard();
				if (selected.equals(mc)) {
					selected.setState(MemoryCard.SOLVED);
					mc.setState(MemoryCard.SOLVED);
					
					solved += 2;
					points += 10;
					if (allSolved()) {
						points += 40;
						updateDisplay();
						pause();
					}
				} else {
					gGrid.selectCard(mc);
				}
			} 
			else gGrid.selectCard(mc);
		}
	}
	
	private void setState(int state) {
		synchronized (sHolder) {
			this.state = state;
		}
//		if(state == GAME_OVER) {
//			long elapsed = gTimer.elapsed() / 1000;
//			long minutes = elapsed / 60;
//			long seconds = elapsed % 60;
//			StringBuilder scoreText = new StringBuilder();
//			scoreText.append("solved in").append(' ');
//			
//			if (minutes > 0) {
//				scoreText.append(minutes).append(' ').append(minutes == 1 ?
//						"min" : "mins").append(' ');
//			}
//			scoreText.append(seconds)
//			.append(' ')
//			.append(seconds == 1 ?
//			"sec" : "seconds");
//
//			
//			
//			//setScore(scoreText.toString());
//		}
		
	}

	private void updateDisplay() {
		if (state == PLAYING) {
			
			for (int m = 0; m < gGrid.getRows(); m++) {
				for (int n = 0; n < gGrid.getCols(); n++) {
					MemoryCard mc = gGrid.getCardAt(m, n);
					
					int image = mc.getCurrentImage();
					int buttonId = 6*m + n;
						
					setButtonImage(image, buttonId);
					
//					if (mc.getState() == MemoryCard.SOLVED && delay <= 0) {
//						mc.setFaceUpImage(R.drawable.transparent);
//					}
				}
			}
			setTimeText();
			setScore();
		}
	}
	
	private void setButtonImage(int image, int buttonId) {
		 Message msg = mButtonHandler.obtainMessage();
		 Bundle b = new Bundle();
		 b.putInt("image", image);
//		 b.putInt("row", rows);
//		 b.putInt("col", cols);
		 b.putInt("button", buttonId);
		 msg.setData(b);
		 mButtonHandler.sendMessage(msg);	
	}
	
	private void setScore() {
		 Message msg = mScoreHandler.obtainMessage();
		 Bundle b = new Bundle();
		 b.putBoolean("allsolved", allSolved());
		 b.putInt("score", points);
		 msg.setData(b);
		 mScoreHandler.sendMessage(msg);	
	}
	
	private void setTimeText() {
		 Message msg = mTimeHandler.obtainMessage();
		 String message = gTimer.toString();
		 
		 msg.obj = message;
		 mTimeHandler.sendMessage(msg);
	}

	public void setRunning(boolean running) {
		synchronized (runLock) {
			run = running;
		}
	}
	
	public boolean allSolved() {
		return solved == gGrid.getRows() * gGrid.getCols();
	}
	
	public boolean onBack() {
		 synchronized (sHolder) {
			 if (state == PLAYING) {
				 pause();
				 return false;
			 }
			 return true;
		}
	}
	
	public void pause() {
		synchronized (sHolder) {
			if (state == PLAYING) {
				gTimer.pause();
				setState(PAUSE);
			}
		}
	}
	
	public void unPause() {
		synchronized (sHolder) {
			gTimer.start();
			setState(PLAYING);
		}
	}
	
	public void setButtonLocs(int row, int col) {
		bRow = row;
		bCol = col;
		//flipped = true;
	}

	 public void onTouch(MotionEvent event) {
		 synchronized (sHolder) {
			 switch (state) {
			 	case READY:
			 		startGame();
			 		break;
			 	case PLAYING:
			 		if (mTouchEvent == null) {
			 			mTouchEvent = event;
			 		}
			 		break;
			 	case PAUSE:
			 		unPause();
			 		break;
			 	case GAME_OVER:
			 		setState(READY);
			 }
		 }
		 
	}
	 
	private void setGridDims(int level)
	{
		switch(level)
		{
		case 0:
			this.rows = 4;
			this.cols = 2;
			break;
		case 1:
			this.rows = 4;
			this.cols = 3;
			break;
		case 2:
			this.rows = 4;
			this.cols = 4;
			break;
		case 3:
			this.rows = 5;
			this.cols = 4;
			break;
		case 4:
			this.rows = 6;
			this.cols = 4;
			break;
		case 5:
			this.rows = 6;
			this.cols = 5;
			break;
		default:
			this.rows = 6;
			this.cols = 6;
		}
	}
	
	public void nextLevel()
	{
		pause();
		synchronized (sHolder) {
			bRow = -1;
			bCol = -1;
			
			solved = 0;
			
			gGrid = GameGrid.BLANK;

			setGridDims(++level);
		}
	}
	
	static void shuffleArray(int[] ar) {
		Random rnd = new Random();
		rnd.nextInt();
	    for (int i = 0; i < ar.length; i++)
	    {
	      int index = i + rnd.nextInt(ar.length - i);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	}

	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}

}
