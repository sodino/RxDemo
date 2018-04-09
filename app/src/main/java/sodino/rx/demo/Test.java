package sodino.rx.demo;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sodino on 2018/3/29.
 */

public class Test {
    public static void testSubscribe3Params() {
        Observable.just("error test")
        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> apply(Throwable throwable) throws Exception {
                // onErrorResumeNext
                return null;
            }
        })
        .onErrorReturn(new Function<Throwable, String>() {
            @Override
            public String apply(Throwable throwable) throws Exception {
                // onErrorReturn
                return null;
            }
        })
//        .onExceptionResumeNext()
        .doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                // doOnError
            }
        })
        .onErrorReturnItem("onErrorRetrunItem")
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                // "onNext"
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                // onError
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                // onComplete
            }
        });
    }
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

    public static void testOnErrorResumeNext() {
        Observable.just("testOnErrorResumeNext")
                .onErrorResumeNext(Observable.just("observable onErrorResumeNext"))
                .subscribe();

        Observable.just("testOnErrorResumeNext")
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends String>>() {
                    @Override
                    public ObservableSource<? extends String> apply(Throwable throwable) throws Exception {
                        return Observable.just("observable from onErrorResumeNext: " + throwable.getMessage());
                    }
                })
                .subscribe();
    }
}
