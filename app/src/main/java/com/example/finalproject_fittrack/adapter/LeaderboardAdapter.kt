package com.example.finalproject_fittrack.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject_fittrack.databinding.ItemLeaderboardBinding

class LeaderboardAdapter(
    private val leaderboard: List<Pair<String, Int>>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding =
            ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val (name, caloriesBurned) = leaderboard[position]
        holder.bind(name, caloriesBurned, position + 1)
    }

    override fun getItemCount(): Int = leaderboard.size

    class LeaderboardViewHolder(private val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, caloriesBurned: Int, rank: Int) {
            "$rank.".also { binding.leaderboardLBLRank.text = it }
            binding.leaderboardLBLUser.text = name
            "$caloriesBurned kcal".also { binding.leaderboardLBLCalories.text = it }
        }
    }
}
