package com.example.finalproject_fittrack.ui.leaderboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject_fittrack.adapter.LeaderboardAdapter
import com.example.finalproject_fittrack.dataBase.FirebaseRepository
import com.example.finalproject_fittrack.dataBase.LeaderboardRepository
import com.example.finalproject_fittrack.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private var leaderboardList: MutableList<Pair<String, Int>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadLeaderboard()

        return binding.root
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter(leaderboardList)

        binding.leaderboardRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaderboardAdapter
            setHasFixedSize(true)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadLeaderboard() {
        LeaderboardRepository.getLeaderboard { leaderboard ->
            leaderboardList.clear()
            leaderboardList.addAll(leaderboard)
            leaderboardAdapter.notifyDataSetChanged()

            val currentUserName = FirebaseRepository.getCurrentUserName()

            val userRank = leaderboardList.indexOfFirst { it.first == currentUserName } + 1

            if (userRank > 0) {
                "You are currently ranked #$userRank".also {
                    binding.leaderboardLBLRankPlaceholder.text = it
                }
            } else {
                "You are not ranked yet".also { binding.leaderboardLBLRankPlaceholder.text = it }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
