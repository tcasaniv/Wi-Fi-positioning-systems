package com.tcasaniv.wifiscannerepitunsa.ui.scan_wifi_networks

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.tcasaniv.wifiscannerepitunsa.MainActivity
import com.tcasaniv.wifiscannerepitunsa.R
import com.tcasaniv.wifiscannerepitunsa.databinding.FragmentScanWifiNetworksBinding
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class ScanWiFiNetworksFragment : Fragment() {

    private var _binding: FragmentScanWifiNetworksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiScanReceiver: BroadcastReceiver

    private var jsonData = ""
    private var tempData: List<WifiScanResultWithExtras> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanWifiNetworksBinding.inflate(inflater, container, false)

        binding.scanButton.setOnClickListener { scanWifiNetworks() }
        binding.scanButton.text = getString(R.string.menu_scan_wifi_networks)

        binding.sendButton.setOnClickListener { sendDataToURL() }

        wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        // BroadcastReceiver register to hear the end of scanning
        wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                showScanResults()
            }
        }
        requireContext().registerReceiver(
            wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        // BroadcastReceiver unregister to avoid memory leaks
        requireContext().unregisterReceiver(wifiScanReceiver)
    }

    private fun sendDataToURL() {
        val mainActivity = requireActivity() as MainActivity

        thread {
            try {
                val url = URL(mainActivity.url)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.doOutput = true

                // Send the data
                connection.outputStream.use { os ->
                    val input = jsonData.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                // Read the answer
                val responseCode = connection.responseCode
                Log.d("sendDataToURL", "Response Code: $responseCode")
                mainActivity.runOnUiThread {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(mainActivity, getString(R.string.successfully_submitted), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(mainActivity, getString(R.string.error_sending), Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                mainActivity.runOnUiThread {
                    Toast.makeText(mainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun scanWifiNetworks() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
        } else {
            @Suppress("DEPRECATION") val success = wifiManager.startScan()
            if (!success) {
                Toast.makeText(
                    requireContext(), getString(R.string.scan_failed), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addTableHeader() {
        binding.wifiNetworksTableLayout.addView(
            createTableRow(
                getString(R.string.header_ssid),
                getString(R.string.header_bssid),
                getString(R.string.header_signal_strength),
                getString(R.string.header_frequency),
                getString(R.string.header_capabilities)
            )
        )
    }

    private fun timestamp(date: Date): String =
        SimpleDateFormat(TIME_STAMP_FORMAT, Locale.US).format(date)

    data class WifiScanResultWithExtras(
        var ssid: String,
        var bssid: String,
        var level: String,
        var frequency: String,
        var capabilities: String,
        var timestamp: String,
        var celdaX: String,
        var celdaY: String,
        var celdaZ: String
    )


    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun showScanResults() {
        binding.textScanWifiNetworks.text = timestamp(Date())
        binding.wifiNetworksTableLayout.removeAllViews()
        addTableHeader()


        val wifiResultsWithExtras = wifiManager.scanResults.map { result ->
            WifiScanResultWithExtras(
                ssid = result.SSID,
                bssid = result.BSSID,
                level = "${result.level} dBm",
                frequency = "${result.frequency} MHz",
                capabilities = result.capabilities,
                timestamp = timestamp(Date()),
                celdaX = binding.editTextCeldaX.text.toString(),
                celdaY = binding.editTextCeldaY.text.toString(),
                celdaZ = binding.editTextCeldaZ.text.toString()
            )
        }

        tempData = wifiResultsWithExtras

        // Mostrar resultados en la tabla
        wifiResultsWithExtras.forEach { result ->
            binding.wifiNetworksTableLayout.addView(
                createTableRow(
                    result.ssid,
                    result.bssid,
                    result.level,
                    result.frequency,
                    result.capabilities
                )
            )
        }



//        wifiManager.scanResults.forEach { result ->
//            binding.wifiNetworksTableLayout.addView(
//                createTableRow(
//                    result.SSID,
//                    result.BSSID,
//                    "${result.level} dBm",
//                    "${result.frequency} MHz",
//                    result.capabilities
//                )
//            )
//        }

        jsonData = Gson().toJson(tempData)  // Convert results to JSON

        binding.sendButton.visibility = View.VISIBLE
    }


    private fun createTableRow(
        ssid: String, bssid: String, level: String, frequency: String, capabilities: String
    ): TableRow {
        fun createTextView(text: String): TextView {
            return TextView(requireContext()).apply {
                this.text = text
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }
        }

        return TableRow(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(createTextView(ssid))
            addView(createTextView(bssid))
            addView(createTextView(level))
            addView(createTextView(frequency))
            addView(createTextView(capabilities))
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.scanWifiNetworks()
        } else {
            // Location permit refused
            Toast.makeText(
                requireContext(), getString(R.string.location_permit_refused), Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val TIME_STAMP_FORMAT = "yyyy/MM/dd-HH:mm:ss"
    }
}