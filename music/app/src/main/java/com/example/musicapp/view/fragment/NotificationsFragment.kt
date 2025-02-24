package com.example.musicapp.view.fragment

import android.Manifest
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.databinding.FragmentNotificationsBinding
import java.util.Date
import java.util.Locale
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.model.entity.Event
import com.example.musicapp.view.adapter.EventAdapter
import com.example.musicapp.viewmodel.NotificationsViewModel
import java.text.SimpleDateFormat

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
        } else {
            recyclerView = binding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            val events = getCalendarEvents(requireContext())
            adapter = EventAdapter(events)
            recyclerView.adapter = adapter
        }

        return root
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                recyclerView = binding.recyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                val events = getCalendarEvents(requireContext())
                adapter = EventAdapter(events)
                recyclerView.adapter = adapter
                Toast.makeText(requireContext(), "Permission", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getCalendarEvents(context: Context): List<Event> {
        val events = mutableListOf<Event>()
        val projection = arrayOf(CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART)

        val cursor: Cursor? = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            null, null,
            "${CalendarContract.Events.DTSTART} ASC"
        )

        cursor?.use {
            val titleIndex = it.getColumnIndex(CalendarContract.Events.TITLE)
            val dateIndex = it.getColumnIndex(CalendarContract.Events.DTSTART)

            while (it.moveToNext()) {
                val title = it.getString(titleIndex)
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(it.getLong(dateIndex)))
                events.add(Event(title, date))
            }
        }
        return events
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}