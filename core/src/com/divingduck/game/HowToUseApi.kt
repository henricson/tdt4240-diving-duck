package com.divingduck.game

import io.swagger.client.apis.ScoreApi
import io.swagger.client.infrastructure.ClientException
import io.swagger.client.infrastructure.ServerException
import io.swagger.client.models.ScoreDTO
import io.swagger.client.models.ScoreResponse

object HowToUseApi {
    val apiInstance = ScoreApi("https://divingduckserver-v2.azurewebsites.net/")

    private fun getScoresExample() {
        try {
            val result : kotlin.Array<ScoreResponse> = apiInstance.apiScoreGet()
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
            val result : ScoreResponse = apiInstance.apiScorePost(ScoreDTO(1))
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