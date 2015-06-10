package com.zfj123.mobilesafe.receiver;

import com.zfj123.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SafeWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		System.out.println("onReceive");
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onUpdate");
		Intent intent=new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		System.out.println("onDisabled");
		Intent intent=new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
	
	@Override
	public void onEnabled(Context context) {
		System.out.println("onEnabled");
		Intent intent=new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
}
