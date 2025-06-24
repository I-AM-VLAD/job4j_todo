package com.example.news1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var user_field: EditText? = null
    private var main_btn: Button? = null

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsArticles = mutableListOf<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user_field = findViewById(R.id.user_field)
        main_btn = findViewById(R.id.main_btn)

        newsRecyclerView = findViewById(R.id.news_recycler_view)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(newsArticles)
        newsRecyclerView.adapter = newsAdapter

        main_btn?.setOnClickListener {
            val queryInput = user_field?.text?.toString()?.trim()

            if (queryInput.isNullOrEmpty()) {
                Toast.makeText(this, "Введите поисковый запрос", Toast.LENGTH_LONG).show()
                newsArticles.clear()
                newsAdapter.notifyDataSetChanged()
            } else {
                fetchNewsByTitle(queryInput)
            }
        }
    }

    private fun fetchNewsByTitle(query: String) {
        val newsApiKey: String = "e0ef0c70e24143398ea4c97abfff6806"

        val newsUrlString: String = "https://newsapi.org/v2/everything?q=$query&language=en&sortBy=relevancy&apiKey=$newsApiKey"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val apiResponse = URL(newsUrlString).readText()

                withContext(Dispatchers.Main) {
                    try {
                        val jsonObject = JSONObject(apiResponse)

                        if (jsonObject.getString("status") == "ok") {
                            val articlesArray = jsonObject.getJSONArray("articles")
                            val newArticles = mutableListOf<Article>()

                            for (i in 0 until articlesArray.length()) {
                                val articleJson = articlesArray.getJSONObject(i)
                                val title = articleJson.optString("title", "Нет заголовка")

                                if (title.contains(query, ignoreCase = true)) {
                                    val article = Article(
                                            sourceName = articleJson.getJSONObject("source").optString("name", "Неизвестный источник"),
                                            author = articleJson.optString("author", "Неизвестен"),
                                            title = title,
                                            description = articleJson.optString("description", "Нет описания"),
                                            url = articleJson.optString("url", ""),
                                            urlToImage = articleJson.optString("urlToImage", ""),
                                            publishedAt = articleJson.optString("publishedAt", "")
                                    )
                                    newArticles.add(article)
                                }
                            }

                            newsArticles.clear()
                            newsArticles.addAll(newArticles)
                            newsAdapter.notifyDataSetChanged()

                            if (newArticles.isEmpty()) {
                                Toast.makeText(this@MainActivity, "Новостей по вашему запросу не найдено.", Toast.LENGTH_LONG).show()
                            }

                        } else {
                            val errorCode = jsonObject.optString("code", "unknown_error")
                            val errorMessage = jsonObject.optString("message", "Произошла ошибка API.")
                            Toast.makeText(this@MainActivity, "Ошибка NewsAPI ($errorCode): $errorMessage", Toast.LENGTH_LONG).show()
                            newsArticles.clear()
                            newsAdapter.notifyDataSetChanged()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Ошибка парсинга данных: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                        newsArticles.clear()
                        newsAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    newsArticles.clear()
                    newsAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
