package com.nibm.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nibm.myapplication.R
import com.nibm.myapplication.apiservices.WeatherApiService
import com.nibm.myapplication.repository.WeatherRepository
import com.nibm.myapplication.viewModel.WeatherViewModel
import com.nibm.myapplication.viewModel.WeatherViewModelFactory
import kotlinx.coroutines.launch

class WeatherFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var etCity: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvWeatherCondition: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvCityName: TextView

    private val API_KEY = "" // Replace with your OpenWeatherMap API key

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        etCity = view.findViewById(R.id.etCity)
        btnGetWeather = view.findViewById(R.id.btnGetWeather)
        progressBar = view.findViewById(R.id.progressBar)
        tvWeatherCondition = view.findViewById(R.id.tvWeatherCondition)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvCityName = view.findViewById(R.id.tvCityName)

        // Initialize ViewModel
        val apiService = WeatherApiService.create()
        val repository = WeatherRepository(apiService)
        viewModel = ViewModelProvider(this, WeatherViewModelFactory(repository))
            .get(WeatherViewModel::class.java)

        btnGetWeather.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(city: String) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val weather = viewModel.fetchWeather(city, API_KEY).value
            progressBar.visibility = View.GONE

            if (weather != null) {
                tvCityName.text = "City: $city"
                tvTemperature.text = "Temperature: ${weather.main.temp}°C"
                tvWeatherCondition.text = "Condition: ${weather.weather[0].description}"
            } else {
                Toast.makeText(requireContext(), "Failed to fetch weather", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
