package com.appunite.loudius.di

import okhttp3.Interceptor
import okhttp3.Response

object TestInterceptor : Interceptor {
    var testInterceptor: Interceptor? = null
    override fun intercept(chain: Interceptor.Chain): Response {
        val interceptor = testInterceptor ?: DoNothingInterceptor
        return interceptor.intercept(chain)
    }
}

private object DoNothingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(chain.request())
}
