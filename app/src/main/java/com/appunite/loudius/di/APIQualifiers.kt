package com.appunite.loudius.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseAPI

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthAPI
