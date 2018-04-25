package com.dev.nino.erowidreader;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.dev.nino.erowidreader.scrapers.SubstanceListScraper;

/**
 *
 */
public class ErowidReaderApp extends Application {

    public ErowidReaderApp() {
        Log.i("App", "App started");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Check if a day has passed to refresh the substance list
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.databasePrefs), Context.MODE_PRIVATE);
        if (System.currentTimeMillis() - sharedPref.getLong(getString(R.string.lastSubstanceRefreshTimeKey), -24*60*60*2000) > 24*60*60*1000) {

            // Refresh the substance list in the database
            SubstanceListScraper s = new SubstanceListScraper(this);
            s.execute();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(getString(R.string.lastSubstanceRefreshTimeKey), System.currentTimeMillis());
            editor.apply();
        }
    }
}
