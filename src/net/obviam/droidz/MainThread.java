package net.obviam.droidz;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();
	private boolean running;
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
		while (running) {
			canvas = null;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					//update game state
					this.gamePanel.update();
					Log.d(TAG, "Game loop executed|||||||  canvas locked now");
					this.gamePanel.onDraw(canvas);
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
					Log.d(TAG,
							"Game loop executed__________ canvas got unlocked");
				}
			}// end finally
		}
		// Log.d(TAG, "Game loop executed " + tickCount + " times");
	}
}
