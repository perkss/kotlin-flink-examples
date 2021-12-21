package com.perkss

import org.apache.flink.api.common.eventtime.TimestampAssigner
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import java.time.Instant

class MyKeyedProcessFunction : KeyedProcessFunction<Int, UserClicks, UserClicks>() {

    companion object {
        private val logger = LoggerFactory.getLogger(MyKeyedProcessFunction::class.java)
    }

    override fun open(parameters: Configuration?) {
        super.open(parameters)
    }

    override fun processElement(value: UserClicks, ctx: Context, out: Collector<UserClicks>) {
        logger.info(
            "Processing Clicks {}, Timestamp {}, Watermark {}, Processing Time {}",
            value, Instant.ofEpochMilli(ctx.timestamp()), Instant.ofEpochMilli(ctx.timerService().currentWatermark()),
            Instant.ofEpochMilli(ctx.timerService().currentProcessingTime())
        )
        // Simply publish out
        out.collect(value)
    }

    override fun onTimer(timestamp: Long, ctx: OnTimerContext?, out: Collector<UserClicks>?) {
        super.onTimer(timestamp, ctx, out)
    }
}

object WatermarkExample {

    private val logger = LoggerFactory.getLogger(JoinExample::class.java)

    fun topology(
        clickStream: DataStream<UserClicks>
    ): SingleOutputStreamOperator<UserClicks> {
        //https://issues.apache.org/jira/browse/FLINK-23979

        return clickStream
            .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps<UserClicks>()
                .withTimestampAssigner(
                    TimestampAssignerSupplier {
                        TimestampAssigner { element, _ ->
                            element.eventTime.toEpochMilli()
                        }
                    }
                ))
            .keyBy(object : KeySelector<UserClicks, Int> {
                override fun getKey(it: UserClicks): Int = it.id
            })
            .process(MyKeyedProcessFunction())
            .map(object : MapFunction<UserClicks, UserClicks> {
                override fun map(value: UserClicks): UserClicks {
                    logger.info("User Clicks {}", value.id)
                    return value
                }
            })
    }


}