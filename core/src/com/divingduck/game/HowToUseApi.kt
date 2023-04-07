package com.divingduck.game

import com.divingduck.apiclient.apis.ScoreApi
import com.divingduck.apiclient.infrastructure.ClientException
import com.divingduck.apiclient.infrastructure.ServerException
import com.divingduck.apiclient.models.Score
import com.divingduck.apiclient.models.ScoreDTO

object HowToUseApi {
    val apiInstance = ScoreApi("https://divingduckserver-v2.azurewebsites.net/")

    private fun getScoresExample() {
        try {
            val result : kotlin.Array<Score> = apiInstance.apiScoreGet()
            result.forEach { println(it) }
        } catch (e: ClientException) {
            println("4xx response calling ScoreApi#apiScoreGet")
            e.printStackTrace()
        } catch (e: ServerException) {
            println("5xx response calling ScoreApi#apiScoreGet")
            e.printStackTrace()
        }
    }
    private fun postScoreExample() {
        try {
            val result : Score = apiInstance.apiScorePost(ScoreDTO(1))
            println(result)
        } catch (e: ClientException) {
            println("4xx response calling ScoreApi#apiScoreGet")
            e.printStackTrace()
        } catch (e: ServerException) {
            println("5xx response calling ScoreApi#apiScoreGet")
            e.printStackTrace()
        }
    }
    @JvmStatic
    fun main(args: Array<String>) {
        getScoresExample()
    }
}