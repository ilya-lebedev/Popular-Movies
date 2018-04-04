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
import io.github.ilya_lebedev.popularmovies.utilities.NetworkUtils;
import io.github.ilya_lebedev.popularmovies.utilities.TmdbJsonUtils;

/**
 * MovieDetailFetchTask
 */
public class MovieDetailFetchTask {

    /* Actions constants */
    public static final String ACTION_FETCH_MOVIE_VIDEO = "fetch_movie_video";
    public static final String ACTION_FETCH_MOVIE_REVIEW = "fetch_movie_review";

    public static void executeTask(Context context, String action, int movieTmdbId) {

        if (ACTION_FETCH_MOVIE_VIDEO.equals(action)) {
            fetchMovieVideo(context, movieTmdbId);
        } else if (ACTION_FETCH_MOVIE_REVIEW.equals(action)) {
            fetchMovieReview(context, movieTmdbId);
        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }

    }

    private static void fetchMovieVideo(Context context, int movieTmdbId) {
        URL url = NetworkUtils.getMovieVideoListUrl(movieTmdbId);
        Uri uri = MoviesContract.VideoEntry.buildMovieVideosUriWithMovieTmdbId(movieTmdbId);

        ContentValues[] videoContentValues = null;

        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            videoContentValues = TmdbJsonUtils.getVideoContentValueFromJsonString(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (videoContentValues == null) {
            return;
        }

        context.getContentResolver().bulkInsert(uri, videoContentValues);
    }

    private static void fetchMovieReview(Context context, int movieTmdbId) {
        URL url = NetworkUtils.getMovieReviewListUrl(movieTmdbId);
        Uri uri = MoviesContract.ReviewEntry.buildMovieReviewsUriWithMovieTmdbId(movieTmdbId);

        ContentValues[] reviewContentValues = null;

        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            reviewContentValues = TmdbJsonUtils.getReviewContentValueFromJsonString(response);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (reviewContentValues == null) {
            return;
        }

        context.getContentResolver().bulkInsert(uri, reviewContentValues);
    }

}
