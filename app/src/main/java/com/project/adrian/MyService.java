package com.project.adrian;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.project.adrian.DataBase.DBAdapter;
import com.project.adrian.DataModel.userdatamodel;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class MyService extends Service {
    long millisec = 0;
    String filename;
    String currentTime;
    public Context context = this;
    DBAdapter obj;
    ArrayList<userdatamodel> dh;
    Date date1, date2, date3, date4;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        rec();
        return super.onStartCommand(intent, flags, startId);
    }

    public void rec() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rec();
                getDateAndTime();

                long date = System.currentTimeMillis();
                SimpleDateFormat time1 = new SimpleDateFormat("kk:mm");
                String curenttime = time1.format(date);
                obj = DBAdapter.getDBAdapter(getApplicationContext());
                if (obj.checkDatabase() == false)
                    obj.createDatabase(getApplicationContext());
                obj.openDatabase();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.adrian_widget);
                ComponentName thisWidget = new ComponentName(context, AdrianWidget.class);
                Calendar calendar = Calendar.getInstance();
                String[] days = new String[]{"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sábado"};
                int da = calendar.get(Calendar.DAY_OF_WEEK);
                String startTime = "00:00";
                if (da > 1 && da <= 6) {
                    dh = new ArrayList<userdatamodel>();
                    LocalTime target;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        target = LocalTime.parse(curenttime);
                        if (target.isBefore(LocalTime.parse("16:00")) && target.isAfter(LocalTime.parse("15:00"))) {
                            startTime = "15:00";
                        }
                        if (target.isBefore(LocalTime.parse("17:00")) && target.isAfter(LocalTime.parse("16:00"))) {
                            startTime = "16:00";
                        }
                        if (target.isBefore(LocalTime.parse("18:00")) && target.isAfter(LocalTime.parse("17:00"))) {
                            startTime = "17:00";
                        }
                        if (target.isBefore(LocalTime.parse("19:20")) && target.isAfter(LocalTime.parse("18:20"))) {
                            startTime = "18:20";
                        }
                        if (target.isBefore(LocalTime.parse("20:20")) && target.isAfter(LocalTime.parse("19:20"))) {
                            startTime = "19:20";
                        }
                        if (target.isBefore(LocalTime.parse("21:20")) && target.isAfter(LocalTime.parse("20:20"))) {
                            startTime = "20:20";
                        }
                        System.out.println(days[da-1]);
                        System.out.println(target);
                        System.out.println(startTime);
                        dh = obj.getData(startTime, days[da-1]);
                        for (int i = 0; i < dh.size(); i++) {
                            remoteViews.setTextViewText(R.id.appwidget_text, dh.get(i).getProfesor());
                            remoteViews.setTextViewText(R.id.appwidget_course, dh.get(i).getSub());
                            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        }
                        if (target.isBefore(LocalTime.parse("18:19")) && target.isAfter(LocalTime.parse("18:01"))) {
                            remoteViews.setTextViewText(R.id.appwidget_text, "Descanso");
                            remoteViews.setTextViewText(R.id.appwidget_course, "Descanso");
                            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        }
                        if (target.isBefore(LocalTime.parse("14:59")) && target.isAfter(LocalTime.parse("00:00"))) {
                            remoteViews.setTextViewText(R.id.appwidget_text, "Casa");
                            remoteViews.setTextViewText(R.id.appwidget_course, "Casa");
                            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        }
                        if (target.isBefore(LocalTime.parse("23:59")) && target.isAfter(LocalTime.parse("21:21"))) {
                            remoteViews.setTextViewText(R.id.appwidget_text, "Casa");
                            remoteViews.setTextViewText(R.id.appwidget_course, "Casa");
                            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        }
                    }
                    dh = obj.getData(startTime, days[da - 1]);
                    for (int i = 0; i < dh.size(); i++) {
                        remoteViews.setTextViewText(R.id.appwidget_text, dh.get(i).getProfesor());
                        remoteViews.setTextViewText(R.id.appwidget_course, dh.get(i).getSub());
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                    }
                } else {
                    remoteViews.setTextViewText(R.id.appwidget_text, "Festivo");
                    remoteViews.setTextViewText(R.id.appwidget_course, "Festivo");
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }
            }
        }, millisec);
    }

    public void getDateAndTime() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date myDate = new Date();
        filename = timeStampFormat.format(myDate);
        SimpleDateFormat timeget = new SimpleDateFormat("hh:mm");
        currentTime = timeget.format(myDate);
    }

    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
        Toast.makeText(getApplicationContext(), "Restart Service", Toast.LENGTH_LONG).show();
    }
}
