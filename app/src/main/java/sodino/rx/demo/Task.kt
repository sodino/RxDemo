package sodino.rx.demo

import android.util.Log

/**
 * Created by sodino on 2018/3/29.
 */
object Task {
    fun task2s(idx : Int = 0) {
        "task2s before execute : $idx".log()
        Thread.sleep(2000L)
        "task2s after execute : $idx".log()
    }

    fun boolean_2s(idx : Int = 0) : Boolean {
        "boolean_2s before execute : $idx".log()
        Thread.sleep(2000L)
        "boolean_2s after execute : $idx".log()

        return idx % 2 == 0
    }
}


fun String.log() {
    Log.d("RxDemo", Thread.currentThread().name + "->" + this)
}


fun Throwable.log(line : String) {
    Log.e("RxDemo", Thread.currentThread().name +"->" + line + " " + Log.getStackTraceString(this))
}