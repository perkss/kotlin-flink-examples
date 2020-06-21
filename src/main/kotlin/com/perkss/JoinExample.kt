package com.perkss

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.java.DataSet
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.util.Collector


object JoinExample {

    fun topology() {

        val env = ExecutionEnvironment.getExecutionEnvironment()

        val text: DataSet<String> = env.fromElements(
                "Hello",
                "Kotlin examples with Flink Fun")

        val wordCounts = text
                .flatMap(FlatMapFunction<String, Tuple2<String, Int>>
                { line: String, out: Collector<Tuple2<String, Int>> ->
                    line.split(" ").forEach { word ->
                        out.collect(Tuple2<String, Int>(word, 1))
                    }

                })
                .groupBy(0)
                .sum(1)

        wordCounts.print()

    }


}

fun main() {
    JoinExample.topology()
}