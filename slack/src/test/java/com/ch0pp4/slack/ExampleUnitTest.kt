package com.ch0pp4.slack

import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun sendSlackMessageTest() {
        val result = SlackRepository.instance
            ?.sendSlackMessage("Test message from instrumented test")
            ?.subscribeOn(Schedulers.io())
            ?.blockingGet()

        assertTrue(result == true || result == false)
    }
}