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
package io.github.ilya_lebedev.popularmovies.sync;

import android.content.Context;

/**
 * Utilities class for fetching data from TMDb server.
 */
public class MovieFetchTask {

    /* Actions constants */
    public static final String ACTION_FETCH_NEXT_PAGE = "fetch-next-page";

    /**
     * Select task to execute.
     *
     * @param context Used by tasks methods
     * @param action  Define which task to execute
     */
    public static void executeTask(Context context, String action) {
        if (ACTION_FETCH_NEXT_PAGE.equals(action)) {
            fetchNextPage(context);
        }
    }

    /**
     * Performs the network request for fetch movie data, parse the JSON
     * from that request and inserts the new movie information into ContentProvider.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    private static void fetchNextPage(Context context) {
        // TODO
    }

}
