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
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for handling date conversions
 */
public class TmdbDateUtils {

    /* Pattern of date in TMDb API */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Convert movie release date from TMDb API format to milliseconds
     *
     * @param releaseDateString String representing movie release date
     *
     * @return The number of milliseconds for movie release date
     *
     * @throws ParseException If date string cannot be converted
     */
    public static long convertReleaseDateStringToMillis(String releaseDateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        Date releaseDate = dateFormat.parse(releaseDateString);

        return releaseDate.getTime();
    }

    /**
     * Helper method to convert date in milliseconds to user-friendly string
     *
     * @param context Used by DateUtils to format the date in the current locale
     * @param releaseDateInMillis The date in milliseconds
     *
     * @return A user-friendly representation of the date
     */
    public static String getFriendlyReleaseDateString(Context context, long releaseDateInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_NUMERIC_DATE;

        return DateUtils.formatDateTime(context, releaseDateInMillis, flags);
    }

}
