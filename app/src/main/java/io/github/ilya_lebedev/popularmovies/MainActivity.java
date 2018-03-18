/*
 * Copyright (C) 2018 Ilya Lebedev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ilya_lebedev.popularmovies;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import io.github.ilya_lebedev.popularmovies.data.MoviesContract;
import io.github.ilya_lebedev.popularmovies.data.MoviesPreferences;

/**
 * Main activity of the app.
 */
public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * The columns which we need for displaying list of movies within MainActivity.
     */
    public static final String[] MAIN_MOVIE_PROJECTION = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH

    };

    /*
     * This indices representing the values in the array of String above.
     * Uses for more quickly access to the data from query.
     * WARN: If the order or the contents of the Strings above changes,
     * these indices must be adjust to match the changes.
     */
    public static final int INDEX_MOVIE_LOCAL_ID = 0;
    public static final int INDEX_MOVIE_THMBD_ID = 1;
    public static final int INDEX_MOVIE_TITLE = 2;
    public static final int INDEX_MOVIE_POSTER_PATH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        int showMode = MoviesPreferences.getMoviesShowMode(this);
        int menuItemId;

        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                menuItemId = R.id.action_top_rated;
                break;
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                menuItemId = R.id.action_most_popular;
                break;
            default:
                menuItemId = R.id.action_top_rated;
        }

        menu.findItem(menuItemId).setChecked(true);

        return true;
    }

    private void checkUncheckMenuItem(MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_top_rated:
                MoviesPreferences.setMoviesShowMode(this,
                        MoviesPreferences.SHOW_MODE_TOP_RATED);
                checkUncheckMenuItem(item);
                return true;
            case R.id.action_most_popular:
                MoviesPreferences.setMoviesShowMode(this,
                        MoviesPreferences.SHOW_MODE_MOST_POPULAR);
                checkUncheckMenuItem(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String showModeKey = getString(R.string.pref_show_mode_key);
        if (showModeKey.equals(key)) {
            // TODO
        }
    }

}
