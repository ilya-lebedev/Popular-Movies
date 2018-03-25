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

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.ilya_lebedev.popularmovies.data.MoviesContract;
import io.github.ilya_lebedev.popularmovies.utilities.NetworkUtils;
import io.github.ilya_lebedev.popularmovies.utilities.TmdbDateUtils;

/**
 * MovieDetailActivity
 */
public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    public static final String[] DETAIL_MOVIE_PROJECTION = {
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW
    };

    /*
     * Indexes.
     */
    public static final int INDEX_MOVIE_TMDB_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_RELEASE_DATE = 3;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_POPULARITY = 5;
    public static final int INDEX_MOVIE_OVERVIEW = 6;

    /* Loaders ids */
    private static final int ID_DETAIL_MOVIE_LOADER = 17;
    private static final int ID_IS_MOVIE_FAVORITE_LOADER = 27;

    /* Tokens for AsyncQueryHandler */
    private static final int TOKEN_MOVIE_INSERT = 1;
    private static final int TOKEN_MOVIE_DELETE = 2;

    int mMovieTmdbId;
    String mMovieTitle;
    String mMoviePosterPath;
    long mMovieReleaseDateMillis;
    double mMovieVoteAverage;
    double mMoviePopularity;
    String mMovieOverview;

    private TextView mTitleTv;
    private ImageView mPosterIv;
    private TextView mReleaseDateTv;
    private TextView mRatingTv;
    private TextView mPopularityTv;
    private FloatingActionButton mFavoriteFab;
    private TextView mOverviewTv;

    private boolean mIsMovieFavorite;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitleTv = findViewById(R.id.tv_movie_title);
        mPosterIv = findViewById(R.id.iv_movie_poster);
        mReleaseDateTv = findViewById(R.id.tv_movie_release_date);
        mRatingTv = findViewById(R.id.tv_movie_rating);
        mPopularityTv = findViewById(R.id.tv_movie_popularity);
        mFavoriteFab = findViewById(R.id.fab_movie_in_favorite);
        mOverviewTv = findViewById(R.id.tv_movie_overview);

        mUri = getIntent().getData();
        if (mUri == null) {
            throw new NullPointerException("URI for MovieDetailActivity cannot be null");
        }

        mFavoriteFab.setEnabled(false);
        mFavoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMovieFavorite) {
                    deleteMovieFromFavorite();
                } else {
                    saveMovieToFavorite();
                }
            }
        });

        getSupportLoaderManager().initLoader(ID_DETAIL_MOVIE_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_IS_MOVIE_FAVORITE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {

            case ID_DETAIL_MOVIE_LOADER: {
                return new CursorLoader(
                        this,
                        mUri,
                        DETAIL_MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            }

            case ID_IS_MOVIE_FAVORITE_LOADER: {
                String id = mUri.getLastPathSegment();
                Uri favoriteUri = Uri.withAppendedPath(
                        MoviesContract.MovieEntry.CONTENT_URI_FAVORITE, id);

                return new CursorLoader(
                        this,
                        favoriteUri,
                        null,
                        null,
                        null,
                        null
                );
            }

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int loaderId = loader.getId();

        switch (loaderId) {

            case ID_DETAIL_MOVIE_LOADER: {

                boolean cursorHasValidData = false;
                if (data != null && data.moveToFirst()) {
                    cursorHasValidData = true;
                }

                if (!cursorHasValidData) {
                    return;
                }

                mMovieTmdbId = data.getInt(INDEX_MOVIE_TMDB_ID);
                mMovieTitle = data.getString(INDEX_MOVIE_TITLE);
                mMoviePosterPath = data.getString(INDEX_MOVIE_POSTER_PATH);
                String moviePosterUrl = NetworkUtils.getMoviePosterUrl(this, mMoviePosterPath);
                mMovieReleaseDateMillis = data.getLong(INDEX_MOVIE_RELEASE_DATE);
                String movieReleaseDate = TmdbDateUtils
                        .getFriendlyReleaseDateString(this, mMovieReleaseDateMillis);
                mMovieVoteAverage = data.getDouble(INDEX_MOVIE_VOTE_AVERAGE);
                String movieRating = getString(R.string.format_rating, mMovieVoteAverage);
                mMoviePopularity = data.getDouble(INDEX_MOVIE_POPULARITY);
                String moviePopularityString = getString(R.string.format_popularity, mMoviePopularity);
                mMovieOverview = data.getString(INDEX_MOVIE_OVERVIEW);

                mTitleTv.setText(mMovieTitle);
                Picasso.with(this).load(moviePosterUrl).into(mPosterIv);
                mReleaseDateTv.setText(movieReleaseDate);
                mRatingTv.setText(movieRating);
                mPopularityTv.setText(moviePopularityString);
                mOverviewTv.setText(mMovieOverview);

                break;
            }

            case ID_IS_MOVIE_FAVORITE_LOADER: {
                mFavoriteFab.setEnabled(true);
                if (data != null && data.moveToFirst()) {
                    mFavoriteFab.setImageResource(android.R.drawable.btn_star_big_on);
                    mIsMovieFavorite = true;
                } else {
                    mFavoriteFab.setImageResource(android.R.drawable.btn_star_big_off);
                    mIsMovieFavorite = false;
                }

                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void saveMovieToFavorite() {
        MovieAsyncHandler movieAsyncHandler = new MovieAsyncHandler(getContentResolver());

        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovieTmdbId);
        values.put(MoviesContract.MovieEntry.COLUMN_TITLE, mMovieTitle);
        values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, mMoviePosterPath);
        values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mMovieReleaseDateMillis);
        values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovieVoteAverage);
        values.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, mMoviePopularity);
        values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovieOverview);
        values.put(MoviesContract.MovieEntry.COLUMN_LAST_UPDATE_TIME, System.currentTimeMillis()); // TODO

        Uri uri = ContentUris.withAppendedId(
                MoviesContract.MovieEntry.CONTENT_URI_FAVORITE, mMovieTmdbId);

        movieAsyncHandler.startInsert(TOKEN_MOVIE_INSERT, null, uri, values);
    }

    private void deleteMovieFromFavorite() {
        MovieAsyncHandler movieAsyncHandler = new MovieAsyncHandler(getContentResolver());

        Uri uri = ContentUris.withAppendedId(
                MoviesContract.MovieEntry.CONTENT_URI_FAVORITE, mMovieTmdbId);

        String selection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = new String[] { Integer.toString(mMovieTmdbId) };

        movieAsyncHandler.startDelete(
                TOKEN_MOVIE_DELETE,
                null,
                uri,
                selection,
                selectionArgs);
    }

    private static class MovieAsyncHandler extends AsyncQueryHandler {

        public MovieAsyncHandler(ContentResolver cr) {
            super(cr);
        }

    }

}
