package xyz.axxonte.jacquesnoirremastered.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {
    @GET("posts")
    suspend fun getPosts(): Response<MutableList<Post>>

    @GET("posts/{num}")
    suspend fun getPostById(@Path("num") num: Int): Response<Post>
}