package com.tcasaniv.wifiscannerepitunsa.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.tcasaniv.wifiscannerepitunsa.R
import com.tcasaniv.wifiscannerepitunsa.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.textHome.text = getString(R.string.about_description_text)
        val myWebView: WebView = binding.webview
        val webSettings: WebSettings = myWebView.getSettings()
//        val newUA = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1"
        // Se puede obtener el User Agent de un navegador abriendo la consola de las herramientas
        // del desarrollador con F12 y pegando el código: window.navigator.appVersion
        val newUA = "5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
        webSettings.userAgentString = newUA
        myWebView.settings.javaScriptEnabled = true
        myWebView.loadUrl("https://docs.google.com/spreadsheets/d/1Yj_6PoBw_QpRyULMrbLvlmvguT3ta5TOH9gZIzB-G4o/edit?usp=sharing")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}