package kz.assignment.presidentserviceconsult.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kz.assignment.presidentserviceconsult.R
import kz.assignment.presidentserviceconsult.adapters.SearchAdapter
import kz.assignment.presidentserviceconsult.network.NetworkService
import kz.assignment.presidentserviceconsult.models.User
import kz.assignment.presidentserviceconsult.models.Repository

class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        progressBar = findViewById(R.id.progressBar)

        // Поиск при нажатии на кнопку
        findViewById<View>(R.id.searchButton).setOnClickListener {
            val query = findViewById<EditText>(R.id.searchField).text.toString()
            if (query.length >= 3) {
                searchGitHub(query)
            } else {
                Toast.makeText(this, "Введите не менее 3 символов", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchGitHub(query: String) {
        progressBar.visibility = View.VISIBLE

        // Выполняем запросы на пользователей и репозитории
        Observable.zip(
            NetworkService.getInstance().gitHubApi.searchUsers(query),
            NetworkService.getInstance().gitHubApi.searchRepositories(query),
            { usersResponse, reposResponse ->
                val combinedList = mutableListOf<Any>()
                combinedList.addAll(usersResponse.items)
                combinedList.addAll(reposResponse.items)
                combinedList
            }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ combinedList ->
                progressBar.visibility = View.GONE
                adapter = SearchAdapter(combinedList)
                recyclerView.adapter = adapter
            }, { throwable ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Ошибка: ${throwable.message}", Toast.LENGTH_SHORT).show()
            })
    }
}
