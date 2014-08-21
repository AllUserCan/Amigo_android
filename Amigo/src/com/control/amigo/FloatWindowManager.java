package com.control.amigo;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.amigo.R;

public class FloatWindowManager {

	/**
	 * 小悬浮窗View的实例
	 */
	private static FloatWindowSmallView smallWindow;

	/**
	 * 大悬浮窗View的实例
	 */
	private static FloatWindowBigView bigWindow;

	/**
	 * 小悬浮窗View的参数
	 */
	private static LayoutParams smallWindowParams;

	/**
	 * 大悬浮窗View的参数
	 */
	private static LayoutParams bigWindowParams;

	/**
	 * 用于控制在屏幕上添加或移除悬浮窗
	 */
	private static WindowManager mWindowManager;

	/**
	 * 用于获取手机可用内存
	 */
	private static ActivityManager mActivityManager;

	/**
	 * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createSmallWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (smallWindow == null) {
			smallWindow = new FloatWindowSmallView(context);
			if (smallWindowParams == null) {
				smallWindowParams = new LayoutParams();
				smallWindowParams.type = LayoutParams.TYPE_PHONE;
				smallWindowParams.format = PixelFormat.RGBA_8888;
				smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				smallWindowParams.width = FloatWindowSmallView.viewWidth;
				smallWindowParams.height = FloatWindowSmallView.viewHeight;
				smallWindowParams.x = screenWidth;
				smallWindowParams.y = screenHeight / 2;
			}
			smallWindow.setParams(smallWindowParams);
			windowManager.addView(smallWindow, smallWindowParams);
		}
	}

	/**
	 * 将小悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(smallWindow);
			smallWindow = null;
		}
	}

	/**
	 * 创建一个大悬浮窗。位置为屏幕正中间。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createBigWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (bigWindow == null) {
			bigWindow = new FloatWindowBigView(context);
			if (bigWindowParams == null) {
				bigWindowParams = new LayoutParams();
				bigWindowParams.x = screenWidth / 2 - FloatWindowBigView.viewWidth / 2;
				bigWindowParams.y = screenHeight / 2 - FloatWindowBigView.viewHeight / 2;
				bigWindowParams.type = LayoutParams.TYPE_PHONE;
				bigWindowParams.format = PixelFormat.RGBA_8888;
				bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				bigWindowParams.width = FloatWindowBigView.viewWidth;
				bigWindowParams.height = FloatWindowBigView.viewHeight;
			}
			windowManager.addView(bigWindow, bigWindowParams);
		}
	}

	/**
	 * 将大悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeBigWindow(Context context) {
		if (bigWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(bigWindow);
			bigWindow = null;
		}
	}

	/**
	 * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 */
	public static void updateAmigoInfo() {
		if (bigWindow != null) {
//			TextView percentView = (TextView) smallWindow.findViewById(R.id.percent);
//			percentView.setText(getUsedPercentValue(context));
			TextView motorView = (TextView) bigWindow.findViewById(R.id.motorStatus);
			TextView batteryView = (TextView) bigWindow.findViewById(R.id.batteryStatus);
			TextView positionView = (TextView) bigWindow.findViewById(R.id.position);
			TextView sonar1View = (TextView) bigWindow.findViewById(R.id.sonar1);
			TextView sonar2View = (TextView) bigWindow.findViewById(R.id.sonar2);
			TextView sonar3View = (TextView) bigWindow.findViewById(R.id.sonar3);
			TextView sonar4View = (TextView) bigWindow.findViewById(R.id.sonar4);
			TextView sonar5View = (TextView) bigWindow.findViewById(R.id.sonar5);
			TextView sonar6View = (TextView) bigWindow.findViewById(R.id.sonar6);
			TextView sonar7View = (TextView) bigWindow.findViewById(R.id.sonar7);
			TextView sonar8View = (TextView) bigWindow.findViewById(R.id.sonar8);
			
			motorView.setText(BluetoothService.Amigo.getMotor());
			batteryView.setText(BluetoothService.Amigo.getBattery());
			positionView.setText(BluetoothService.Amigo.getPosition());
			sonar1View.setText(BluetoothService.Amigo.getSonar1());
			sonar2View.setText(BluetoothService.Amigo.getSonar2());
			sonar3View.setText(BluetoothService.Amigo.getSonar3());
			sonar4View.setText(BluetoothService.Amigo.getSonar4());
			sonar5View.setText(BluetoothService.Amigo.getSonar5());
			sonar6View.setText(BluetoothService.Amigo.getSonar6());
			sonar7View.setText(BluetoothService.Amigo.getSonar7());
			sonar8View.setText(BluetoothService.Amigo.getSonar8());
			
		}
	}

	/**
	 * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
	 * 
	 * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
	 */
	public static boolean isWindowShowing() {
		return smallWindow != null || bigWindow != null;
	}

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	/**
	 * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return ActivityManager的实例，用于获取手机可用内存。
	 */
	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	/**
	 * 计算已使用内存的百分比，并返回。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 已使用内存的百分比，以字符串形式返回。
	 */
//	public static String getUsedPercentValue(Context context) {
//		String dir = "/proc/meminfo";
//		try {
//			FileReader fr = new FileReader(dir);
//			BufferedReader br = new BufferedReader(fr, 2048);
//			String memoryLine = br.readLine();
//			String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
//			br.close();
//			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
//			long availableSize = getAvailableMemory(context) / 1024;
//			int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
//			return percent + "%";
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "悬浮窗";
//	}

	/**
	 * 获取当前可用内存，返回数据以字节为单位。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 当前可用内存。
	 */
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		getActivityManager(context).getMemoryInfo(mi);
		return mi.availMem;
	}

}
