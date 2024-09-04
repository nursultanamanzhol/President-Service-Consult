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

class SearchAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_REPO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is User -> TYPE_USER
            is Repository -> TYPE_REPO
            else -> throw IllegalArgumentException("Unknown type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER -> UserViewHolder(inflater.inflate(R.layout.item_user, parent, false))
            TYPE_REPO -> RepoViewHolder(inflater.inflate(R.layout.item_repo, parent, false))
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> holder.bind(items[position] as User)
            is RepoViewHolder -> holder.bind(items[position] as Repository)
        }
    }

    override fun getItemCount(): Int = items.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageAvatar: ImageView = itemView.findViewById(R.id.imageAvatar)
        private val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        private val textUserScore: TextView = itemView.findViewById(R.id.textUserScore)

        fun bind(user: User) {
            textUserName.text = user.login
            textUserScore.text = user.score.toString()

            // Загрузка аватара пользователя с помощью Glide
            Glide.with(itemView.context)
                .load(user.avatar_url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_error)
                .into(imageAvatar)

            itemView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(user.html_url))
                itemView.context.startActivity(browserIntent)
            }
        }
    }

    class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val repoName: TextView = itemView.findViewById(R.id.textRepoName)
        private val forksCount: TextView = itemView.findViewById(R.id.textForksCount)
        private val repoDescription: TextView = itemView.findViewById(R.id.textRepoDescription)

        fun bind(repo: Repository) {
            repoName.text = repo.name
            forksCount.text = repo.forks_count.toString()
            repoDescription.text = repo.description

            itemView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.html_url))
                itemView.context.startActivity(browserIntent)
            }
        }
    }
}
