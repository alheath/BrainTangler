package projectapp.braintangler.memorygame;

import java.util.Random;

import projectapp.braintangler.R;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MemoryGame extends Thread {
//	public static final int EASY = 0;
//	public static final int MED = 1;
//	public static final int HARD = 2;
	
	private final SurfaceHolder sHolder;
	private final Handler mButtonHandler;
	private final Handler mScoreHandler;
	private final Handler mTimeHandler;
	
	private static final int READY = 0;
	private static final int PLAYING = 1;
	private static final int PAUSE = 2;
	private static final int GAME_OVER = 3;
	
	private int solved;
	private int state;
	private boolean run;
	private GameGrid gGrid;
	private boolean isNewGame;
	private MotionEvent mTouchEvent;
	
	private final int rows;
	private final int cols;
	private int bRow;
	private int bCol;

	private GameTimer gTimer;
	
	public MemoryGame(SurfaceHolder holder, Handler buttonHandler, Handler scoreHandler, 
			Handler timeHandler, int rows, int cols) {
		this.sHolder = holder;
		this.mButtonHandler = buttonHandler;
		this.mScoreHandler = scoreHandler;
		this.mTimeHandler = timeHandler;
		
		isNewGame = true;
		
		this.rows = rows;
		this.cols = cols;
		bRow = -1;
		bCol = -1;
		
		run = false;
		state = READY;
		
		gGrid = GameGrid.BLANK;
		solved = 0;
		
		gTimer = new GameTimer();
	}
	
	@Override
	public void run() {
		Looper.prepare();
		int skipTicks = 1000 / 20;
		long mNextGameTick = SystemClock.uptimeMillis();
		while (run) {
			Canvas canvas = null;
			try {
				canvas = sHolder.lockCanvas(null);
				if (canvas != null) {
					synchronized (sHolder) {
						if (state == PLAYING) {
							updateGame();
						}
						synchronized (new Object()) {
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
			mNextGameTick += skipTicks;
			long sleepTime = mNextGameTick - SystemClock.uptimeMillis();
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				// don't care
				}
			}
		}
		Looper.loop();
	}
	
	public int getScore() { return solved * 10; }
	
	private void initGrid() {
		gGrid = new GameGrid(rows, cols);
		int[] temp = {
				R.drawable.memorycard000,
				R.drawable.memorycard000,
				R.drawable.memorycard001,
				R.drawable.memorycard001,
				R.drawable.memorycard002,
				R.drawable.memorycard002,
				R.drawable.memorycard003,
				R.drawable.memorycard003,
				R.drawable.memorycard004,
				R.drawable.memorycard004,
				R.drawable.memorycard005,
				R.drawable.memorycard005,
				R.drawable.memorycard006,
				R.drawable.memorycard006,
				R.drawable.memorycard007,
				R.drawable.memorycard007,
		};
		
		shuffleArray(temp);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				MemoryCard x = new MemoryCard(temp[4*i+j], i, j);
				gGrid.setCardAt(x, i, j);
			}
		}	
		
		solved = 0;
	}
	
	public void startGame() {
		synchronized (sHolder) {
			initGrid();
			gTimer.start();
			setState(PLAYING);
		}
	}
	
	private void updateGame() {
		//Log.d("test", "update game");
		MemoryCard mc = gGrid.getCardAt(bRow,bCol);
		if (mc != null) Log.d("test", "" + bRow + " " + bCol);
		if (mc != null && mc.getState() == MemoryCard.FACE_DOWN) {
			MemoryCard selected = gGrid.getSelectedCard();
			if (selected != null) {
				gGrid.clearSelectedCard();
				if (selected.equals(mc)) {
					selected.setState(MemoryCard.SOLVED);
					mc.setState(MemoryCard.SOLVED);
					solved += 2;
					if (allSolved()) {
						solved += 2;
						updateDisplay();
						run = false;
						setState(GAME_OVER);
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
			Log.d("test", "set state");
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
		for (int row = 0; row < gGrid.getRows(); row++) {
			for (int col = 0; col < gGrid.getCols(); col++) {
				MemoryCard mc = gGrid.getCardAt(row, col);
				
				int image = mc.getCurrentImage();
				int buttonId = 4*row + col;
				
				setButtonImage(image, buttonId);				
				//Update button image
			}
		}
		setTimeText();
		setScore();
	}
	
	private void setButtonImage(int image, int buttonId) {
		 Message msg = mButtonHandler.obtainMessage();
		 Bundle b = new Bundle();
		 b.putInt("image", image);
		 b.putInt("button", buttonId);
		 msg.setData(b);
		 mButtonHandler.sendMessage(msg);	
	}
	
	private void setScore() {
		 Message msg = mScoreHandler.obtainMessage();
		 Bundle b = new Bundle();
		 b.putInt("score", solved * 10);
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
		run = running;
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
	  static void shuffleArray(int[] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
}
