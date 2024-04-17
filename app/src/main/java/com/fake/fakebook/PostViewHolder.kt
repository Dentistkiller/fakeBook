package com.fake.fakebook

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.collection.LLRBNode.Color
import com.google.firebase.storage.FirebaseStorage

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.picture)
    private val usernameText: TextView = itemView.findViewById(R.id.username)
    private val captionText: TextView = itemView.findViewById(R.id.caption)
    private val likesText: TextView = itemView.findViewById(R.id.likes)
    private val hatesText: TextView = itemView.findViewById(R.id.hates)
    private val likesButton: ImageButton = itemView.findViewById(R.id.likeButton)
    private val hatesButton: ImageButton = itemView.findViewById(R.id.hateButton)


    fun bind(post: Posts) {
        Log.d("FirebaseURI", "URI: ${post.imageUrl}")
        val storageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl(post.imageUrl)

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            // uri is the download URL for the image
            Log.d("FirebaseURI", "URI: $storageReference")
            Glide.with(itemView.context)
                .load(post.imageUrl + ".jpeg")
                .into(imageView)
            usernameText.text = "${post.username} : ${post.caption}"
            likesText.text = "Likes: ${post.likes}"
            hatesText.text = "Hates: ${post.hates}"
        }
        updateLikesDisplay(post)
        updateHatesDisplay(post)

        hatesButton.setOnClickListener {
            post.isHated = !post.isHated
            if (post.isHated) {
                post.HatesCount++ // Increment likes
                hatesButton.setImageResource(R.drawable.vecteezy_broken_heart_icon_on_white_background_12243502) // Assume this is your red heart icon
            } else {
                post.HatesCount-- // Decrement likes
                hatesButton.setImageResource(R.drawable.heart_3510) // Assume this is your white heart icon
            }
            updateLikesDisplay(post)
        }

        likesButton.setOnClickListener {
            post.isLiked = !post.isLiked
            if (post.isLiked) {
                post.likesCount++ // Increment likes
                likesButton.setImageResource(R.drawable.red_heart_11121) // Assume this is your red heart icon
            } else {
                post.likesCount-- // Decrement likes
                likesButton.setImageResource(R.drawable.heart_3510) // Assume this is your white heart icon
            }
            updateLikesDisplay(post)
        }
    }

    private fun updateLikesDisplay(item: Posts) {
        likesText.text = item.likesCount.toString()
        if (item.isLiked) {
            likesButton.setImageResource(R.drawable.red_heart_11121) // Red heart
        } else {
            likesButton.setImageResource(R.drawable.heart_3510) // White heart
        }
    }

    private fun updateHatesDisplay(item: Posts) {
        hatesText.text = item.HatesCount.toString()
        if (item.isHated) {
            hatesButton.setImageResource(R.drawable.vecteezy_broken_heart_icon_on_white_background_12243502) // Red heart
        } else {
            likesButton.setImageResource(R.drawable.heart_3510) // White heart
        }
    }

}