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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 */
public class MoviesContract {

    /* Name for the entire content provider. */
    public static final String CONTENT_AUTHORITY = "io.github.ilya_lebedev.popularmovies";

    /* Base of all URI's. */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* Paths to form valid URI's. */
    public static final String PATH_MOVIE = "movie";

    /* Paths to form valid URI's. */
    public static final String PATH_TOP_RATED_MOVIE = "top_rated_movie";
    public static final String PATH_MOST_POPULAR_MOVIE = "most_popular_movie";

    /* This inner class defines the table content of the movie table */
    public static final class MovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the movie table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();
				
        public static final Uri CONTENT_URI_TOP_RATED = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED_MOVIE)
                .build();
        public static final Uri CONTENT_URI_MOST_POPULAR = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOST_POPULAR_MOVIE)
                .build();

        /* Name of movie table for cache */
        public static final String TABLE_NAME = "movie";
		
		/* Name of movie table for top rated movies */
		public static final String TABLE_NAME_TOP_RATED = "top_rated_movie";
		
		/* Name of movie table for most popular movies */
		public static final String TABLE_NAME_MOST_POPULAR = "most_popular_movie";

        /* Movie ID as returned by TMDb API, used to identify concrete movie */
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /* Title is stored as String representing movie title */
        public static final String COLUMN_TITLE = "title";

        /* Vote average is stored as a float representing movie user rating */
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        /* Popularity is stored as a float representing movie popularity */
        public static final String COLUMN_POPULARITY = "popularity";

        /* Poster path is stored as String representing relative path to a move poster image */
        public static final String COLUMN_POSTER_PATH = "poster_path";

        /* Overview is stored as String representing movie overview */
        public static final String COLUMN_OVERVIEW = "overview";

        /* Release date is stored as long representing movie release date (in milliseconds) */
        public static final String COLUMN_RELEASE_DATE = "release_date";
		
		/* Time of last update (in milliseconds) */
		public static final String COLUMN_LAST_UPDATE_TIME = "last_update_time";

    }

}
