package sodino.rx.demo

import io.reactivex.Observable
import io.reactivex.internal.functions.Functions
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

/**
 * Created by sodino on 2018/4/3.
 */

//object PublishBus  @see RxBus

object AsyncBus {
    private val bus by lazy {
        AsyncSubject.create<Any>().toSerialized()
    }

    public fun post(o : Any) {
        bus.onNext(o)
    }

    public fun <T> toObservable(eventType : Class<T>) : Observable<T> {
        var observable = bus.filter(Functions.isInstanceOf(eventType))
        return observable.cast(eventType)
    }

    public fun onComplete() {
        bus.onComplete()
    }
}

object BehaviorBus {
    private val bus by lazy {
        BehaviorSubject.create<Any>().toSerialized()
    }

    public fun post(o: Any) {
        bus.onNext(o)
    }

    public fun <T> toObservable(eventType : Class<T>) : Observable<T> {
        var observable = bus.filter(Functions.isInstanceOf(eventType))
        return observable.cast(eventType)
    }
}

object ReplayBus {
    private val bus by lazy {
        //        ReplaySubject.create<Any>().toSerialized()
        ReplaySubject.create<Any>()
    }

    public fun post(o : Any) {
        bus.onNext(o)
    }

    public fun <T> toObservable(eventType: Class<T>) : Observable<T> {
        var observable = bus.filter(Functions.isInstanceOf(eventType))
        return observable.cast(eventType)

        // ofType = filter + cast
        // return bus.ofType(eventType)
    }
}


object ReplayStringBus {
    private val bus by lazy {
        //        ReplaySubject.create<Any>().toSerialized()
        ReplaySubject.create<String>()
    }

    public fun post(o : String) {
        bus.onNext(o)
    }

    public fun toObservable() : Observable<String> {
        var observable = bus.filter(Functions.isInstanceOf(String::class.java))
        return observable.cast(String::class.java)

        // ofType = filter + cast
        // return bus.ofType(eventType)
    }
}

