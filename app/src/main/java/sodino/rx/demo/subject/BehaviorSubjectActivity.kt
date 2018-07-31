package sodino.rx.demo.subject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.activity_subject.*
import sodino.rx.demo.R
import sodino.rx.demo.TestEvent1
import sodino.rx.demo.TestEvent2
import sodino.rx.demo.log

class BehaviorSubjectActivity : AppCompatActivity(), View.OnClickListener {
    var idx = 0L
    lateinit var disposable1 : Disposable
    lateinit var disposable2 : Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        btnRegiste.setOnClickListener(this)
        btnFireEvent1.setOnClickListener(this)
        btnFireEvent2.setOnClickListener(this)
        btnUnregiste.setOnClickListener(this)
    }


    fun testReplay() {
        val bus = ReplaySubject.create<String>()
//        val bus = ReplaySubject.createWithSize<String>(1)
        bus.onNext("test 1 ")
        bus.onNext("test 2 ")

        bus.subscribe { "$it".log() }

        bus.onNext("test 3 ")
        bus.onNext("test 4 ")
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnRegiste -> {
                register()

//                testReplay()
            }
            R.id.btnFireEvent1 -> {
                BehaviorBus.post(TestEvent1(idx++))
//                ReplayStringBus.post("fireEvent ${idx ++}")
            }
            R.id.btnFireEvent2 -> {
                BehaviorBus.post(TestEvent2(idx++))
//                ReplayStringBus.post("fireEvent ${idx ++}")
            }
            R.id.btnUnregiste -> {
                // Disposed之后，不会再收到新消息通知
                if (!disposable1.isDisposed) {
                    disposable1.dispose()
                }
                if (!disposable2.isDisposed) {
                    disposable2.dispose()
                }
            }
        }
    }

    private fun register() {
        "register new subscriber".log()

        disposable1 = BehaviorBus.toObservable(TestEvent1::class.java)
                .subscribe { "callback TestEvent1 ${it.id}".log() }
        disposable2 = BehaviorBus.toObservable(TestEvent2::class.java)
                .subscribe { it ->
                    var i = 0
                    i ++
                    "callback TestEvent2 ${it.id}".log() }

//        disposable = ReplayStringBus.toObservable()
//                .subscribe { "replay action ${it}".log() }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (!disposable1.isDisposed) {
            disposable1.dispose()
        }
        if (!disposable2.isDisposed) {
            disposable2.dispose()
        }
    }
}
