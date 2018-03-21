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
package io.github.ilya_lebedev.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import io.github.ilya_lebedev.popularmovies.data.MoviesContract;
import io.github.ilya_lebedev.popularmovies.data.MoviesPreferences;
import io.github.ilya_lebedev.popularmovies.utilities.DateUtils;
import io.github.ilya_lebedev.popularmovies.utilities.NetworkUtils;
import io.github.ilya_lebedev.popularmovies.utilities.PageUtils;
import io.github.ilya_lebedev.popularmovies.utilities.TmdbJsonUtils;

/**
 * Utilities class for fetching data from TMDb server.
 */
public class MovieFetchTask {

    /* Actions constants */
    public static final String ACTION_FETCH_NEXT_PAGE = "fetch-next-page";

    private static final String TAG = MovieFetchTask.class.getSimpleName();

    /**
     * Select task to execute.
     *
     * @param context Used by tasks methods
     * @param action  Define which task to execute
     */
    public static void executeTask(Context context, String action) {
        if (ACTION_FETCH_NEXT_PAGE.equals(action)) {
            fetchNextPage(context);
        }
    }

    /**
     * Performs the network request for fetch movie data, parse the JSON
     * from that request and inserts the new movie information into ContentProvider.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    private static void fetchNextPage(Context context) {

        /* Get pages info */
        int currentPage = PageUtils.getCurrentPage(context);
        int totalPages = PageUtils.getTotalPages(context);

        /* Get uri of data for current show mode */
        Uri uri;
        int showMode = MoviesPreferences.getMoviesShowMode(context);
        if (showMode == MoviesPreferences.SHOW_MODE_MOST_POPULAR) {
            uri = MoviesContract.MovieEntry.CONTENT_URI_MOST_POPULAR;
        } else {
            uri = MoviesContract.MovieEntry.CONTENT_URI_TOP_RATED;
        }

        /* If last update time expired, set current page to 0 and clean cache table */
        boolean isDataActual = DateUtils.isMoviesListLastUpdateActual(context);
        if (!isDataActual) {
            context.getContentResolver().delete(uri, null, null);
            currentPage = 0;
            PageUtils.setCurrentPage(context, currentPage);
        }

        /* If all pages already loaded, we don't need to do anything */
        if (currentPage == totalPages) {
            return;
        }

        URL moviesListUrl = NetworkUtils.getMoviesListUrl(context, currentPage + 1);
        ContentValues[] movieContentValues = null;
        int requestedTotalPages = 1;

        try {
            String response = NetworkUtils.getResponseFromHttpUrl(moviesListUrl);
            movieContentValues = TmdbJsonUtils.getMovieContentValuesFromJson(response);
            requestedTotalPages = TmdbJsonUtils.getTotalPagesFromJson(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (movieContentValues == null) {
            return;
        }

        int insertedRows = context.getContentResolver()
                .bulkInsert(uri, movieContentValues);

        if (insertedRows > 0) {
            currentPage++;
            PageUtils.setCurrentPage(context, currentPage);

            if (currentPage == 1) {
                PageUtils.setTotalPages(context, requestedTotalPages);
                long currentTime = System.currentTimeMillis();
                DateUtils.setMoviesListLastUpdateTime(context, currentTime);
            }
        }

    }

}
