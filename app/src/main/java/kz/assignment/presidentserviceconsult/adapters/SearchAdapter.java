package kz.assignment.presidentserviceconsult.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kz.assignment.presidentserviceconsult.R;
import kz.assignment.presidentserviceconsult.models.Repository;
import kz.assignment.presidentserviceconsult.models.User;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_USER = 1;
    private static final int TYPE_REPO = 2;
    private List<Object> items;

    public SearchAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof User) {
            return TYPE_USER;
        } else {
            return TYPE_REPO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repo, parent, false);
            return new RepoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_USER) {
            ((UserViewHolder) holder).bind((User) items.get(position));
        } else {
            ((RepoViewHolder) holder).bind((Repository) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder для пользователей
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageAvatar;
        private TextView textUserName;
        private TextView textUserScore;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUserScore = itemView.findViewById(R.id.textUserScore);
        }

        public void bind(User user) {
            textUserName.setText(user.getLogin());
            textUserScore.setText(String.valueOf(user.getScore()));

            // Загрузка аватара пользователя с помощью Glide
            Glide.with(itemView.getContext())
                    .load(user.getAvatar_url())
                    .placeholder(R.drawable.ic_launcher_foreground) // Заглушка при загрузке
                    .error(R.drawable.ic_error) // Ошибка загрузки
                    .into(imageAvatar);

            itemView.setOnClickListener(v -> {
                // Открываем профиль пользователя в браузере
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getHtml_url()));
                itemView.getContext().startActivity(browserIntent);
            });
        }
    }

    // ViewHolder для репозиториев
    public static class RepoViewHolder extends RecyclerView.ViewHolder {
        private TextView repoName;
        private TextView forksCount;
        private TextView repoDescription;

        public RepoViewHolder(@NonNull View itemView) {
            super(itemView);
            repoName = itemView.findViewById(R.id.textRepoName);
            forksCount = itemView.findViewById(R.id.textForksCount);
            repoDescription = itemView.findViewById(R.id.textRepoDescription);
        }

        public void bind(Repository repo) {
            repoName.setText(repo.getName());
            forksCount.setText(String.valueOf(repo.getForks_count()));
            repoDescription.setText(repo.getDescription());

            itemView.setOnClickListener(v -> {
                // Открываем репозиторий в браузере
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(repo.getHtml_url()));
                itemView.getContext().startActivity(browserIntent);
            });
        }
    }
}
