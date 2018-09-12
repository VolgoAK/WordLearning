package xyz.volgoak.wordlearning.admob

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import org.json.JSONObject
import timber.log.Timber
import xyz.volgoak.wordlearning.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by alex on 5/8/18.
 */
object AdsManager {

    private const val APP_ID_KEY = "app_id"
    private const val BANNER_ID_KEY = "banner_id"
    private const val TEST_DEVICE_KEY = "test_device_id"

    private const val BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111"

    private var appId: String? = null
    private var bannerId: String? = null
    private var testDeviceId: String? = null

    var initialized = false

    fun initAds(context: Context) {
        try {
            val jsonObject = loadIds(context)
            appId = jsonObject.optString(APP_ID_KEY)
            bannerId = jsonObject.optString(BANNER_ID_KEY)
            testDeviceId = jsonObject.optString(TEST_DEVICE_KEY)

            MobileAds.initialize(context, appId)

            initialized = true
        } catch (ex: Exception) {
            //Not initialized, do nothing just log
            Timber.e(ex)
        }
    }

    fun createBannerRequest() : AdRequest {
        val adRequestBuilder = AdRequest.Builder()

        if(BuildConfig.DEBUG) {
            adRequestBuilder.addTestDevice(testDeviceId)
        }

        return adRequestBuilder.build()
    }

    fun getBannerId(): String? {
        return if (BuildConfig.DEBUG) BANNER_TEST_ID
        else bannerId
    }

    private fun loadIds(context: Context): JSONObject {
        val inputStream = context.assets.open("ads_keys.json")
        val bufferedStream = BufferedReader(InputStreamReader(inputStream))

        val builder = StringBuilder()
        var line = bufferedStream.readLine()

        while (line != null) {
            builder.append(line)
            line = bufferedStream.readLine()
        }

        return JSONObject(builder.toString())
    }


}