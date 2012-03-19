package net.obviam.droidz;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();
	private boolean running;
	
	
	//desired fps	
	private final static int MAX_FPS = 50;
	//maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	//the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	
	//for statiticss
	private DecimalFormat df = new DecimalFormat("0.##");
	private final static int STAT_INTERVAL 	= 1000;
	private final static int FPS_HISTORY_NR = 10;
	private long lastStatusStore 		= 0;
	private long statusIntervalTimer 	= 0l;
	private long totalFramesSkipped 	= 0l;
	private long framesSkippedPerStatCycle =0l;
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFps = 0.0;
	
	
	private SurfaceHolder surfaceHolder;
	private MainGamePanel gamePanel;
	

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super();

		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		Canvas canvas;
		// long tickCount = 0L;
		Log.d(TAG, "Starting game loop");
		//initializing timeing elements
		initTimingElements();
		
		long beginTime; 	//time when the cycle begun
		long timeDiff; 		//the time it took for the cycle to execute
		long sleepTime; 	//ms to sleep
		int framesSkipped; 	//number of frames skipped
		
		sleepTime=0;
		while (running) {
			canvas = null;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;
					//update game state
					this.gamePanel.update();
					Log.d(TAG, "Game loop executed| | | | | | |  canvas locked now");
					this.gamePanel.onDraw(canvas);
					timeDiff = System.currentTimeMillis() - beginTime;
					//calculate sleep time
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					if(sleepTime>0){
						//>0 sleep time is ok
						try{
						Thread.sleep(sleepTime);
						}catch(InterruptedException e){}
					}
					while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS){
						this.gamePanel.update();
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
					if(framesSkipped > 0){
						Log.d(TAG, "Skipped: " + framesSkipped);
					}
					//for statistics
					framesSkippedPerStatCycle +=framesSkipped;
					storeStats();
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
					Log.d(TAG,
							"Game loop executed__________canvas got unlocked");
				}
			}// end finally
		}
		// Log.d(TAG, "Game loop executed " + tickCount + " times");
	}
	
	private void storeStats() {
		frameCountPerStatCycle++;
		totalFrameCount++;
		// assuming that the sleep works each call to storeStats
		// happens at 1000/FPS so we just add it up
//		statusIntervalTimer += FRAME_PERIOD;
		
		// check the actual time
		statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);
		
		if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL) {
			// calculate the actual frames pers status check interval
			double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));
			
			//stores the latest fps in the array
			fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;
			
			// increase the number of times statistics was calculated
			statsCount++;
			
			double totalFps = 0.0;
			// sum up the stored fps values
			for (int i = 0; i < FPS_HISTORY_NR; i++) {
				totalFps += fpsStore[i];
			}
			
			// obtain the average
			if (statsCount < FPS_HISTORY_NR) {
				// in case of the first 10 triggers
				averageFps = totalFps / statsCount;
			} else {
				averageFps = totalFps / FPS_HISTORY_NR;
			}
			// saving the number of total frames skipped
			totalFramesSkipped += framesSkippedPerStatCycle;
			// resetting the counters after a status record (1 sec)
			framesSkippedPerStatCycle = 0;
			statusIntervalTimer = 0;
			frameCountPerStatCycle = 0;

			statusIntervalTimer = System.currentTimeMillis();
			lastStatusStore = statusIntervalTimer;
//			Log.d(TAG, "Average FPS:" + df.format(averageFps));
			gamePanel.setAvgFps("FPS: " + df.format(averageFps));
		}
	}
	
	private void initTimingElements(){
		//initialize timing elements
		fpsStore = new double[FPS_HISTORY_NR];
		for (int i =0; i< FPS_HISTORY_NR; i++){
			fpsStore[i] = 0.0;
		}
		Log.d(TAG + ".initTimingElements()", "Timing elements for stats initialised");
	}
}
