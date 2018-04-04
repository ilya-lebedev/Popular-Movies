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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link VideoAdapter} exposes a list of videos
 * from a {@link android.database.Cursor} to a {@link android.support.v7.widget.RecyclerView}
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    /* The context is used to app resources and layout inflaters */
    private final Context mContext;

    /* Video list data source */
    private Cursor mCursor;

    final private VideoAdapterOnClickHandler mClickHandler;

    /**
     * Creates VideoAdapter.
     *
     * @param context      Used for the app resources and the UI
     * @param clickHandler Used for handle clicks on recycler view items
     */
    public VideoAdapter(Context context, VideoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_video, parent, false);
        view.setFocusable(true);

        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String videoName = mCursor.getString(MovieDetailActivity.INDEX_MOVIE_VIDEO_NAME);
        holder.titleView.setText(videoName);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * Swap the cursor used by VideoAdapter for its movies data.
     *
     * @param cursor the new cursor to use as VideoAdapter's data source
     */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public interface VideoAdapterOnClickHandler {
        void onClick(String videoKey);
    }

    class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView titleView;

        public VideoAdapterViewHolder(View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.tv_video_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String videoKey = mCursor.getString(MovieDetailActivity.INDEX_MOVIE_VIDEO_KEY);
            mClickHandler.onClick(videoKey);
        }

    }

}
