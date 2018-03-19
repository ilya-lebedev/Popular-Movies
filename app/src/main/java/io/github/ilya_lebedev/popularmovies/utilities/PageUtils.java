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

import io.github.ilya_lebedev.popularmovies.data.MoviesPreferences;

/**
 * PageUtils
 */
public class PageUtils {

    public static int getCurrentPage(Context context) {
        int showMode = MoviesPreferences.getMoviesShowMode(context);

        int currentPage;

        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                currentPage = MoviesPreferences.getMostPopularCurrentPage(context);
                break;
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                currentPage = MoviesPreferences.getTopRatedCurrentPage(context);
                break;
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }

        return currentPage;
    }

    public static void setCurrentPage(Context context, int currentPage) {
        int showMode = MoviesPreferences.getMoviesShowMode(context);

        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                MoviesPreferences.setMostPopularCurrentPage(context, currentPage);
                break;
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                MoviesPreferences.setTopRatedCurrentPage(context, currentPage);
                break;
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }
    }

    public static int getTotalPages(Context context) {
        int showMode = MoviesPreferences.getMoviesShowMode(context);

        int totalPages;

        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                totalPages = MoviesPreferences.getMostPopularTotalPages(context);
                break;
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                totalPages = MoviesPreferences.getTopRatedTotalPages(context);
                break;
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }

        return totalPages;
    }

    public static void setTotalPages(Context context, int totalPages) {
        int showMode = MoviesPreferences.getMoviesShowMode(context);

        switch (showMode) {
            case MoviesPreferences.SHOW_MODE_MOST_POPULAR:
                MoviesPreferences.setMostPopularTotalPages(context, totalPages);
                break;
            case MoviesPreferences.SHOW_MODE_TOP_RATED:
                MoviesPreferences.setTopRatedTotalPages(context, totalPages);
                break;
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }
    }

    public static boolean isCurrentPageLast(Context context) {

        int currentPage = getCurrentPage(context);
        int totalPages = getTotalPages(context);

        return currentPage == totalPages;
    }

}
