package com.perkss

import org.apache.flink.api.common.functions.JoinFunction
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.slf4j.LoggerFactory
import java.time.Instant

data class UserDetails(val id: Int, val name: String)
data class UserClicks(val id: Int, val clicks: Int, val eventTime: Instant = Instant.now())
data class UserAnalysis(val id: Int, val name: String, val clicks: Int)

object JoinExample {

    private val logger = LoggerFactory.getLogger(JoinExample::class.java)

    fun topology(
        userStream: DataStream<UserDetails>,
        clickStream: DataStream<UserClicks>
    ): DataStream<UserAnalysis> {
        //https://issues.apache.org/jira/browse/FLINK-23979
        val keyedClickStream = clickStream
            .keyBy(object : KeySelector<UserClicks, Int> {
                override fun getKey(it: UserClicks): Int = it.id
            })

        return userStream
            .keyBy(object : KeySelector<UserDetails, Int> {
                override fun getKey(it: UserDetails): Int = it.id
            })
            .join(keyedClickStream)
            // Join where the ids match
            .where(object : KeySelector<UserDetails, Int> {
                override fun getKey(it: UserDetails): Int = it.id
            })
            .equalTo(object : KeySelector<UserClicks, Int> {
                override fun getKey(it: UserClicks): Int = it.id
            })
            .window(EventTimeSessionWindows.withGap(Time.milliseconds(5)))
            .apply(object : JoinFunction<UserDetails, UserClicks, UserAnalysis> {
                override fun join(userDetails: UserDetails, userClicks: UserClicks): UserAnalysis {
                    logger.info("We have joined {}, {}", userDetails, userClicks)
                    return UserAnalysis(userDetails.id, userDetails.name, userClicks.clicks)
                }
            })
    }


}

fun main() {
    val env = ExecutionEnvironment.getExecutionEnvironment()


    //JoinExample.topology()
}