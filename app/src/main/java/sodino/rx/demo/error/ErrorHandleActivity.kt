package sodino.rx.demo.error

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Observable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_rx_errors.*
import sodino.rx.demo.R
import sodino.rx.demo.log

class ErrorHandleActivity : AppCompatActivity(), View.OnClickListener {
    var idx = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_errors)

        btnError.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnError -> {
                testOnError()
//                testOnErrorReturn()
//                testOnErrorResumeNext()
//                testOnErrorReturnItem()
            }
        }
    }

    private fun testOnError() {
        Observable.just("")
                .map { val i = 0
                    if (i == 0) {
                        throw Exception("testOnErrorReturnItem")
                    }
                    "test map"
                }
                .subscribe({
                    // onNext
                    "subscribe onNext: $it".log()
                }, {
                    // onError
                    "subscribe onError ${it::class.java.simpleName} ${it.message}".log()
                }, {
                    // onComplete
                    "subscribe onComplete".log()
                })
    }

    private fun testOnErrorReturnItem() {
        Observable.just("")
                .map { val i = 0
                    if (i == 0) {
                        throw Exception("testOnErrorReturnItem")
                    }
                    "test map"
                }
                // doOnError 1 会被执行
                .doOnError { throwable -> "doOnError 1 ${throwable::class.java.simpleName} ${throwable.message}".log() }
                .onErrorReturnItem("onErrorReturnItem")
                // doOnError 2 不会被执行
                .doOnError { throwable -> "doOnError 2 ${throwable::class.java.simpleName} ${throwable.message}".log() }
                .subscribe({
                    // onNext
                    "subscribe onNext: $it".log()
                }, {
                    // onError
                    "subscribe onError".log()
                }, {
                    // onComplete
                    "subscribe onComplete".log()
                })

    }

    private fun testOnErrorResumeNext() {
        Observable.just("testOnErrorResumeNextNext")
                .map {
                    val i = 0
                    if (i == 0) {
                        throw Exception("testOnErrorResumeNext")
                    }
                    "test map"
                }
                .onErrorResumeNext (Function { throwable ->
                        val i = 0;
                        if (i == 0) {
                            val tmp = "Function onErrorResumeNext exception"
                            tmp.log()
                            // 第一处onErrorResumeNext()抛的异常，会在下一处的onErrorResumeNext()再一次处理，否则下文的onErrorResumeNext()不会得到执行的机会
                            throw Exception(tmp)
                        }
                        Observable.just("observable from onErrorResumeNext (function) : ${throwable.message}")
                    }
                )
                .onErrorResumeNext (Observable.just("observable from onErrorResumeNext"))
                .subscribe({
                    // onNext
                    "subscribe onNext: $it".log()
                }, {
                    // onError
                    "subscribe onError".log()
                }, {
                    // onComplete
                    "subscribe onComplete".log()
                })
    }

    private fun testOnErrorReturn() {
        Observable.just("testOnErrorReturn")
                .map { val i = 0
                    if (i == 0) throw Exception("testOnErrorReturn")
                    "map result"
                }
                .onErrorReturn({
                    val tmp = "catch ${it::class.java.simpleName} ${it.message}"
                    tmp.log()
                    tmp
                })
                .subscribe({
                    // onNext
                    "subscribe onNext: $it".log()
                }, {
                    // onError
                    "subscribe onError".log()
                }, {
                    // onComplete
                    "subscribe onComplete".log()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
