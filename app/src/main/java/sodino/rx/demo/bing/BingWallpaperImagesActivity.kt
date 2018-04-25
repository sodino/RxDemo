package sodino.rx.demo.bing

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bing_imgs.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import sodino.rx.demo.R
import sodino.rx.demo.log

/**
 * 拉取bing.com的背景壁纸图片，并显示出来
 * */
class BingWallpaperImagesActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var bindAdapter : BingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bing_imgs)

        btnTest.setOnClickListener(this)

        recyclerView.adapter = BingAdapter(mutableListOf<BingImg>())
        recyclerView.layoutManager = LinearLayoutManager(MainActivity@this, LinearLayoutManager.VERTICAL, false)
    }


    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnTest -> {
                fetchBingImgs()
            }
        }
    }

    private fun fetchBingImgs() {
        Observable.just(BingImg.Companion.URL_BING_ALL_IMGs)
                .observeOn(Schedulers.io())
                .map {
                    // 获取bing 的json内容
                    "fetch url[$it]".log()
                    val req = Request.Builder().url(it).build()
                    val resp = OkHttpClient().newCall(req).execute()
                    val json = resp.body()?.string()
                    json
                }.map {
                    // 解析json
                    "parse json".log()
                    val jsonBing = JSONObject(it)
                    val jsonArrImgs = jsonBing.optJSONArray("images")

                    val list = mutableListOf<BingImg>()

                    for (i in 0 until jsonArrImgs.length()) {
                        val tmp = jsonArrImgs.opt(i)
                        val bingImg = BingImg.parse(tmp as JSONObject)
                        bingImg?.let { list.add(it) }
                    }

                    list
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<BingImg>> {
                    override fun onComplete() {
                        "onComplete".log()
                    }

                    override fun onSubscribe(d: Disposable) {
                        "onSubscribe".log()
                    }

                    override fun onNext(list: List<BingImg>) {
                        "onNext : receive listBingImg.length=${list.size}".log()
                        (recyclerView.adapter as BingAdapter).updateList(list)
                        recyclerView.adapter.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        "onError ${e::class.java.simpleName} ${e.message}".log()
                    }
                })

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class BingAdapter(var list : MutableList<BingImg>) : RecyclerView.Adapter<BingAdapter.BingHolder>() {
        fun updateList(newList : List<BingImg>) {
            list.clear()
            list.addAll(newList)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BingHolder {
            val vBing = LayoutInflater.from(parent.context).inflate(R.layout.bing_layout, parent, false)

            return BingHolder(vBing)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: BingHolder, position: Int) {
            val bingImg = list[position]
            holder.txtView.text = bingImg.copyright
            Glide.with(holder.imgView)
                    .load(bingImg.url)
                    .into(holder.imgView)
        }

        inner class BingHolder(itemView :View) : RecyclerView.ViewHolder(itemView) {
            val imgView : ImageView = itemView.findViewById(R.id.imgView)
            val txtView : TextView = itemView.findViewById(R.id.txtView)
        }
    }

}
