package com.example.neel.myiiit.utils;

import android.os.AsyncTask;

public abstract class CallbackAsyncTask<T1, T2, T3> extends AsyncTask<T1, T2, AsyncTaskResult<T3>> {
    private AsyncTaskCallback<T3> mCallback;

    public CallbackAsyncTask(AsyncTaskCallback<T3> callback) {
        super();

        mCallback = callback;
    }

    public void onPostExecute(AsyncTaskResult<T3> result) {
        if (mCallback != null) {
            mCallback.call(result);
        }
    }
}
