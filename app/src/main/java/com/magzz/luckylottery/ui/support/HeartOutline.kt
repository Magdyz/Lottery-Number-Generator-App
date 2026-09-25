package com.magzz.luckylottery.ui.support

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * A single-stroke heart, drawn rather than filled.
 *
 * Material's own `FavoriteBorder` is a filled heart with a hole punched through
 * it, so its outline thins at the shoulders and reads as a solid shape at the
 * 22dp this is used at. A real stroked path keeps one even weight all the way
 * round, which is what makes it read as a line drawing and not a small blob.
 *
 * Geometry follows the Material 24dp grid (1.8 units of stroke, ends rounded)
 * so it sits beside the outlined icons in the support sheet without looking like
 * it came from a different set.
 *
 * The stroke colour is black only as a placeholder: `Icon` paints the whole
 * vector with a tint `ColorFilter`, so whatever is set here is replaced.
 */
val HeartOutline: ImageVector by lazy {
    ImageVector.Builder(
        name = "HeartOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            // Starting at the point and sweeping up the left lobe keeps the two
            // halves mirror-exact; an asymmetric heart is obvious even at 22dp.
            moveTo(12f, 20f)
            curveTo(12f, 20f, 4f, 14.7f, 4f, 9.3f)
            curveTo(4f, 6.7f, 5.9f, 5f, 8.1f, 5f)
            curveTo(9.9f, 5f, 11.3f, 6.1f, 12f, 7.4f)
            curveTo(12.7f, 6.1f, 14.1f, 5f, 15.9f, 5f)
            curveTo(18.1f, 5f, 20f, 6.7f, 20f, 9.3f)
            curveTo(20f, 14.7f, 12f, 20f, 12f, 20f)
            close()
        }
    }.build()
}
