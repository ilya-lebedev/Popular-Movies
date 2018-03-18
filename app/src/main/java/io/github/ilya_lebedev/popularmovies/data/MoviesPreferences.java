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
package io.github.ilya_lebedev.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.github.ilya_lebedev.popularmovies.R;

/**
 * Class for manipulating app preferences.
 */
public class MoviesPreferences {

    /* Movies show mode variants */
    public static final int SHOW_MODE_MOST_POPULAR = 1;
    public static final int SHOW_MODE_TOP_RATED = 2;

    /* This is utility class and we don't need to instantiate it */
    private MoviesPreferences() {}

    /**
     * Return show mode type.
     *
     * @param context Used to access SharedPreferences
     *
     * @return Show mode type
     */
    public static int getMoviesShowMode(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForShowMode = context.getString(R.string.pref_show_mode_key);
        String mostPopularShowMode = context.getString(R.string.pref_show_mode_most_popular);
        String topRatedShowMode = context.getString(R.string.pref_show_mode_top_rated);

        String showMode = sp.getString(keyForShowMode, mostPopularShowMode);

        if (showMode.equals(topRatedShowMode)) {
            return SHOW_MODE_TOP_RATED;
        } else {
            return SHOW_MODE_MOST_POPULAR;
        }
    }

    /**
     * Save movies show mode.
     *
     * @param context Used to access SharedPreferences
     * @param showMode Show mode type
     */
    public static void setMoviesShowMode(Context context, int showMode) {

        int showModeStringId;
        switch (showMode) {
            case SHOW_MODE_MOST_POPULAR:
                showModeStringId = R.string.pref_show_mode_label_most_popular;
                break;
            case SHOW_MODE_TOP_RATED:
                showModeStringId = R.string.pref_show_mode_label_top_rated;
                break;
            default:
                throw new IllegalArgumentException("Unknown show mode: " + showMode);
        }

        String keyForShowMode = context.getString(R.string.pref_show_mode_key);
        String showModeValue = context.getString(showModeStringId);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(keyForShowMode, showModeValue);

        editor.apply();
    }

    /**
     * Return total pages number of most popular movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @return total pages number
     */
    public static int getMostPopularTotalPages(Context context) {

        String key = context.getString(R.string.pref_most_popular_total_pages_key);
        int defaultValue = context.getResources()
                .getInteger(R.integer.pref_most_popular_total_pages_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(key, defaultValue);
    }

    /**
     * Save total pages number of most popular movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @param totalPages total pages number
     */
    public static void setMostPopularTotalPages(Context context, int totalPages) {

        String key = context.getString(R.string.pref_most_popular_total_pages_key);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(key, totalPages);

        editor.apply();
    }

    /**
     * Return current page number of most popular movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @return current page number
     */
    public static int getMostPopularCurrentPage(Context context) {

        String key = context.getString(R.string.pref_most_popular_current_page_key);
        int defaultValue = context.getResources()
                .getInteger(R.integer.pref_most_popular_current_page_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(key, defaultValue);
    }

    /**
     * Save current page number of most popular movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @param currentPage current page number
     */
    public static void setMostPopularCurrentPage(Context context, int currentPage) {

        String key = context.getString(R.string.pref_most_popular_current_page_key);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(key, currentPage);

        editor.apply();
    }

    /**
     * Return last update time of most popular movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @return time in millis
     */
    public static long getMostPopularLastUpdateTime(Context context) {

        String key = context.getString(R.string.pref_most_popular_last_update_time_key);
        int defaultValue = context.getResources()
                .getInteger(R.integer.pref_most_popular_last_update_time_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getLong(key, defaultValue);
    }

    /**
     * Save last update time of most popular movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @param lastUpdateTime time in millis
     */
    public static void setMostPopularLastUpdateTime(Context context, long lastUpdateTime) {

        String key = context.getString(R.string.pref_most_popular_last_update_time_key);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(key, lastUpdateTime);

        editor.apply();
    }

    /**
     * Return total pages number of top rated movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @return total pages number
     */
    public static int getTopRatedTotalPages(Context context) {

        String key = context.getString(R.string.pref_top_rated_total_pages_key);
        int defaultValue = context.getResources()
                .getInteger(R.integer.pref_top_rated_total_pages_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(key, defaultValue);
    }

    /**
     * Save total pages number of top rated movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @param totalPages total pages number
     */
    public static void setTopRatedTotalPages(Context context, int totalPages) {

        String key = context.getString(R.string.pref_top_rated_total_pages_key);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(key, totalPages);

        editor.apply();
    }

    /**
     * Return current page number of top rated movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @return current page number
     */
    public static int getTopRatedCurrentPage(Context context) {

        String key = context.getString(R.string.pref_top_rated_current_page_key);
        int defaultValue = context.getResources()
                .getInteger(R.integer.pref_top_rated_current_page_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(key, defaultValue);
    }

    /**
     * Save current page number of top rated movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @param currentPage current page number
     */
    public static void setTopRatedCurrentPage(Context context, int currentPage) {

        String key = context.getString(R.string.pref_top_rated_current_page_key);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(key, currentPage);

        editor.apply();
    }

    /**
     * Return last update time of top rated movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @return time in millis
     */
    public static long getTopRatedLastUpdateTime(Context context) {

        String key = context.getString(R.string.pref_top_rated_last_update_time_key);
        int defaultValue = context.getResources()
                .getInteger(R.integer.pref_top_rated_last_update_time_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getLong(key, defaultValue);
    }


    /**
     * Save last update time of top rated movies.
     *
     * @param context Used to access SharedPreferences and app resources
     * @param lastUpdateTime time in millis
     */
    public static void setTopRatedLastUpdateTime(Context context, long lastUpdateTime) {

        String key = context.getString(R.string.pref_top_rated_last_update_time_key);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(key, lastUpdateTime);

        editor.apply();
    }

}
