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

import java.util.concurrent.TimeUnit;

import io.github.ilya_lebedev.popularmovies.data.MoviesPreferences;

/**
 * DateUtils
 */
public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    private static final int MOST_POPULAR_UPDATE_PERIOD_HOURS = 12;

    private static final long MOST_POPULAR_UPDATE_PERIOD_MILLISECONDS =
            TimeUnit.HOURS.toMillis(MOST_POPULAR_UPDATE_PERIOD_HOURS);

    private static final int TOP_RATED_UPDATE_PERIOD_HOURS = 24;

    private static final long TOP_RATED_UPDATE_PERIOD_MILLISECONDS =
            TimeUnit.HOURS.toMillis(TOP_RATED_UPDATE_PERIOD_HOURS);

    public static boolean isMoviesListLastUpdateActual(Context context) {
        int showMode = MoviesPreferences.getMoviesShowMode(context);

        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                return isMostPopularListActual(context);
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                return isTopRatedListActual(context);
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }
    }

    public static void setMoviesListLastUpdateTime(Context context, long lastUpdateTime) {
        int showMode = MoviesPreferences.getMoviesShowMode(context);

        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                MoviesPreferences.setMostPopularLastUpdateTime(context, lastUpdateTime);
                break;
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                MoviesPreferences.setTopRatedLastUpdateTime(context, lastUpdateTime);
                break;
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }
    }

    private static boolean isMostPopularListActual(Context context) {

        long lastUpdateTime = MoviesPreferences.getMostPopularLastUpdateTime(context);

        long updatePeriod = System.currentTimeMillis() - lastUpdateTime;

        return updatePeriod < MOST_POPULAR_UPDATE_PERIOD_MILLISECONDS;
    }

    private static boolean isTopRatedListActual(Context context) {

        long lastUpdateTime = MoviesPreferences.getTopRatedLastUpdateTime(context);

        long updatePeriod = System.currentTimeMillis() - lastUpdateTime;

        return updatePeriod < TOP_RATED_UPDATE_PERIOD_MILLISECONDS;
    }

}
