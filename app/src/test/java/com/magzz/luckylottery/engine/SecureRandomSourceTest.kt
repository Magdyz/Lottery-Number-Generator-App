package com.magzz.luckylottery.engine

import org.junit.Assert.assertTrue
import org.junit.Test

class SecureRandomSourceTest {

    @Test
    fun `nextInt always returns a value in 0 until bound`() {
        for (bound in listOf(1, 2, 5, 12, 50, 1000)) {
            repeat(500) {
                val n = SecureRandomSource.nextInt(bound)
                assertTrue("nextInt($bound) returned $n, expected it in [0,$bound)", n in 0 until bound)
            }
        }
    }
}
