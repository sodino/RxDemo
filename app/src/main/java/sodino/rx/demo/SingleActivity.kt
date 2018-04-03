package sodino.rx.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class SingleActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTest.setOnClickListener(this)
    }

    private fun testBaseSingle() {
        var disposable : Disposable? = null

        Single.just("baseSingle")
                .map {
                    "$it maping...".log()
                    disposable?.let {
                        it.dispose()
                        "dispose in map..".log()
                    }
                    it.length
                }.subscribeOn(AndroidSchedulers.mainThread())
                // -->subscribe(final BiConsumer<? super T, ? super Throwable> onCallback)
//                .subscribe { integer, throwable ->
//                    BiConsumer
//                    if (throwable != null) {
//                        throwable.log("error")
//                    } else {
//                        integer.toString().log()
//                    }
//                }
                // --> subscribe(final Consumer<? super T> onSuccess, final Consumer<? super Throwable> onError)
//                .subscribe( { integer ->
//                    // Consumer
//                    integer.toString().log()
//                }, {throwable ->
//                    // Consumer
//                    throwable.log("baseSingle")
//                })

                // --> SingleObserver
                .subscribe(object : SingleObserver<Int>{
                    override fun onSuccess(t: Int) {
                        "${t.toString()} onSuccess".log()
                    }

                    override fun onSubscribe(d: Disposable) {
                        "onSubscribe".log()  // SingleSubscribeOn$SubscribeOnObserver
//                        d.dispose()
                        disposable = d
                        "onSubscribe do dispose".log()
                    }

                    override fun onError(e: Throwable) {
                        e.log("baseSingle")
                    }
                })



    }

    private fun testSingleConcat() {
        var idx = 0

        // concat:仍然在调用线程执行
        Single.concat(Single.just(Task.boolean_2s(idx ++)),
                Single.just(Task.boolean_2s(idx ++))).subscribeOn(Schedulers.io())
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnTest -> {
//                testSingleConcat()
                testBaseSingle()
            }
        }
    }
}
