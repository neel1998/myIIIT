package com.trivedi.neel.myiiit.utils;

public interface Callback3<T1, T2, T3> {
    public void success(T1 t1, T2 t2, T3 t3);
    public void error(Exception e);
}