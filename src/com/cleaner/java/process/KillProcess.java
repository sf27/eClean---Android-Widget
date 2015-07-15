package com.cleaner.java.process;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.cleaner.java.preferences.AppPreferences;
import com.cleaner.java.utils.UtilsGenerics;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class KillProcess {
	private Context context_;
	private int pid_ = 0;
	private String packageName_;

	public KillProcess(Context context, int pid, String packageName) {
		this.context_ = context;
		this.pid_ = pid;
		this.packageName_ = packageName;
	}

	public boolean execute() {
		List<Boolean> verify = new ArrayList<Boolean>();
		verify.add(this.context_ == null);
		verify.add(this.pid_ == 0);
		verify.add(this.context_ == null);

		if (UtilsGenerics.any(verify)) {
			return false;
		}

		ActivityManager manager = (ActivityManager) this.context_
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (this.pid_ <= 0) {
			return false;
		}
		if (this.pid_ == android.os.Process.myPid()) {
			Log.i(AppPreferences.LOGTAG, "Killing own process");
			android.os.Process.killProcess(this.pid_);
			return true;
		}
		Method method = null;
		try {
			method = manager.getClass().getMethod("killBackgroundProcesses",
					new Class[] { String.class });
		} catch (NoSuchMethodException e) {
			try {
				method = manager.getClass().getMethod("restartPackage",
						new Class[] { String.class });
			} catch (NoSuchMethodException ee) {
				ee.printStackTrace();
			}
		}
		if (method != null) {
			try {
				method.invoke(manager, this.packageName_);
				Log.i(AppPreferences.LOGTAG, "kill method  " + method.getName()
						+ " invoked " + this.packageName_);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		android.os.Process.killProcess(this.pid_);
		return true;
	}
}
