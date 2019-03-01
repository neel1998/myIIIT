package com.trivedi.neel.myiiit.utils;

public interface AsyncTaskCallback<T> {
    void call(AsyncTaskResult<T> result);
}
