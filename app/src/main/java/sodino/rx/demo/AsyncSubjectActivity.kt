package sodino.rx.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.activity_subject.*

class AsyncSubjectActivity : AppCompatActivity(), View.OnClickListener {
    var idx = 0L
    lateinit var disposable : Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_async)

        btnRegiste.setOnClickListener(this)
        btnFireEvent.setOnClickListener(this)
        btnUnregiste.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnRegiste -> {
                register()
            }
            R.id.btnFireEvent -> {
                val id = idx ++
                "fire event $id".log()
                AsyncBus.post(TestEvent(id))
//                ReplayStringBus.post("fireEvent ${idx ++}")
            }
            R.id.btnUnregiste -> {
                // onComplete()触发AsyncSubject发射最后的数据
                AsyncBus.onComplete()
                // Disposed之后，不会再收到新消息通知
                if (!disposable.isDisposed) {
                    disposable.dispose()
                }
            }
        }
    }

    private fun register() {
        "register new subscriber".log()

        disposable = AsyncBus.toObservable(TestEvent::class.java)
                .subscribe { "callback ${it.id}".log() }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
