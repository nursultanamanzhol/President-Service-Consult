package kz.assignment.presidentserviceconsult.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kz.assignment.presidentserviceconsult.R
import kz.assignment.presidentserviceconsult.models.Repository
import kz.assignment.presidentserviceconsult.models.User
import kz.assignment.presidentserviceconsult.utils.Utils

class SearchAdapter(
    private val items: List<Any>,
    private val onHistoryClick: (String) -> Unit // Callback для клика по элементу истории
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HISTORY = 0
        private const val TYPE_USER_HEADER = 1
        private const val TYPE_USER = 2
        private const val TYPE_REPO_HEADER = 3
        private const val TYPE_REPO = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> TYPE_HISTORY // История поиска — это строка
            is Utils.UserHeader -> TYPE_USER_HEADER // Заголовок для пользователей
            is User -> TYPE_USER
            is Utils.RepositoryHeader -> TYPE_REPO_HEADER // Заголовок для репозиториев
            is Repository -> TYPE_REPO
            else -> throw IllegalArgumentException("Unknown type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HISTORY -> HistoryViewHolder(inflater.inflate(R.layout.item_history, parent, false), onHistoryClick)
            TYPE_USER_HEADER -> UserHeaderViewHolder(inflater.inflate(R.layout.item_user_header, parent, false))
            TYPE_USER -> UserViewHolder(inflater.inflate(R.layout.item_user, parent, false))
            TYPE_REPO_HEADER -> RepoHeaderViewHolder(inflater.inflate(R.layout.item_repository_header, parent, false))
            TYPE_REPO -> RepoViewHolder(inflater.inflate(R.layout.item_repo, parent, false))
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HistoryViewHolder -> holder.bind(items[position] as String)
            is UserHeaderViewHolder -> holder.bind() // Заголовок пользователей, данные не требуются
            is UserViewHolder -> holder.bind(items[position] as User)
            is RepoHeaderViewHolder -> holder.bind() // Заголовок репозиториев, данные не требуются
            is RepoViewHolder -> holder.bind(items[position] as Repository)
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolder для отображения элемента истории поиска
    class HistoryViewHolder(itemView: View, private val onClick: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val historyText: TextView = itemView.findViewById(R.id.historyItemText)

        fun bind(historyItem: String) {
            historyText.text = historyItem
            itemView.setOnClickListener {
                onClick(historyItem)  // При клике передаем строку в callback
            }
        }
    }

    // ViewHolder для заголовка пользователей
    class UserHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            // Настройка заголовка для пользователей, если требуется
        }
    }

    // ViewHolder для отображения пользователя
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageAvatar: ImageView = itemView.findViewById(R.id.imageAvatar)
        private val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        private val textUserScore: TextView = itemView.findViewById(R.id.textUserScore)

        fun bind(user: User) {
            textViewUserName.text = user.login
            textUserScore.text = user.score.toString()

            // Загрузка аватара пользователя с помощью Glide
            Glide.with(itemView.context)
                .load(user.avatar_url)
                .placeholder(R.drawable.ic_github_img)
                .error(R.drawable.ic_error)
                .into(imageAvatar)

            itemView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(user.html_url))
                itemView.context.startActivity(browserIntent)
            }
        }
    }

    // ViewHolder для заголовка репозиториев
    class RepoHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            // Настройка заголовка для репозиториев, если необходимо
        }
    }

    // ViewHolder для отображения репозитория
    class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val repoName: TextView = itemView.findViewById(R.id.textRepoName)
        private val forksCount: TextView = itemView.findViewById(R.id.textForksCount)
        private val repoDescription: TextView = itemView.findViewById(R.id.textRepoDescription)

        fun bind(repo: Repository) {
            repoName.text = repo.name
            forksCount.text = repo.forks_count.toString()
            repoDescription.text = repo.description ?: "No description"

            itemView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.html_url))
                itemView.context.startActivity(browserIntent)
            }
        }
    }
}

