package com.example.neel.myiiit.utils;

public interface Callback2<T1, T2> {
    public void success(T1 t1, T2 t2);
    public void error(Exception e);
}