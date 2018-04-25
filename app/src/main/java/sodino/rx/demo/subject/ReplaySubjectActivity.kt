package sodino.rx.demo.subject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.activity_subject.*
import sodino.rx.demo.R
import sodino.rx.demo.TestEvent
import sodino.rx.demo.log

class ReplaySubjectActivity : AppCompatActivity(), View.OnClickListener {
    var idx = 0L
    lateinit var disposable : Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        btnRegiste.setOnClickListener(this)
        btnFireEvent.setOnClickListener(this)
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
            }
            R.id.btnFireEvent -> {
                BehaviorBus.post(TestEvent(idx++))
            }
            R.id.btnUnregiste -> {
                // Disposed之后，不会再收到新消息通知
                if (!disposable.isDisposed) {
                    disposable.dispose()
                }
            }
        }
    }

    private fun register() {
        "register new subscriber".log()

        disposable = BehaviorBus.toObservable(TestEvent::class.java)
                .subscribe { "callback ${it.id}".log() }

//        disposable = ReplayStringBus.toObservable()
//                .subscribe { "replay action ${it}".log() }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
