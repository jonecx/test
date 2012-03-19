package net.obviam.droidz;

import net.obviam.droidz.model.Droid;
import net.obviam.droidz.model.components.Speed;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private MainThread thread;
	private Droid droid;
	private static final String TAG = MainGamePanel.class.getSimpleName();
	private String avgFps;

	public MainGamePanel(Context context) {
		super(context);
		// call back this to the surface holder to intercept events.
		getHolder().addCallback(this);
		droid = new Droid(BitmapFactory.decodeResource(getResources(),
				R.drawable.droid_1), 50, 50);
		thread = new MainThread(getHolder(), this);
		setFocusable(true);
	}

	//@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.setRunning(true);
		thread.start();

	}

	//@Override
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
	
	
	public void update(){
		//check collision with the right wall if heading fright
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT && droid.getX() + droid.getBitmap().getWidth()/ 2 >= getWidth()){
			droid.getSpeed().toggleXDirection();
		}
		//check collision with left wall if heading left
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_LEFT && droid.getX() - droid.getBitmap().getWidth()/2 <= 0){
			droid.getSpeed().toggleXDirection();
		}
		//check collision with bottom wall if heading down
		if(droid.getSpeed().getyDirection() == Speed.DIRECTION_DOWN && droid.getY() + droid.getBitmap().getHeight()/2 >= getHeight()){
			droid.getSpeed().toggleYDirection();
		}
		//check collision with top wall if heading up
		if(droid.getSpeed().getyDirection() == Speed.DIRECTION_UP && droid.getY() - droid.getBitmap().getHeight()/2 <= getHeight()){
			droid.getSpeed().toggleYDirection();
		}
		//update the lone droid
		droid.update();
	}

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
		displayFps(canvas, avgFps);
	}
	
	public void setAvgFps(String avgFps){
		this.avgFps = avgFps;
	}
	
	private void displayFps(Canvas canvas, String fps){
		if(canvas != null && fps != null){
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

}
