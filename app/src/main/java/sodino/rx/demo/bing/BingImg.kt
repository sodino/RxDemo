package sodino.rx.demo.bing

import org.json.JSONObject

/**
 * Created by sodino on 2018/4/10.
 */
data class BingImg(val url : String = "",
                   val copyright : String = "") {
    companion object {
        val URL_BING_ALL_IMGs   = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=-1&n=9&mkt=en-US"
        val URL_IMG_PREFIX      = "http://s.cn.bing.net/"

        public fun parse(json : JSONObject) : BingImg? {
            val copyright = json.optString("copyright")
            val url = json.optString("url")

            if (url == null || url.isEmpty()) {
                return null
            }

            val fullUrl = URL_IMG_PREFIX + url
            return BingImg(fullUrl, copyright)
        }
    }
}