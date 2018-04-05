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

    private static final int TIMEOUT_CONNECT = 5000;
    private static final int TIMEOUT_READ = 10000;

    private static final String SCANNER_DELIMITER = "\\A";

    /* Base URL of TMDb API. */
    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";

    /* The movie path of the TMDb service */
    private static final String MOVIE_PATH = "movie";

    /* Sort order path by most popular */
    private static final String MOST_POPULAR_PATH = "popular";

    /* Sort order path by top rated */
    private static final String TOP_RATED_PATH = "top_rated";

    /**/
    private static final String VIDEOS_PATH = "videos";

    /**/
    private static final String REVIEWS_PATH = "reviews";

    /* Page parameter */
    private static final String PAGE_PARAM = "page";

    /* The API key parameter allow to access TMDb service */
    private static final String API_KEY_PARAM = "api_key";

    /* The API key value */
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    /* Base URL of movie poster */
    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String MOVIE_POSTER_SIZE_W92 = "w92";

    private static final String MOVIE_POSTER_SIZE_W154 = "w154";

    private static final String MOVIE_POSTER_SIZE_W185 = "w185";

    private static final String MOVIE_POSTER_SIZE_W342 = "w342";

    private static final String MOVIE_POSTER_SIZE_W500 = "w500";

    private static final String MOVIE_POSTER_SIZE_W780 = "w780";

    private static final String MOVIE_POSTER_SIZE_ORIGINAL = "original";

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

    public static String getMoviePosterUrl(Context context, String posterPath) {

        Uri moviePosterUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath(MOVIE_POSTER_SIZE_W185)
                .appendEncodedPath(posterPath)
                .build();

        return moviePosterUri.toString();
    }

    public static URL getMovieVideoListUrl(int movieTmdbId) {

        Uri movieVideosListUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(String.valueOf(movieTmdbId))
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        try {
            URL moviesListQueryUrl = new URL(movieVideosListUri.toString());
            Log.v(TAG, "URL: " + moviesListQueryUrl);
            return moviesListQueryUrl;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static URL getMovieReviewListUrl(int movieTmdbId) {

        Uri movieReviewsListUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(String.valueOf(movieTmdbId))
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        try {
            URL moviesListQueryUrl = new URL(movieReviewsListUri.toString());
            Log.v(TAG, "URL: " + moviesListQueryUrl);
            return moviesListQueryUrl;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        }

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

        /* Open connection for a given uri */
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        /* Set connection and read timeouts */
        urlConnection.setConnectTimeout(TIMEOUT_CONNECT);
        urlConnection.setReadTimeout(TIMEOUT_READ);

        try {
            /* Get a stream to read data from it */
            InputStream inputStream = urlConnection.getInputStream();

            /* Put stream inside scanner */
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(SCANNER_DELIMITER);

            /* Read data */
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
