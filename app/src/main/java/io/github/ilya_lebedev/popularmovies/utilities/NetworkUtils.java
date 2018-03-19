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
package io.github.ilya_lebedev.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import io.github.ilya_lebedev.popularmovies.BuildConfig;
import io.github.ilya_lebedev.popularmovies.data.MoviesPreferences;

/**
 * These utilities uses to communicate with TMDb server.
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String SCANNER_DELIMITER = "\\A";

    /* Base URL of TMDb API. */
    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";

    /* The movie path of the TMDb service */
    private static final String MOVIE_PATH = "movie";

    /* Sort order path by most popular */
    private static final String MOST_POPULAR_PATH = "popular";

    /* Sort order path by top rated */
    private static final String TOP_RATED_PATH = "top_rated";

    /* Page parameter */
    private static final String PAGE_PARAM = "page";

    /* The API key parameter allow to access TMDb service */
    private static final String API_KEY_PARAM = "api_key";

    /* The API key value */
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    /* This is utility class and we don't need to instantiate it */
    private NetworkUtils() {}

    /**
     * Retrieves the proper URL to query the movies data.
     *
     * @param context used to access other utility methods
     * @return URL to query TMDb service
     */
    public static URL getMoviesListUrl(Context context, int page) {
        int showMode = MoviesPreferences.getMoviesShowMode(context);
        String showModePath;
        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                showModePath = MOST_POPULAR_PATH;
                break;
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                showModePath = TOP_RATED_PATH;
                break;
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }
        return buildMoviesListUrl(showModePath, page);
    }

    /**
     * Builds the URL used to talk to the TMDb server using sort order of a movies list.
     *
     * @param sortOrderPath The path saying to TMDb service in which sort order case
     *                     we want to get movies list
     * @return The Url to use to query TMDb service
     */
    private static URL buildMoviesListUrl(String sortOrderPath, int page) {

        Uri moviesListQueryUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(sortOrderPath)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        try {
            URL moviesListQueryUrl = new URL(moviesListQueryUri.toString());
            Log.v(TAG, "URL: " + moviesListQueryUrl);
            return moviesListQueryUrl;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(SCANNER_DELIMITER);

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

}
