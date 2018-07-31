package sodino.rx.demo.schdulers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import sodino.rx.demo.R
import sodino.rx.demo.R.id.btnTest
import sodino.rx.demo.Task
import sodino.rx.demo.log
import java.util.concurrent.TimeUnit

class SchdulersActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTest.setOnClickListener(this)
    }

    private fun testDelayAndLoop() {
        var idx = 0
        // delay
        Schedulers.computation().createWorker().schedule ({ Task.boolean_2s(idx++) }, 1000, TimeUnit.MICROSECONDS)

        // loop
        var disposable : Disposable? = null

        disposable = Schedulers.io().createWorker().schedulePeriodically( {
            Task.boolean_2s(idx++)
            if (idx > 10) {
                disposable?.let { it.dispose()
                "do dispose".log()
                }
            }
        }, 500, 1000, TimeUnit.MICROSECONDS)
    }

    private fun testSchedulerBySubscribeOn() {
//        Observable.just("scheduler by subscribe on")
//                .map { "map result" }
//                .doOnSubscribe { "do on subscribe newThread".log() }
//                .subscribeOn(Schedulers.newThread())
//                .doOnSubscribe { "do on subscribe ioThread".log() }
//                .subscribeOn(Schedulers.io())
//                .subscribe { it.log() }

//        线程调度的影响上：
//        subscribeOn影响的是订阅流程，由下至上
//        observeOn影响的事件发射消费流程，由上到下
        Observable.fromCallable {
            val txt = "callable string item"
            txt.log()
            txt
        }.map {
            val result = "map result 1"
            result.log()
            result
        }
        .observeOn(AndroidSchedulers.mainThread())
        .map {
            val result = "map result 2"
            result.log()
            result
        }
        .observeOn(Schedulers.computation())
        .doOnSubscribe { "do on subscribe newThread".log() }
        .subscribeOn(Schedulers.newThread())
        .doOnSubscribe { "do on subscribe ioThread".log() }
        .subscribeOn(Schedulers.computation())
        .subscribe { it.log() }
    }


    private fun testScheculersChange() {
        Observable.just("scheduler call")
                .observeOn(Schedulers.newThread())
                .map { it.log()
                    it}
                .observeOn(Schedulers.io())
                .map { it.log()
                    it}
                .observeOn(Schedulers.computation())
                .map { it.log()
                    it}
                .observeOn(Schedulers.single())
                .map { it.log()
                    it}
                .observeOn(Schedulers.trampoline())
                .map { it.log()
                    it}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    it.log()
                }
    }


    private fun testScheculersCall() {
        Single.just("scheduler call")
                // 只有第一个subscribeOn生效
                .subscribeOn(Schedulers.newThread())
                .map { it.log()
                it}
                .subscribeOn(Schedulers.io())
                .map { it.log()
                it}
                .subscribeOn(Schedulers.computation())
                .map { it.log()
                it}
                .subscribeOn(Schedulers.single())
                .map { it.log()
                it}
                .subscribeOn(Schedulers.trampoline())
                .map { it.log()
                it}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    it.log()
                }

    }

    private fun testSchedulers() {
//        Schedulers.newThread()
//        Schedulers.computation()
//        Schedulers.io()
//        Schedulers.single()
//        Schedulers.trampoline()

        var idx = 0
        // Schedulers.newThread() 每次新创建线程
        Schedulers.newThread().createWorker().schedule {
            Task.boolean_2s(idx++)
        }
        Schedulers.newThread().createWorker().schedule {
            Task.boolean_2s(idx++)
        }

        // 自行管理线程池
        Schedulers.computation().createWorker().schedule {
            Task.boolean_2s(idx++)
        }

        // 自行管理线程池
        Schedulers.io().createWorker().schedule {
            Task.boolean_2s(idx++)
        }

        // 在当前线程调用并执行，和Schedulers.single()类似的，在同一线程执行的话也是强顺序的
        Schedulers.trampoline().createWorker().schedule {
            val tmp = idx ++
            "trampoline $tmp start".log()
            Task.boolean_2s(tmp)
            "trampoline $tmp end".log()
        }
        Schedulers.trampoline().createWorker().schedule {
            val tmp = idx ++
            "trampoline $tmp start".log()
            Task.boolean_2s(tmp)
            "trampoline $tmp end".log()
        }
        Schedulers.trampoline().createWorker().schedule {
            val tmp = idx ++
            "trampoline $tmp start".log()
            Task.boolean_2s(tmp)
            "trampoline $tmp end".log()
        }



        // 返回单纯程支持的调度程序实例，用于需要在同一后台线程上进行按固定顺序执行的场景
        // 以下三个调用会按顺序在同一线程执行
        Schedulers.single().createWorker().schedule {
            Task.boolean_2s(idx++)
        }

        Schedulers.single().createWorker().schedule {
            Task.boolean_2s(idx++)
        }

        Schedulers.single().createWorker().schedule {
            Task.boolean_2s(idx++)
        }
    }

    fun testNoSchedulers() {
        Thread( {
            // 没有subscribeOn()及observeOn，则在哪个线程调用就在哪个线程沧海消耗执行
            Observable.just("no schedulers")
                    .map { "$it map result".log() }
                    .subscribe { "$it , subscribe result".log() }
        }, "testNoSchedulers").start()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnTest -> {
                testNoSchedulers()
//                testSchedulers()
//                testScheculersCall()
//                testScheculersChange()
//                testSchedulerBySubscribeOn()
//                testDelayAndLoop()
            }
        }
    }
}
