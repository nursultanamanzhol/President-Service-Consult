package kz.assignment.presidentserviceconsult.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kz.assignment.presidentserviceconsult.R;
import kz.assignment.presidentserviceconsult.adapters.SearchAdapter;
import kz.assignment.presidentserviceconsult.network.NetworkService;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);

        // Поиск при нажатии на кнопку
        findViewById(R.id.searchButton).setOnClickListener(view -> {
            String query = ((EditText) findViewById(R.id.searchField)).getText().toString();
            if (query.length() >= 3) {
                searchGitHub(query);
            } else {
                Toast.makeText(this, "Введите не менее 3 символов", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchGitHub(String query) {
        progressBar.setVisibility(View.VISIBLE);

        // Выполняем запросы на пользователей и репозитории
        Observable.zip(
                        NetworkService.getInstance().getGitHubApi().searchUsers(query),
                        NetworkService.getInstance().getGitHubApi().searchRepositories(query),
                        (usersResponse, reposResponse) -> {
                            List<Object> combinedList = new ArrayList<>();
                            combinedList.addAll(usersResponse.getItems());
                            combinedList.addAll(reposResponse.getItems());
                            return combinedList;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(combinedList -> {
                    progressBar.setVisibility(View.GONE);
                    adapter = new SearchAdapter(combinedList);
                    recyclerView.setAdapter(adapter);
//                    recyclerView.setVisibility(View.VISIBLE);
                }, throwable -> {
//                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Ошибка: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
