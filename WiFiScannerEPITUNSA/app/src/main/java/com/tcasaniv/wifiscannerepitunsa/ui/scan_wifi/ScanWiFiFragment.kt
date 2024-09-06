package com.tcasaniv.wifiscannerepitunsa.ui.scan_wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.tcasaniv.wifiscannerepitunsa.R
import com.tcasaniv.wifiscannerepitunsa.databinding.FragmentScanWifiBinding

class ScanWiFiFragment : Fragment() {

    private var _binding: FragmentScanWifiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanWifiBinding.inflate(inflater, container, false)

        binding.scanButton.setOnClickListener { scanWifi() }
        binding.scanButton.text = getString(R.string.menu_scan_wifi)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun scanWifi() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
        } else {
            showCurrentNetwork()
        }
    }

    @Suppress("DEPRECATION")
    private fun showCurrentNetwork() {
        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo


        if (wifiInfo != null && wifiInfo.networkId != -1) {
            binding.textScanWifi.text = wifiInfo.toString().replace(",", "\n")

            /*binding.wifiTableLayout.removeAllViews()

            val properties = mapOf(
                "SSID" to wifiInfo.ssid,
                "BSSID" to wifiInfo.bssid,
                "IP Address" to intToIp(wifiInfo.ipAddress),
                "Link Speed" to "${wifiInfo.linkSpeed} ${WifiInfo.LINK_SPEED_UNITS}",
                "Frequency" to "${wifiInfo.frequency} ${WifiInfo.FREQUENCY_UNITS}",
                "Signal Strength" to "${wifiInfo.rssi} dBm",
                "MAC Address" to wifiInfo.macAddress,
                "Network ID" to wifiInfo.networkId.toString(),
                "Supplicant State" to wifiInfo.supplicantState.toString(),
                "Hidden SSID" to wifiInfo.hiddenSSID.toString()
            )

            // Add headers
            val headerRow = TableRow(requireContext())
            val headers = listOf("Property", "Value")
            headers.forEach { header ->
                val textView = TextView(requireContext())
                textView.text = header
                headerRow.addView(textView)
            }
            binding.wifiTableLayout.addView(headerRow)

            // Add properties
            properties.forEach { (key, value) ->
                val row = TableRow(requireContext())
                val keyTextView = TextView(requireContext())
                keyTextView.text = key
                val valueTextView = TextView(requireContext())
                valueTextView.text = value
                row.addView(keyTextView)
                row.addView(valueTextView)
                binding.wifiTableLayout.addView(row)
            }*/

            binding.noWifiImage.visibility = View.GONE
        } else {
            binding.textScanWifi.text = getString(R.string.without_wifi)
            binding.wifiTableLayout.removeAllViews()
            binding.noWifiImage.visibility = View.VISIBLE
        }
    }

    /*
    private fun intToIp(int: Int): String {
        return (int and 0xFF).toString() + "." + (int shr 8 and 0xFF).toString() + "." + (int shr 16 and 0xFF).toString() + "." + (int shr 24 and 0xFF).toString()
    }
    */
}
