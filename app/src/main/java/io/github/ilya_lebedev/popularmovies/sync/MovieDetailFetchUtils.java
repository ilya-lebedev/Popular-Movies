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

import android.content.Context;
import android.content.Intent;

/**
 * MovieDetailFetchUtils
 */
public class MovieDetailFetchUtils {

    public static final String EXTRA_MOVIE_TMDB_ID = "movie_tmdb_id";

    public static void startMovieVideoFetchTask(Context context, int movieTmdbId) {
        Intent intent = new Intent(context, MovieVideoFetchIntentService.class);
        intent.setAction(MovieDetailFetchTask.ACTION_FETCH_MOVIE_VIDEO);
        intent.putExtra(EXTRA_MOVIE_TMDB_ID, movieTmdbId);
        context.startService(intent);
    }

    public static void startMovieReviewFetchTask(Context context, int movieTmdbId) {
        Intent intent = new Intent(context, MovieReviewFetchIntentService.class);
        intent.setAction(MovieDetailFetchTask.ACTION_FETCH_MOVIE_REVIEW);
        intent.putExtra(EXTRA_MOVIE_TMDB_ID, movieTmdbId);
        context.startService(intent);
    }

}
