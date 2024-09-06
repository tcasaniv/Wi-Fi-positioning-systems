package com.tcasaniv.wifiscannerepitunsa.ui.about

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.tcasaniv.wifiscannerepitunsa.R
import com.tcasaniv.wifiscannerepitunsa.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (minVersionR()) {
            if(wifiManager.isScanThrottleEnabled){
                binding.aboutWifiThrottlingOn.visibility = View.VISIBLE
                binding.aboutWifiThrottlingOff.visibility = View.GONE
            } else {
                binding.aboutWifiThrottlingOff.visibility = View.VISIBLE
                binding.aboutWifiThrottlingOn.visibility = View.GONE
            }
        } else {
            false
        }

//        binding.scanButton.setOnClickListener { About() }
//        binding.scanButton.text = getString(R.string.menu_scan_wifi)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun minVersionR(): Boolean = buildMinVersionR()
    private fun buildMinVersionR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

}
