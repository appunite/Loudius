package com.appunite.loudius.di

import com.appunite.loudius.ui.MainViewModel
import com.appunite.loudius.ui.authenticating.AuthenticatingViewModel
import com.appunite.loudius.ui.login.LoginScreenViewModel
import com.appunite.loudius.ui.pullrequests.PullRequestsViewModel
import com.appunite.loudius.ui.reviewers.ReviewersViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AuthenticatingViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::PullRequestsViewModel)
    viewModelOf(::ReviewersViewModel)
    viewModelOf(::MainViewModel)
}
