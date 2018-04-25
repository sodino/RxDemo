package sodino.rx.demo.rxbus

import io.reactivex.Observable
import io.reactivex.internal.functions.Functions
import io.reactivex.subjects.PublishSubject

/**
 * Created by sodino on 2018/4/2.
 */

object RxBus {
    private val bus by lazy {
        // toSerialized(): for multiple threads safe
        // https://github.com/ReactiveX/RxJava/wiki/Subject
        PublishSubject.create<Any>().toSerialized()
    }

    fun post(o: Any) {
        bus.onNext(o)
    }

    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        var observable = bus.filter(Functions.isInstanceOf(eventType))
        return observable.cast(eventType)
        // ofType = filter + cast
//        return bus.ofType(eventType)
    }

}
