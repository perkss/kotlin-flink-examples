package com.perkss

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

internal class WatermarkExampleTest {

    @Test
    fun incrementPipeline() {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        // configure your test environment
        env.parallelism = 2

        // values are collected in a static variable
        CollectSink.values.clear()

        val time = Instant.now()
        val time2 = time.plusSeconds(60)
        val time3 = time2.plusSeconds(60)
        // create a stream of custom elements and apply transformations
        val userClicks = env.fromElements(UserClicks(1, 3, time), UserClicks(2, 2, time2), UserClicks(3, 1, time3))

        WatermarkExample
            .topology(userClicks)
            .addSink(CollectSink())

        // execute
        env.execute()

        // verify your results
        assertTrue(
            CollectSink.values.containsAll(
                listOf(
                    UserClicks(1, 3, time),
                    UserClicks(2, 2, time2),
                    UserClicks(3, 1, time3)
                )
            )
        )
    }

    // create a testing sink
    private class CollectSink : SinkFunction<UserClicks> {

        companion object {
            // must be static
            val values: MutableList<UserClicks> = mutableListOf()
        }

        override fun invoke(value: UserClicks, context: SinkFunction.Context) {
            values.add(value)
        }

    }
}