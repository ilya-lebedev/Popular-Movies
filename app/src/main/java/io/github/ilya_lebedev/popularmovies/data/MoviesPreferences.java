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

    /* Movies sort order variants */
    public static final int SORT_ORDER_MOST_POPULAR = 1;
    public static final int SORT_ORDER_TOP_RATED = 2;

    /* This is utility class and we don't need to instantiate it */
    private MoviesPreferences() {}

    /**
     * Return sort order type.
     *
     * @param context Used to access SharedPreferences
     * @return sort order type.
     */
    public static int getMoviesSortOrder(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForSortOrder = context.getString(R.string.pref_sort_order_key);
        String mostPopularSortOrder = context.getString(R.string.pref_sort_order_most_popular);
        String topRatedSortOrder = context.getString(R.string.pref_sort_order_top_rated);

        String sortOrder = sp.getString(keyForSortOrder, mostPopularSortOrder);

        if (sortOrder.equals(topRatedSortOrder)) {
            return SORT_ORDER_TOP_RATED;
        } else {
            return SORT_ORDER_MOST_POPULAR;
        }
    }

    /**
     * Save movies sort order.
     *
     * @param context Used to access SharedPreferences
     * @param sortOrder Sort order type
     */
    public static void setMoviesSortOrder(Context context, int sortOrder) {

        int sortOrderStringId;
        switch (sortOrder) {
            case SORT_ORDER_MOST_POPULAR:
                sortOrderStringId = R.string.pref_sort_order_label_most_popular;
                break;
            case SORT_ORDER_TOP_RATED:
                sortOrderStringId = R.string.pref_sort_order_label_top_rated;
                break;
            default:
                throw new IllegalArgumentException("Unknown sort order: " + sortOrder);
        }

        String keyForSortOrder = context.getString(R.string.pref_sort_order_key);
        String sortOrderValue = context.getString(sortOrderStringId);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(keyForSortOrder, sortOrderValue);

        editor.apply();
    }

}
