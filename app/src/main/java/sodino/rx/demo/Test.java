package sodino.rx.demo;

import android.util.Log;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sodino on 2018/3/29.
 */

public class Test {
    public static void testBaseSingle1() {
        Single.just("baseSingle")
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        Log.d("RxDemo", s);
                        return s.length();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    public static void testBaseSingle() {
        Single.just("baseSingle")
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        Log.d("RxDemo", s);
                        return s.length();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<Integer, Throwable>() {
                    @Override
                    public void accept(Integer integer, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Log.d("RxDemo", "error", throwable);
                        } else {
                            Log.d("RxDemo", "ok");
                        }
                    }
                });
    }
}
