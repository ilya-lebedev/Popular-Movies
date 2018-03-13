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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movies data.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    /**
     * Creates MoviesDbHelper.
     *
     * @param context Used for locating paths to the database
     */
    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +

                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +

                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +

                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +

                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +

                MoviesContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +

                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL" +

                " UNIQUE (" + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    /**
     * This method defines upgrade policy of database.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Here nothing to do right now
    }

}
