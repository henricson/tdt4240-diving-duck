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
 * @param timeElapsed 
 * @param userId 
 * @param user 
 */

data class Score (

    @Json(name = "id")
    val id: kotlin.Int,

    @Json(name = "timeElapsed")
    val timeElapsed: kotlin.Float? = null,

    @Json(name = "userId")
    val userId: kotlin.Int? = null,

    @Json(name = "user")
    val user: User? = null

)

