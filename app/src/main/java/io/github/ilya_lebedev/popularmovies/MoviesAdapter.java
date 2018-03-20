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

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.github.ilya_lebedev.popularmovies.utilities.NetworkUtils;

/**
 * {@link MoviesAdapter} exposes a list of movies
 * from a {@link android.database.Cursor} to a {@link android.support.v7.widget.RecyclerView}
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    /* The context is used to app resources and layout inflaters */
    private final Context mContext;

    /* Movies list data source */
    private Cursor mCursor;

    /**
     * Creates MoviesAdapter.
     *
     * @param context Used for the app resources and the UI
     */
    public MoviesAdapter(@NonNull Context context) {
        mContext = context;
    }

    /**
     * This method called when new ViewHolder is created.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType Used to provide different layout.
     *                 In our case we don't use it.
     * @return A new MoviesAdapter that holds the view for each movie list item
     */
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_movie, parent, false);
        view.setFocusable(true);
        return new MoviesAdapterViewHolder(view);
    }

    /**
     * This method is called by the RecyclerView to display the data at the specific position.
     *
     * @param moviesAdapterViewHolder View holder representing the content of the movie item
     * @param position The position of the movie item within the adapter data set
     */
    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String moviePosterPath = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER_PATH);
        String moviePosterUrl = NetworkUtils.getMoviePosterUrl(mContext, moviePosterPath);

        Picasso.with(mContext).load(moviePosterUrl).into(moviesAdapterViewHolder.posterView);
    }

    /**
     * Returns the number of movies to display.
     *
     * @return The number of movies available in movies list
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * Swap the cursor used by MoviesAdapter for its movies data.
     *
     * @param cursor the new cursor to use as MoviesAdapter's data source
     */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        final ImageView posterView;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);

            posterView = itemView.findViewById(R.id.im_movie_poster);
        }

    }

}
