package xyz.volgoak.wordlearning

import org.junit.Test
import java.util.concurrent.CountDownLatch

class CountTest {
    private var countDown: CountDownLatch? = null

    @Test
    fun testCountDown() {
        countDown = CountDownLatch(10)
        for(i in 0..9) {
            Worker(i).doSomething()
        }
        countDown?.await()
        println("All workers finished")
    }

    inner class Worker(val num: Int) {
        fun doSomething() {
            Thread{
                Thread.sleep(500L * num)
                println("Finished work for $num")
                countDown?.countDown()
            }.start()
        }
    }
}