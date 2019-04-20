package com.example.simplereader.util;

import java.util.List;

public interface StateCallBack<T> {

    void onSuccess(T t);

    void onSucceed();

    void onFailed(String s);

}
