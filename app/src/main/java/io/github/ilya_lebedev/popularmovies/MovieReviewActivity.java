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
package io.github.ilya_lebedev.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import io.github.ilya_lebedev.popularmovies.data.MoviesContract;

/**
 * MovieReviewActivity
 */
public class MovieReviewActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] MOVIE_REVIEW_PROJECTION = {
            MoviesContract.ReviewEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewEntry.COLUMN_CONTENT
    };

    public static final int INDEX_MOVIE_REVIEW_AUTHOR = 0;
    public static final int INDEX_MOVIE_REVIEW_CONTENT = 1;

    /* Movie review loader id */
    private static final int ID_MOVIE_REVIEWS_LOADER = 74;

    private TextView mAuthorTv;
    private TextView mContentTv;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review);

        mAuthorTv = findViewById(R.id.tv_movie_review_author);
        mContentTv = findViewById(R.id.tv_movie_review_content);

        mUri = getIntent().getData();
        if (mUri == null) {
            throw new NullPointerException("URI for MovieReviewActivity cannot be null");
        }

        getSupportLoaderManager().initLoader(ID_MOVIE_REVIEWS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {

            case ID_MOVIE_REVIEWS_LOADER: {
                return new CursorLoader(
                        this,
                        mUri,
                        MOVIE_REVIEW_PROJECTION,
                        null,
                        null,
                        null);
            }

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        int loaderId = loader.getId();

        switch (loaderId) {

            case ID_MOVIE_REVIEWS_LOADER: {

                boolean cursorHasValidData = false;
                if (cursor != null && cursor.moveToFirst()) {
                    cursorHasValidData = true;
                }

                if (!cursorHasValidData) {
                    return;
                }

                String author = cursor.getString(INDEX_MOVIE_REVIEW_AUTHOR);
                String content = cursor.getString(INDEX_MOVIE_REVIEW_CONTENT);

                mAuthorTv.setText(author);
                mContentTv.setText(content);

                break;
            }

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
