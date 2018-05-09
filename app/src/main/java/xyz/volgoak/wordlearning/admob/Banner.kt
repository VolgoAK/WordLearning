package xyz.volgoak.wordlearning.admob

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Created by alex on 5/8/18.
 */
class Banner(context: Context) {
    private val mBannerAdView = AdView(context)

    init {
        mBannerAdView.adSize = AdSize.SMART_BANNER
        mBannerAdView.adUnitId = AdsManager.getBannerId()
    }

    fun loadAdRequest() {
        var request = AdsManager.createBannerRequest()
        mBannerAdView.loadAd(request)
    }

    fun setTargetView(viewGroup: ViewGroup) {
        mBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()

                if (mBannerAdView.parent != null) {
                    (mBannerAdView.parent as ViewGroup).removeView(mBannerAdView)
                }

                viewGroup.removeAllViews()
                viewGroup.addView(mBannerAdView)
            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                viewGroup.removeAllViews()
            }
        }
    }

    fun onPause() {
        mBannerAdView.pause()
    }

    fun onResume() {
        mBannerAdView.resume()
    }

    fun onDestroy() {
        mBannerAdView.destroy()
    }
}