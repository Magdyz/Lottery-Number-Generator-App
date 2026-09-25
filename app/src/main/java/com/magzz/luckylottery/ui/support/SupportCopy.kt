package com.magzz.luckylottery.ui.support

/**
 * Every word the support sheet says.
 *
 * Note: this deliberately asks for "a review", not "a 5-star review". Play's
 * Ratings & Reviews policy treats soliciting a specific rating as review
 * manipulation, which can get the listing suspended.
 */
object SupportCopy {

    const val HEART_DESCRIPTION = "Support this app"

    const val TITLE = "Keeping this one free"

    const val BODY =
        "Lucky Lottery is a side project built by one person. No ads, no " +
            "subscription, no tracking, nothing locked behind a payment, and I " +
            "would rather keep it that way.\n\n" +
            "If it's been useful, a small tip covers the test phones and the " +
            "coffee. Entirely optional, and nothing in the app changes either way."

    const val REVIEW_TITLE = "Leave a review"

    const val REVIEW_BODY = "Free, and it helps other players find an honest picker."

    const val DONATE_TITLE = "Buy me a coffee"

    const val DONATE_BODY = "A one-off tip, opened in your browser."
}
