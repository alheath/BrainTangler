package projectapp.braintangler.memorygame;

import android.os.CountDownTimer;
import android.util.Log;

public class GameTimer {
	long millisInit;
	long millisRemaining;
	long countDownInterval;
	long elapsed;
	
	boolean isPaused = true;
	
	CountDownTimer timer = null;

	public GameTimer() {
		millisRemaining = millisInit = 30000;
		countDownInterval = 1000;
		elapsed = 0;
	}
	private void createGameTimer() {
		timer = new CountDownTimer(millisRemaining, countDownInterval) {
			@Override
			public void onFinish() {
				elapsed = millisInit;
			}

			@Override
			public void onTick(long millisUntilFinished) {
				//millisRemaining = millisUntilFinished;
				millisRemaining = millisUntilFinished;;
				//Log.d("test", toString());
			}
		};
	}
	
	public final void cancel() {
		if(timer!=null){
			timer.cancel();
		}
		this.millisRemaining = 0;
	}
	
	public synchronized final GameTimer start(){
        if(isPaused){
            createGameTimer();
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
    
    public long elapsed() {
    	return elapsed;
    }
	public String toString() {
		return "" + millisRemaining / 60000 + ":" + millisRemaining / 1000 % 60;
	}
}
