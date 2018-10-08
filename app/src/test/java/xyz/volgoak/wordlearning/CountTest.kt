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

    @Test
    fun mathTest() {
        val x = Math.pow(5.0, 3.0)
        println(x)
        val y = x.rem(13)
        println(y)
    }

    @Test
    fun mathTest2() {
        val x = 5
        val n = 3
        val m = 13
        var y = 1
        for(i in 1..n) {
            y = (y * x).rem(m)
        }

        println(y)
    }

    @Test
    fun testNOD() {
        println(getNOD(555555, 6122256))
    }

    fun getNOD(a: Int, b: Int): Int {
        println("a $a b $b")
        if(a == 0) return b
        return getNOD(b.rem(a), a)
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