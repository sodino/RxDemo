[TOC]

参考链接：
[RxJava/RxAndroid 使用实例实践](https://www.jianshu.com/p/031745744bfa)



# onSubscribe? subscribeOn?
目前发现: 
    SingleObserver.onSubscribe( Disposable )
    
    Subscriber.onSubscribe( Subscription )    // 这个库太深了，不必理会

而subscribeOn有：
    Observeable.subscribeOn( Scheduler )
    Flowable.subscribeOn( Scheduler )
    Single.subscribeOn( Scheduler )
    Maybe.subscribeOn( Scheduler )
    Completable.subscribeOn( Scheduler )

# Schdulers.trampoline() ??
[Schedulers.trampoline调度器说明](https://blog.csdn.net/piglite/article/details/61299981)
Schedulers.trampoline调度器会将任务按添加顺序依次放入当前线程中等待执行
    （当前线程就是Schedulers.trampoline调度器创建Worker对象后，Worker对象调用scedule方法所在的线程）。
    线程一次只执行一个任务，其余任务排队等待，一个任务都执行完成后再开始下一个任务的执行。 

# Disposable?
用于中断数据发射，解除观察者
    void dispose()  // 执行解除 
    boolean isDisposed() // 已被解除
    
Disposable会从 subscribe() 中返回，或在**Observer（如SingleObserver/MaybeObserver/CompletableObserver）的onSubscribe( Disposable )中返回

![dispose.removeCallbacks](http://wx1.sinaimg.cn/large/e3dc9cealy1fpu3r4vyr0j21kw0xwdsf.jpg)

# Scheduler?

[RxJava之调度器(Schedulers)](https://blog.csdn.net/io_field/article/details/51429519)
[RxJava 第三篇 - Scheduler调度器使用及示例](https://www.jianshu.com/p/b037dbae9d8f)

    调度器(Schedulers)
        io
        Schedulers.io()调度器主要用于I/O操作，它基于根据需要，增长或缩减来自适应的线程池。大量的I/O调度操作将创建许多个线程并占用内存。一如既往的是，我们需要在性能和简捷两者之间找到一个有效的平衡点。
        
        computation
        Schedulers.computation()调度是精于计算工作的，它也是许多RxJava方法的默认调度器   buffer(),debounce(),delay(),interval(),sample(),skip()。
        
        immediate
        Schedulers.immediate()调度器主要用于立即在当前线程执行你指定的工作。它是timeout(),timeInterval(),以及timestamp()方法默认的调度器。
        
        newThread
        Schedulers.newThread()调度器主要用于为指定任务启动一个新的线程。
        
        trampoline
        Schedulers.trampoline()主要用于延迟工作任务的执行。当我们想在当前线程执行一个任务时，并不是立即，我们可以用.trampoline()将它入队。
        trampoline将会处理它的队列并且按序运行队列中每一个任务。它是repeat()和retry()方法默认的调度器。

设定调度线程时， subscribeOn () 的影响是随订阅流程由下往上的，
而observeOn是由随事件消费由上往下的，并且可以多次调用多次切换线程
[用一张图解释RxJava中的线程控制](http://zhangtielei.com/posts/blog-rxandroid-schedulers.html)
[浅谈 RxJava 中的线程管理](https://juejin.im/entry/5901903444d90400690a8b6f)

# Observer
    void onSubscribe( Disposable )
    void onNext( T )
    void onComplete()
    void onError( Throwable ) 

# Observeable & Flowable & Single & Maybe & Completable的区别？

Observable<T>	
    能够发射0或n个数据，并以成功或错误事件终止。

Flowable<T>	
    能够发射0或n个数据，并以成功或错误事件终止。 支持Backpressure，可以控制数据源发射的速度。

Single<T>	
    只发射单个数据或错误事件。

Completable	
    它从来不发射数据，只处理 onComplete 和 onError 事件。可以看成是Rx的Runnable。
    注意：因为Completable不发射数据，所以没有数据类型 <T>

Maybe<T>	
    能够发射0或者1个数据，要么成功，要么失败。有点类似于Optional


# Subject的分类？
[RxJava 的 Subject](https://www.jianshu.com/p/99bd603881bf())
该文章详细说明了Subject的各种区别与使用示例。


各种Subject在onCompleted()之后则不再接收数据。
Subject不支持Backpressed,可换用Processor来处理。

AsyncSubject: 
    Observer会接收AsyncSubject.onCompleted()之前的最后一个数据。
    即AsyncSubject.onCompleted()必须调用才会执行数据的发射
    
BehaviorSubject : 
    Observer会接收BehaviorSubject被订阅之前的最后一个数据，及被订阅之后的所有数据。
    如果被订阅之前没有发送任何数据，则会自动发送个默认的数据。
        
ReplySubject
    默认：Observer会接收Observable的全部数据，无论该Observable是何时被订阅的。
    可以使用createWithSize()控制缓存的数据数量
    可以使用createWithTime()控制缓存的数据时间
    
PublishSubject
    Observable只接收PublishSubject被订阅之后的所有数据。
    

# 不调用subscribOn 或 observerOn，则各种map & callback 会运行在当前线程吗？

没有subscribeOn()及observeOn，则在哪个线程调用就在哪个线程沧海消耗执行

# Subject考虑到多线程还需要toSerialized()??
[When you use a Subject as a Subscriber, take care not to call its onNext( ) method (or its other on methods) from multiple threads, as this could lead to non-serialized calls, which violates the Observable contract and creates an ambiguity in the resulting Subject.](https://github.com/ReactiveX/RxJava/wiki/Subject)


[Is SerializedSubject necessary for thread-safety in RxJava](https://stackoverflow.com/questions/31841809/is-serializedsubject-necessary-for-thread-safety-in-rxjava)



# Hot Observable & Cold Observable

[Cold Observable 和 Hot Observable](https://www.jianshu.com/p/12fb42bcf9fd)
Hot Observable 无论有没有 Subscriber 订阅，事件始终都会发生。当 Hot Observable 有多个订阅者时，Hot Observable 与订阅者们的关系是一对多的关系，可以与多个订阅者共享信息。

然而，Cold Observable 只有 Subscriber 订阅时，才开始执行发射数据流的代码。并且 Cold Observable 和 Subscriber 只能是一对一的关系，当有多个不同的订阅者时，消息是重新完整发送的。也就是说对 Cold Observable 而言，有多个Subscriber的时候，他们各自的事件是独立的。


# Error Handling : 错误/异常处理
onErrorReturn(throwable -> ClassOfReturn): 
 拦截捕获发生的异常，并返回一个正常的数据回调给观察者的onNext()。然后onComplete()
 
onErrorResumeNext( ObservableSource )
 当发生错误的时候，在该方法内捕获异常，并返回一个新的ObservableSource继续发射数据。观察者的onNext()继续收到回调。然后onComplete()
 第一处onErrorResumeNext()抛的异常，会在下一处的onErrorResumeNext()再一次处理，否则下文的onErrorResumeNext()不会得到执行的机会

onErrorReturnItem( ClassOfReturn )
 当发生错误的时候，不处理异常，直接返回一个正常的数据发射给观察者的onNext()。然后onComplete()

doOnError( )
 当之前的代码发生异常时，会被回调。
 如果之前的异常被onErrorResumeNext() onErrorResturnItem() onErrorReturn() catch并处理过了，则不会被回调。


# doOnNext() 是不是在onNext()之前回调？



技术 流量 产品 业务
