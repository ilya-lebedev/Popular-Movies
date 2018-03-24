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

    private static final int ID_DETAIL_MOVIE_LOADER = 17;

    private TextView mTitleTv;
    private ImageView mPosterIv;
    private TextView mReleaseDateTv;
    private TextView mRatingTv;
    private TextView mPopularityTv;
    private TextView mFavoriteFab;
    private TextView mOverviewTv;

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

        mFavoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        getSupportLoaderManager().initLoader(ID_DETAIL_MOVIE_LOADER, null, this);
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

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        String movieTitle = data.getString(INDEX_MOVIE_TITLE);
        String moviePosterPath = data.getString(INDEX_MOVIE_POSTER_PATH);
        String moviePosterUrl = NetworkUtils.getMoviePosterUrl(this, moviePosterPath);
        long movieReleaseDateMillis = data.getLong(INDEX_MOVIE_RELEASE_DATE);
        String movieReleaseDate = TmdbDateUtils
                .getFriendlyReleaseDateString(this, movieReleaseDateMillis);
        double movieVoteAverage = data.getDouble(INDEX_MOVIE_VOTE_AVERAGE);
        String movieRating = getString(R.string.format_rating, movieVoteAverage);
        double moviePopularity = data.getDouble(INDEX_MOVIE_POPULARITY);
        String moviePopularityString = getString(R.string.format_popularity, moviePopularity);
        String movieOverview = data.getString(INDEX_MOVIE_OVERVIEW);

        mTitleTv.setText(movieTitle);
        Picasso.with(this).load(moviePosterUrl).into(mPosterIv);
        mReleaseDateTv.setText(movieReleaseDate);
        mRatingTv.setText(movieRating);
        mPopularityTv.setText(moviePopularityString);
        mOverviewTv.setText(movieOverview);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
