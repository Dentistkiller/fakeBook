package com.fake.fakebook

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    private lateinit var postsRecyclerView: RecyclerView
    private val posts = mutableListOf<Posts>() // An empty list initially
    private lateinit var adapter: PostAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var tvName : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val userName = intent.getStringExtra("userName")
        postsRecyclerView = findViewById(R.id.postRecyclerView) // Replace with your RecyclerView ID
        adapter = PostAdapter(posts)
        postsRecyclerView.adapter = adapter
        bottomNav = findViewById(R.id.bottomNav)
        tvName = findViewById(R.id.tvname)
        val layoutManager = LinearLayoutManager(this) // Vertical
        postsRecyclerView.layoutManager = layoutManager

        tvName.setText(userName)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.upload -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {false}
            }
        }
        fetchPostsFromFirestore()
    }
    private fun fetchPostsFromFirestore() {
        db.collection("posts") // Replace with your collection name
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val post = document.toObject(Posts::class.java)!! // Assuming data matches Post class
                    posts.add(post)
                }
                adapter.notifyDataSetChanged() // Update RecyclerView adapter
            }
            .addOnFailureListener { exception ->
                // Handle fetching posts failure
            }
    }
}