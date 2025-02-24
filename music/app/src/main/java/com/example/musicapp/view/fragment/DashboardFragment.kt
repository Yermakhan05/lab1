package com.example.musicapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.model.service.MusicPlayerService
import com.example.musicapp.databinding.FragmentDashboardBinding
import com.example.musicapp.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val startIntent = Intent(requireContext(), MusicPlayerService::class.java).setAction("START")
        val pauseIntent = Intent(requireContext(), MusicPlayerService::class.java).setAction("PAUSE")
        val stopIntent = Intent(requireContext(), MusicPlayerService::class.java).setAction("STOP")

        binding.btnStart.setOnClickListener { requireContext().startService(startIntent) }
        binding.btnPause.setOnClickListener { requireContext().startService(pauseIntent) }
        binding.btnStop.setOnClickListener { requireContext().startService(stopIntent) }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}