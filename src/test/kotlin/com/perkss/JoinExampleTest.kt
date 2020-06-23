package com.perkss

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class JoinExampleTest {

    @BeforeEach
    fun setup() {
    }

    @Test
    fun incrementPipeline() {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        // configure your test environment
        env.parallelism = 2

        // values are collected in a static variable
        CollectSink.values.clear()

        // create a stream of custom elements and apply transformations
        val userDetails = env.fromElements(UserDetails(1, "Sandra"), UserDetails(3, "Mohammed"), UserDetails(2, "Joe"))
        val userClicks = env.fromElements(UserClicks(1, 3), UserClicks(2, 2), UserClicks(3, 1))

        JoinExample
            .topology(userDetails, userClicks)
            .addSink(CollectSink())

        // execute
        env.execute()

        // verify your results
        assertTrue(
            CollectSink.values.containsAll(
                listOf(
                    UserAnalysis(1, "Sandra", 3),
                    UserAnalysis(3, "Mohammed", 1),
                    UserAnalysis(2, "Joe", 2)
                )
            )
        )
    }

    // create a testing sink
    private class CollectSink : SinkFunction<UserAnalysis> {

        companion object {
            // must be static
            val values: MutableList<UserAnalysis> = mutableListOf()
        }

        override fun invoke(value: UserAnalysis, context: SinkFunction.Context) {
            values.add(value)
        }

    }
}