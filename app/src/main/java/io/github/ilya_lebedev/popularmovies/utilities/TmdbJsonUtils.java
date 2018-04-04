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

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import io.github.ilya_lebedev.popularmovies.data.MoviesContract;

/**
 * Utility functions to handle TMDb JSON data.
 */
public class TmdbJsonUtils {

    /* JSON key of movies JSON array */
    private static final String TMDB_RESULTS = "results";

    /* JSON keys of a movie JSON object */
    private static final String TMDB_ID = "id";
    private static final String TMDB_TITLE = "title";
    private static final String TMDB_VOTE_AVERAGE = "vote_average";
    private static final String TMDB_POPULARITY = "popularity";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_OVERVIEW = "overview";
    private static final String TMDB_RELEASE_DATE = "release_date";

    private static final String TMDB_TOTAL_PAGES = "total_pages";

    /* JSON keys of a movie video JSON object */
    private static final String TMDB_VIDEO_MOVIE_ID = "id";
    private static final String TMDB_VIDEO_RESULTS = "results";
    private static final String TMDB_VIDEO_ID = "id";
    private static final String TMDB_VIDEO_KEY = "key";
    private static final String TMDB_VIDEO_NAME = "name";
    private static final String TMDB_VIDEO_SITE = "site";
    private static final String TMDB_VIDEO_TYPE = "type";

    /* JSON keys of a movie review JSON object */
    private static final String TMDB_REVIEW_MOVIE_ID = "id";
    private static final String TMDB_REVIEW_RESULTS = "results";
    private static final String TMDB_REVIEW_ID = "id";
    private static final String TMDB_REVIEW_AUTHOR = "author";
    private static final String TMDB_REVIEW_CONTENT = "content";

    /**/
    private static final String TMDB_STATUS_MESSAGE = "status_message";
    private static final String TMDB_STATUS_CODE = "status_code";

    /**/
    private static final int TMDB_STATUS_CODE_INVALID_API_KEY = 7;
    private static final int TMDB_STATUS_CODE_NOT_FOUND = 34;

    /**
     * This method parses JSON and returns an array of ContentValues
     * representing the list of movies
     *
     * @param movieJsonString JSON response from TMDb server
     *
     * @return Array of ContentValues representing movies data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getMovieContentValuesFromJson(String movieJsonString)
            throws JSONException {

        JSONObject moviesJson = new JSONObject(movieJsonString);

        /* Is error happened */
        if (moviesJson.has(TMDB_STATUS_CODE)) {
            int statusCode = moviesJson.getInt(TMDB_STATUS_CODE);

            switch (statusCode) {
                case TMDB_STATUS_CODE_INVALID_API_KEY:
                    /* Invalid API key */
                    return null;
                case TMDB_STATUS_CODE_NOT_FOUND:
                    /* Movies not found */
                    return null;
            }
        }

        /* Get JSON array of movies */
        JSONArray movieResultsArray = moviesJson.getJSONArray(TMDB_RESULTS);

        ContentValues[] movieContentValues = new ContentValues[movieResultsArray.length()];

        for (int i = 0; i < movieResultsArray.length(); i++) {
            JSONObject movie = movieResultsArray.getJSONObject(i);

            /* Parse concrete movie */
            int id = movie.getInt(TMDB_ID);
            String title = movie.getString(TMDB_TITLE);
            double voteAverage = movie.getDouble(TMDB_VOTE_AVERAGE);
            double popularity = movie.getDouble(TMDB_POPULARITY);
            String posterPath = movie.getString(TMDB_POSTER_PATH);
            String overview = movie.getString(TMDB_OVERVIEW);
            String releaseDateString = movie.getString(TMDB_RELEASE_DATE);
            Long releaseDate = null;
            try {
                releaseDate = TmdbDateUtils.convertReleaseDateStringToMillis(releaseDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            /* Put parsed data to the ContentValues */
            ContentValues movieValues = new ContentValues();
            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

            movieContentValues[i] = movieValues;
        }

        return movieContentValues;
    }

    public static int getTotalPagesFromJson(String movieJsonString) throws JSONException {

        JSONObject moviesJson = new JSONObject(movieJsonString);

        return moviesJson.getInt(TMDB_TOTAL_PAGES);
    }

    public static ContentValues[] getVideoContentValueFromJsonString(String videoJsonString)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(videoJsonString);

        int movieId = jsonObject.getInt(TMDB_VIDEO_MOVIE_ID);

        /* Get JSON array of movie videos */
        JSONArray videoResultsArray = jsonObject.getJSONArray(TMDB_VIDEO_RESULTS);

        ContentValues[] videoContentValues = new ContentValues[videoResultsArray.length()];

        for (int i = 0; i < videoResultsArray.length(); i++) {
            JSONObject video = videoResultsArray.getJSONObject(i);

            String id = video.getString(TMDB_VIDEO_ID);
            String key = video.getString(TMDB_VIDEO_KEY);
            String name = video.getString(TMDB_VIDEO_NAME);
            String site = video.getString(TMDB_VIDEO_SITE);
            String type = video.getString(TMDB_VIDEO_TYPE);

            /* Put parsed data to the ContentValues */
            ContentValues videoValues = new ContentValues();
            videoValues.put(MoviesContract.VideoEntry.COLUMN_MOVIE_TMDB_ID, movieId);
            videoValues.put(MoviesContract.VideoEntry.COLUMN_TMDB_ID, id);
            videoValues.put(MoviesContract.VideoEntry.COLUMN_KEY, key);
            videoValues.put(MoviesContract.VideoEntry.COLUMN_NAME, name);
            videoValues.put(MoviesContract.VideoEntry.COLUMN_SITE, site);
            videoValues.put(MoviesContract.VideoEntry.COLUMN_TYPE, type);

            videoContentValues[i] = videoValues;
        }

        return videoContentValues;
    }

    public static ContentValues[] getReviewContentValueFromJsonString(String reviewJsonString)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(reviewJsonString);

        int movieId = jsonObject.getInt(TMDB_REVIEW_MOVIE_ID);

        /* Get JSON array of movie reviews */
        JSONArray reviewResultsArray = jsonObject.getJSONArray(TMDB_REVIEW_RESULTS);

        ContentValues[] reviewContentsValues = new ContentValues[reviewResultsArray.length()];

        for (int i = 0; i < reviewResultsArray.length(); i++) {
            JSONObject review = reviewResultsArray.getJSONObject(i);

            String id = review.getString(TMDB_REVIEW_ID);
            String author = review.getString(TMDB_REVIEW_AUTHOR);
            String content = review.getString(TMDB_REVIEW_CONTENT);

            /* Put parsed data to the ContentValues */
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_TMDB_ID, movieId);
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_TMDB_ID, id);
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, author);
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, content);

            reviewContentsValues[i] = reviewValues;
        }

        return reviewContentsValues;
    }

}
