package net.obviam.droidz;

import net.obviam.droidz.model.Droid;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private MainThread thread;
	private Droid droid;
	private static final String TAG = MainGamePanel.class.getSimpleName();

	public MainGamePanel(Context context) {
		super(context);
		// call back this to the surface holder to intercept events.
		getHolder().addCallback(this);
		droid = new Droid(BitmapFactory.decodeResource(getResources(),
				R.drawable.droid_1), 50, 50);
		thread = new MainThread(getHolder(), this);
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.setRunning(true);
		thread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {

			}
		}

	}// surfaceDestroyed

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			droid.handleActionDown((int) event.getX(), (int) event.getY());
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity) getContext()).finish();
			} else {

				Log.d(TAG, "Coords: X=" + event.getX() + ", y=" + event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (droid.isTouched()) {
				droid.setX((int) event.getX());
				droid.setY((int) event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (droid.isTouched()) {
				droid.setTouched(false);
			}

		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
		// R.drawable.droid_1), 10, 10, null);
		canvas.drawColor(Color.BLACK);
		droid.draw(canvas);
	}

}
