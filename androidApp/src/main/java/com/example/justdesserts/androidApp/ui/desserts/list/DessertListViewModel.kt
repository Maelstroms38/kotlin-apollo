package com.example.justdesserts.androidApp.ui.desserts.list

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.api.ApolloExperimental
import com.example.justdesserts.shared.repository.DessertRepository
import com.example.justdesserts.shared.cache.Dessert
import kotlinx.coroutines.flow.Flow

@ApolloExperimental
class DessertListViewModel constructor(private val repository: DessertRepository): ViewModel() {
  val desserts: Flow<PagingData<Dessert>> = Pager(PagingConfig(pageSize = 10)) {
    DessertDataSource(repository)
  }.flow
}