package com.magzz.luckylottery.ui.support

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * The two outward links in the app. Both are plain `ACTION_VIEW` hand-offs to the
 * Play Store or a browser, so the app still needs no `INTERNET` permission.
 */
object SupportLinks {

    /** Same Ko-fi page as Slumber and CutMyShorts. Blank hides the coffee row. */
    const val DONATE_URL = "https://ko-fi.com/774ESC98BSLA"

    val isDonationConfigured: Boolean get() = DONATE_URL.isNotBlank()

    /**
     * Opens the Play Store listing. `market://` targets the Store app directly; the
     * https form is the fallback for devices without it. The flags keep the Store
     * out of the app's back stack.
     */
    fun openStoreListing(context: Context) {
        val id = context.packageName
        val market = Intent(Intent.ACTION_VIEW, "market://details?id=$id".toUri()).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
            )
        }
        if (!start(context, market)) {
            start(context, Intent(Intent.ACTION_VIEW, WEB_LISTING.format(id).toUri()))
        }
    }

    fun openDonation(context: Context) {
        if (!isDonationConfigured) return
        start(context, Intent(Intent.ACTION_VIEW, DONATE_URL.toUri()))
    }

    // A device with neither a browser nor the Play Store just does nothing.
    private fun start(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    }

    private const val WEB_LISTING = "https://play.google.com/store/apps/details?id=%s"

    private fun String.toUri(): Uri = Uri.parse(this)
}
