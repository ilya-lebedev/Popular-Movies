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
import android.support.annotation.NonNull;

import io.github.ilya_lebedev.popularmovies.data.MoviesPreferences;
import io.github.ilya_lebedev.popularmovies.utilities.PageUtils;

/**
 * MovieFetchUtils
 */
public class MovieFetchUtils {

    private static boolean sInInitialize = false;

    synchronized public static void initialize(@NonNull Context context) {

        if (sInInitialize) return;

        sInInitialize = true;

        startInitialization(context);
    }

    public static void fetchNextPage(Context context) {
        startMovieFetchTask(context);
    }

    private static void startInitialization(Context context) {

        if (MoviesPreferences.isMoviesShowModeFavorite(context)) {
            return;
        }

        int currentPage = PageUtils.getCurrentPage(context);

        if (currentPage == 0) {
            startMovieFetchTask(context);
        }
    }

    public static void reinitialize(Context context) {
        startInitialization(context);
    }

    private static void startMovieFetchTask(Context context) {
        Intent intent = new Intent(context, MovieFetchIntentService.class);
        intent.setAction(MovieFetchTask.ACTION_FETCH_NEXT_PAGE);
        context.startService(intent);
    }

}
