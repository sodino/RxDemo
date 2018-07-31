package sodino.rx.demo.rxbus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import sodino.rx.demo.R
import sodino.rx.demo.TestEvent1
import sodino.rx.demo.log

class RxBusActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var disposable : Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTest.setOnClickListener(this)

        disposable = RxBus.toObservable(TestEvent1::class.java)
                .subscribe { onNext ->
                    "onCreate() ${onNext.id}".log()
                }

        RxBus.toObservable(TestEvent1::class.java)
                .subscribe { "onCreate()2 ${it.id}".log() }
    }


    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnTest -> {
                testObservable()
                // Disposed之后，不会再收到新消息通知
                if (!disposable.isDisposed) {
                    disposable.dispose()
                }
            }
        }
    }

    private fun testObservable() {
        RxBus.post(TestEvent1(101))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
