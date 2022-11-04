package com.example.lab8;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MyWidgetConfig MyWidgetConfigureActivity}
 */
public class MyWidget extends AppWidgetProvider {

    private static final String BUTTON_ANSWER1 = "button1";
    private static final String BUTTON_ANSWER2 = "button2";
    private static final String BUTTON_ANSWER3 = "button3";

    static protected PendingIntent getPendingSelfIntent(Context context, String action, int answer, int widgetID) {
        Intent intent = new Intent(context, MyWidget.class);
        intent.setAction(action);
        intent.putExtra("USER_ANSWER", answer);
        intent.putExtra("WIDGET_ID", widgetID);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        CharSequence problem = MyWidgetConfig.getProblem(context, appWidgetId);
        if (problem == null) return;
        int answer = MyWidgetConfig.getAnswer(context, appWidgetId);
        int fake_answer1;
        int fake_answer2;
        do {
            fake_answer1 =  Game.randomInt(answer - 10, answer + 10);
        } while(fake_answer1 == answer);
        do {
            fake_answer2 =  Game.randomInt(answer - 10, answer + 10);
        } while(fake_answer2 == answer || fake_answer2 == fake_answer1);
        // Shuffle array to randomly place answers in buttons
        Integer[] answers = { answer, fake_answer1, fake_answer2 };
        List<Integer> integerList = Arrays.asList(answers);
        Collections.shuffle(integerList);
        answers[0] = integerList.get(0);
        answers[1] = integerList.get(1);
        answers[2] = integerList.get(2);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        views.setTextViewText(R.id.widget_problem, problem);
        views.setTextViewText(R.id.widget_answer1, String.valueOf(answers[0]));
        views.setTextViewText(R.id.widget_answer2, String.valueOf(answers[1]));
        views.setTextViewText(R.id.widget_answer3, String.valueOf(answers[2]));

        views.setOnClickPendingIntent(R.id.widget_answer1, getPendingSelfIntent(context, BUTTON_ANSWER1, answers[0], appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_answer2, getPendingSelfIntent(context, BUTTON_ANSWER2, answers[1], appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_answer3, getPendingSelfIntent(context, BUTTON_ANSWER3, answers[2], appWidgetId));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MyWidgetConfig.deleteWidgetPreferences(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Bundle extras = intent.getExtras();
        int widgetID, user_answer;
        if (extras != null) {
            widgetID = extras.getInt("WIDGET_ID", -1);
            if (widgetID == -1) return;
            user_answer = extras.getInt("USER_ANSWER", 0);
        } else return;

        int answer = MyWidgetConfig.getAnswer(context, widgetID);
        if (answer == user_answer) {
            Toast.makeText(context, "True", Toast.LENGTH_SHORT).show();
            Game game = new Game();
            MyWidgetConfig.saveWidgetPreferences(context, widgetID, game.getProblem(), game.getAnswer());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            updateAppWidget(context, appWidgetManager, widgetID);
        }
        else Toast.makeText(context, "False", Toast.LENGTH_SHORT).show();
    }
}