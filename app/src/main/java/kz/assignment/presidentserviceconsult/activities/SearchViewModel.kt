package kz.assignment.presidentserviceconsult.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kz.assignment.presidentserviceconsult.network.NetworkService
import kz.assignment.presidentserviceconsult.utils.Utils

class SearchViewModel : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _searchResults = MutableLiveData<List<Any>>()
    val searchResults: LiveData<List<Any>> get() = _searchResults

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _history = MutableLiveData<List<String>>()
    val history: LiveData<List<String>> get() = _history

    private var isInternetAvailable = false

    // Метод для установки доступности интернета
    fun setInternetAvailability(isAvailable: Boolean) {
        isInternetAvailable = isAvailable
    }

    // Метод для выполнения поиска
    @SuppressLint("CheckResult")
    fun searchGitHub(query: String) {
        _loading.value = true
        _errorMessage.value = null

        val disposable = Observable.zip(
            NetworkService.getInstance().gitHubApi.searchUsers(query),
            NetworkService.getInstance().gitHubApi.searchRepositories(query)
        ) { usersResponse, reposResponse ->
            val combinedList = mutableListOf<Any>()
            if (usersResponse.items.isNotEmpty()) {
                combinedList.add(Utils.UserHeader())
                combinedList.addAll(usersResponse.items)
            }
            if (reposResponse.items.isNotEmpty()) {
                combinedList.add(Utils.RepositoryHeader())
                combinedList.addAll(reposResponse.items)
            }
            combinedList
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ combinedList ->
                _loading.value = false
                if (combinedList.isEmpty()) {
                    _errorMessage.value = "Ничего не найдено"
                } else {
                    _searchResults.value = combinedList
                }
            }, { throwable ->
                _loading.value = false
                _errorMessage.value = throwable.message ?: "Произошла ошибка"
            })

        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()  // Освобождение подписок при уничтожении ViewModel
    }

    // Метод для сохранения истории поиска
    fun saveSearchQuery(context: Context, query: String) {
        val sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        val searchHistory = sharedPreferences.getString("history", "") ?: ""
        val searchHistoryList = searchHistory.split(",").filter { it.isNotEmpty() }.toMutableList()

        // Обновляем историю, добавляя новый запрос
        searchHistoryList.remove(query)
        if (searchHistoryList.size >= 5) searchHistoryList.removeAt(0)
        if (query.isNotEmpty()) searchHistoryList.add(query)

        // Сохраняем историю в SharedPreferences
        sharedPreferences.edit().putString("history", searchHistoryList.joinToString(",")).apply()
    }

    // Метод для загрузки истории поиска
    fun loadSearchHistory(context: Context) {
        val sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        val searchHistory = sharedPreferences.getString("history", "") ?: ""
        _history.value = if (searchHistory.isNotEmpty()) {
            searchHistory.split(",").filter { it.isNotEmpty() }
        } else {
            listOf()
        }
    }
}
