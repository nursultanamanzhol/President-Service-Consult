package kz.assignment.presidentserviceconsult.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kz.assignment.presidentserviceconsult.R
import kz.assignment.presidentserviceconsult.adapters.SearchAdapter
import kz.assignment.presidentserviceconsult.utils.Utils

class SearchActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchField: EditText
    private lateinit var clearSearchButton: ImageButton
    private lateinit var historyTextView: TextView
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: MaterialCardView
    private lateinit var errorLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        initViews()

        viewModel.setInternetAvailability(Utils().isInternetAvailable(this))

        // Подписка на изменения в поисковых результатах
        viewModel.searchResults.observe(this, Observer { searchResults ->
            if (searchResults.isEmpty()) {
                errorMessage.text = "Ничего не найдено"
                errorLayout.visibility = View.VISIBLE
                historyTextView.visibility =
                    View.GONE  // Скрыть historyTextView при отсутствии результатов
            } else {
                adapter = SearchAdapter(searchResults, onHistoryClick = { query ->
                    searchField.setText(query)
                    viewModel.searchGitHub(query)
                })
                recyclerView.adapter = adapter
                recyclerView.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
                historyTextView.visibility =
                    View.GONE  // Скрыть historyTextView при успешном поиске
            }
        })

        // Подписка на изменения в состоянии загрузки
        viewModel.loading.observe(this, Observer { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                historyTextView.visibility = View.GONE // Скрыть historyTextView во время загрузки
                errorLayout.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        // Подписка на сообщения об ошибках
        viewModel.errorMessage.observe(this, Observer { message ->
            if (!message.isNullOrEmpty()) {
                errorMessage.text = message
                errorLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.GONE
                historyTextView.visibility = View.GONE  // Скрыть historyTextView при ошибке
            } else {
                errorLayout.visibility = View.GONE
                // Показать историю после скрытия ошибки
                viewModel.loadSearchHistory(this)
            }
        })

        // Подписка на историю поиска
        viewModel.history.observe(this, Observer { history ->
            showSearchHistory(history)
        })

        viewModel.loadSearchHistory(this)

        setupSearchField()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        progressBar = findViewById(R.id.progressBar)
        searchField = findViewById(R.id.searchField)
        historyTextView = findViewById(R.id.historyTextView)
        clearSearchButton = findViewById(R.id.clear_search_button)

        errorMessage = findViewById(R.id.errorMessage)
        retryButton = findViewById(R.id.retryButton)
        errorLayout = findViewById(R.id.errorLayout)

        retryButton.setOnClickListener {
            val query = searchField.text.toString()
            if (query.length >= 3) {
                viewModel.searchGitHub(query)
            } else {
                searchField.setHint("Введите не менее 3 символов")
            }
        }

    }

    private fun setupSearchField() {
        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    clearSearchButton.visibility = View.VISIBLE
                } else {
                    clearSearchButton.visibility = View.GONE
                    viewModel.loadSearchHistory(this@SearchActivity)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clearSearchButton.setOnClickListener {
            searchField.text.clear()
        }

        searchField.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchField.text.toString()
                if (query.length >= 3) {
                    viewModel.searchGitHub(query)
                    viewModel.saveSearchQuery(this, query)
                } else {
                    Toast.makeText(this, "Введите не менее 3 символов", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        findViewById<View>(R.id.searchCard).setOnClickListener {
            val query = searchField.text.toString()
            if (query.length >= 3) {
                viewModel.searchGitHub(query)
                viewModel.saveSearchQuery(this, query)
            } else {
                Toast.makeText(this, "Введите не менее 3 символов", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSearchHistory(history: List<String>) {
        if (history.isNotEmpty()) {
            adapter = SearchAdapter(history, onHistoryClick = { query ->
                searchField.setText(query)
                viewModel.searchGitHub(query)
            })
            recyclerView.adapter = adapter
            historyTextView.visibility =
                View.VISIBLE  // Показываем historyTextView, если есть история
        } else {
            recyclerView.adapter = null
            historyTextView.visibility = View.GONE  // Скрываем historyTextView, если история пуста
        }
    }
}
