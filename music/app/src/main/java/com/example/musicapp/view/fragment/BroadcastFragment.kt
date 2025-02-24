package com.example.musicapp.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.musicapp.R

class BroadcastFragment : Fragment() {
    private lateinit var airplaneReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_broadcast, container, false)
    }

    override fun onStart() {
        super.onStart()
        airplaneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                    val isEnabled = intent.getBooleanExtra("state", false)
                    Toast.makeText(requireContext(), "Airplane Mode: $isEnabled", Toast.LENGTH_SHORT).show()
                }
            }
        }
        requireContext().registerReceiver(airplaneReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(airplaneReceiver)
    }
}