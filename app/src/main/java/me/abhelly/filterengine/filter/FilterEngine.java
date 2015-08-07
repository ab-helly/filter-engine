package me.abhelly.filterengine.filter;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.LinkedList;

import me.abhelly.filterengine.filter.parser.Parser;
import me.abhelly.filterengine.filter.parser.Tokenizer;
import me.abhelly.filterengine.filter.parser.token.Token;
import me.abhelly.filterengine.model.Item;

/**
 * Filtering engine.
 * Executes asynchronously one query at a time.
 *
 * Created by abhelly on 03.08.15.
 */
public class FilterEngine {

    private static FilterEngine sInstance;

    private FilterAsyncTask mAsyncTask;

    public static FilterEngine getInstance() {
        if (sInstance == null) {
            sInstance = new FilterEngine();
        }
        return sInstance;
    }

    /**
     * Filters given data according to given query.
     *
     * @param query    string query
     * @param data     data to filter
     * @param listener filter listener to pass results when ready
     * @param context  context
     */
    public final void filter(String query, ArrayList<Item> data, FilterListener listener,
            Context context) {
        if (mAsyncTask != null && mAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mAsyncTask.cancel(true);
        }
        mAsyncTask = new FilterAsyncTask(query, data, listener, context);
        mAsyncTask.execute();
    }

    public interface FilterListener {

        void onFilterComplete(FilterResult result);
    }

    /**
     * Performs filtering operation.
     */
    private static class FilterAsyncTask extends AsyncTask<String, Void, FilterResult> {

        private final Context mContext;

        private final String mQuery;

        private final ArrayList<Item> mInput;

        private final FilterListener mListener;

        FilterAsyncTask(String query, ArrayList<Item> input, FilterListener listener,
                Context context) {
            mQuery = query;
            mInput = input;
            mListener = listener;
            mContext = context;
        }

        @Override
        protected FilterResult doInBackground(String... params) {
            if (mQuery == null || mQuery.isEmpty()) {
                return new FilterResult(null, null);
            } else {
                LinkedList<Token> tokens = Tokenizer.tokenize(mQuery);
                return new Parser().evaluate(tokens, mInput, mContext);
            }
        }

        @Override
        protected void onPostExecute(FilterResult result) {
            super.onPostExecute(result);
            if (mListener != null) {
                mListener.onFilterComplete(result);
            }
        }
    }
}
