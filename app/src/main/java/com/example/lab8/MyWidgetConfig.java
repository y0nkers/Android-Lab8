package com.example.lab8;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.lab8.databinding.MyWidgetConfigureBinding;

/**
 * The configuration screen for the {@link MyWidget MyWidget} AppWidget.
 */
public class MyWidgetConfig extends Activity {

    private static final String PREFERENCES_NAME = "MyWidgetPreferences";
    private static final String PREFERENCES_PREFIX_PROBLEM = "MyWidgetData_Problem_";
    private static final String PREFERENCES_PREFIX_ANSWER = "MyWidgetData_Answer_";
    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MyWidgetConfig.this;

            Game game = new Game();
            saveWidgetPreferences(context, widgetID, game.getProblem(), game.getAnswer());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MyWidget.updateAppWidget(context, appWidgetManager, widgetID);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    private MyWidgetConfigureBinding binding;

    public MyWidgetConfig() {
        super();
    }

    // Write data to the SharedPreferences object for this widget
    static void saveWidgetPreferences(Context context, int appWidgetId, String problem, int answer) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        prefs.putString(PREFERENCES_PREFIX_PROBLEM + appWidgetId, problem);
        prefs.putInt(PREFERENCES_PREFIX_ANSWER + appWidgetId, answer);
        prefs.apply();
    }

    static String getProblem(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, 0);
        return prefs.getString(PREFERENCES_PREFIX_PROBLEM + appWidgetId, null);
    }
    
    static int getAnswer(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, 0);
        return prefs.getInt(PREFERENCES_PREFIX_ANSWER + appWidgetId, 0);
    }

    static void deleteWidgetPreferences(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        prefs.remove(PREFERENCES_PREFIX_PROBLEM + appWidgetId);
        prefs.remove(PREFERENCES_PREFIX_ANSWER + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        binding = MyWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.addButton.setOnClickListener(onClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }
}