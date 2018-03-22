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

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import io.github.ilya_lebedev.popularmovies.data.MoviesContract;
import io.github.ilya_lebedev.popularmovies.data.MoviesPreferences;
import io.github.ilya_lebedev.popularmovies.sync.MovieFetchUtils;

/**
 * Main activity of the app.
 */
public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        MoviesAdapter.MoviesAdapterOnClickHandler {

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

    private static final int ID_MOVIE_LOADER = 15;

    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private Parcelable mRecyclerViewState;

    private static final int SCROLL_DIRECTION_DOWN = 1;

    private MoviesAdapter mMoviesAdapter;

    private boolean mIsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);

        mMoviesAdapter = new MoviesAdapter(this, this);

        final GridLayoutManager layoutManager =
                new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mMoviesAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mRecyclerView.canScrollVertically(SCROLL_DIRECTION_DOWN)
                        && !mIsLoading) {
                    mIsLoading = true;
                    MovieFetchUtils.fetchNextPage(getApplicationContext());
                    mRecyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                }
            }
        });

        /* Setup the shared preference listener */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

        MovieFetchUtils.initialize(this);
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
            getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);
            MovieFetchUtils.reinitialize(this);
            mPosition = RecyclerView.NO_POSITION;
        }
    }

    /**
     * Called by the {@link android.support.v4.app.LoaderManagerImpl} when a new Loader created.
     *
     * @param loaderId The loader ID
     * @param bundle   Arguments
     *
     * @return A new Loader instance
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_MOVIE_LOADER: {
                Uri uri;
                String sortOrder;

                int showMode = MoviesPreferences.getMoviesShowMode(this);

                if (showMode == MoviesPreferences.SHOW_MODE_MOST_POPULAR) {
                    uri = MoviesContract.MovieEntry.CONTENT_URI_MOST_POPULAR;
                    sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                } else {
                    uri = MoviesContract.MovieEntry.CONTENT_URI_TOP_RATED;
                    sortOrder = MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                }

                return new CursorLoader(this,
                        uri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        sortOrder);
            }

            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) {
            return;
        }

        mMoviesAdapter.swapCursor(cursor);

        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
            mRecyclerView.scrollToPosition(mPosition);
        }

        if (mIsLoading && mRecyclerViewState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerViewState);
        }

        mIsLoading = false;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int movieTmdbId) {
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        Uri movieUri = MoviesContract.MovieEntry.buildMovieUriWithTmdbId(this, movieTmdbId);
        movieDetailIntent.setData(movieUri);
        startActivity(movieDetailIntent);
    }

}
