/**
 * tdt4240-diving-duck-server
 *
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0
 * 
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.divingduck.client.models

import com.divingduck.client.models.User

import com.squareup.moshi.Json

/**
 * 
 *
 * @param id 
 * @param scoreXPos 
 * @param userId 
 * @param user 
 */

data class Score (

    @Json(name = "id")
    val id: kotlin.Int,

    @Json(name = "scoreXPos")
    val scoreXPos: kotlin.Int? = null,

    @Json(name = "userId")
    val userId: kotlin.Int? = null,

    @Json(name = "user")
    val user: User? = null

)

