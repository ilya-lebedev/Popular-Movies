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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class is for bulkInsert movies data, query movies data and delete movies data.
 * This class serves as the ContentProvider for all of app's data.
 */
public class MoviesProvider extends ContentProvider {

    private static final String TAG = MoviesProvider.class.getSimpleName();

    /*
     * These constants are used to match URIs with the data they are looking for.
     */

    public static final int CODE_TOP_RATED_MOVIE = 100;
    public static final int CODE_TOP_RATED_MOVIE_WITH_TMDB_ID = 101;
    public static final int CODE_MOST_POPULAR_MOVIE = 200;
    public static final int CODE_MOST_POPULAR_MOVIE_WITH_TMDB_ID = 201;
    public static final int CODE_FAVORITE_MOVIE = 300;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    /**
     * Creates the UriMatcher that match each URI to the CODEs constants defined above.
     *
     * @return A UriMatcher
     */
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED_MOVIE, CODE_TOP_RATED_MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED_MOVIE + "/#",
                CODE_TOP_RATED_MOVIE_WITH_TMDB_ID);
        matcher.addURI(authority, MoviesContract.PATH_MOST_POPULAR_MOVIE, CODE_MOST_POPULAR_MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOST_POPULAR_MOVIE + "/#",
                CODE_MOST_POPULAR_MOVIE_WITH_TMDB_ID);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITE_MOVIE, CODE_FAVORITE_MOVIE);

        return matcher;
    }

    /**
     * Here initialized content provider on startup.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    /**
     * Handles requests to insert a set of new rows.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        String tableName;

        switch (sUriMatcher.match(uri)) {

            case CODE_TOP_RATED_MOVIE:
                tableName = MoviesContract.MovieEntry.TABLE_NAME_TOP_RATED;
                break;

            case  CODE_MOST_POPULAR_MOVIE:
                tableName = MoviesContract.MovieEntry.TABLE_NAME_MOST_POPULAR;
                break;

            case CODE_FAVORITE_MOVIE:
                tableName = MoviesContract.MovieEntry.TABLE_NAME_FAVORITE;
                break;

            default:
                return super.bulkInsert(uri, values);
        }


        db.beginTransaction();
        int rowInserted = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    rowInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowInserted;
    }

    /**
     * Handles query requests.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor.
     *                      If null, all columns are included.
     * @param selection     A selection criteria.
     *                      If null, all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *                      from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     Sort order of the row in the cursor
     *
     * @return A cursor containing the results of the query
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_TOP_RATED_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME_TOP_RATED,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_TOP_RATED_MOVIE_WITH_TMDB_ID: {
                String id = uri.getLastPathSegment();
                selection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[] {id};
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME_TOP_RATED,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_MOST_POPULAR_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME_MOST_POPULAR,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_MOST_POPULAR_MOVIE_WITH_TMDB_ID: {
                String id = uri.getLastPathSegment();
                selection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[] {id};
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME_MOST_POPULAR,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_FAVORITE_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME_FAVORITE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at a given URI with optional arguments.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting
     * @param selectionArgs Used in conjunction with the selection statement
     *
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;

        /*
         * If selection is null, entire table will be deleted. However, in this case we won't know
         * how many rows were deleted. According to the documentation for SQLiteDatabase, passing
         * "1" for the selection will delete all rows and return the number of rows deleted.
         */
        if (selection == null) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_TOP_RATED_MOVIE:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.MovieEntry.TABLE_NAME_TOP_RATED,
                        selection,
                        selectionArgs);
                break;

            case CODE_MOST_POPULAR_MOVIE:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.MovieEntry.TABLE_NAME_MOST_POPULAR,
                        selection,
                        selectionArgs);
                break;

            case CODE_FAVORITE_MOVIE:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.MovieEntry.TABLE_NAME_FAVORITE,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
