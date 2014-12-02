package projectapp.braintangler.memorygame;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.util.Log;

public class GameTimer {
	long millisInit;
	long millisRemaining;
	
	boolean isPaused = true;
	
	CountDownTimer timer = null;

	public GameTimer() {
		millisRemaining = millisInit = 30000;
	}
	private void createGameTimer(long millis) {
		timer = new CountDownTimer(millis, 1000) {
			@Override
			public void onFinish() {
				millisRemaining = 0;
			}

			@Override
			public void onTick(long millisUntilFinished) {
				millisRemaining = millisUntilFinished;
			}
		};
	}
	
	public final void cancel() {
		if(timer!=null){
			timer.cancel();
		}
		
	}
	
	public synchronized final GameTimer start(){
        if(isPaused){
            createGameTimer(millisRemaining);
            timer.start();
            isPaused = false;
        }
        Log.d("test", toString());
        return this;
    }
	
	public synchronized final GameTimer add(long mill){
        if(isPaused){
            createGameTimer(mill+millisRemaining);
            timer.start();
            isPaused = false;
        }
        Log.d("test", toString());
        return this;
    }
	
	public void pause()throws IllegalStateException{
        if(!isPaused){
            timer.cancel();
        } else{
            throw new IllegalStateException("GameTimer is already in pause state, start counter before pausing it.");
        }
        isPaused = true;
    }
	
    public boolean isPaused() {
        return isPaused;
    }
    
	@SuppressLint("DefaultLocale") 
	public String toString() {
		int m = (int) (millisRemaining / (1000*60));
		int s = (int) (millisRemaining / 1000) % 60 ;
		return String.format("%02d:%02d", m, s);
	}
}
