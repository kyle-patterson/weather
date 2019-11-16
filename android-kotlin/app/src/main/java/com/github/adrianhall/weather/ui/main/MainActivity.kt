package com.github.adrianhall.weather.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.github.adrianhall.weather.R
import com.github.adrianhall.weather.models.UserLocation
import com.github.adrianhall.weather.models.UserSettings
import com.github.adrianhall.weather.services.WeatherService
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
 * Main Activity
 *
 * This is the central page for the application.  It has a main job of displaying the list of
 * locations that the user wants to monitor.  However, it also has other jobs, including handling
 * location management, asking for permissions, and triggering navigation requests.
 */
class MainActivity : AppCompatActivity() {
    private val vm by viewModel<MainViewModel>()
    private val weatherService: WeatherService by inject()

    /**
     * Android Activity Lifecycle Event
     *
     * Called when the activity is starting. This is where most initialization should go: calling
     * `setContentView(int)` to inflate the activity's UI, using `findViewById(int)` to
     * interact with widgets in the UI, etc.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        supportActionBar?.apply {
            setTitle(R.string.title_main_activity)
        }

        // Configure the locations list
        val adapter = LocationsAdapter(weatherService).apply {
            setOnClickListener { location, _ -> onLocationRowClicked(location) }
        }
        locations_list.adapter = adapter
        // Watch the list of locations for updates
        vm.favoriteLocations.observe(this, Observer<List<UserLocation>> {
            // TODO: Add current location to the list.
            adapter.setLocationList(it)
        })
        // Watch the user settings for updates so we can re-render the list with new units
        vm.userSettings.observe(this, Observer<UserSettings> {
            adapter.setUserSettings(it)
        })

        // TODO: Implement swipe-to-delete in recycler-view

        // Configure the Add Location floating action button
        add_location_button.setOnClickListener { onAddLocationClicked() }
    }

    /**
     * Android Activity Lifecycle Event
     *
     * Called when the toolbar is being created.  This is where you inflate the menu resources
     * to establish the right-hand options menu.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_options_menu, menu)
        return true
    }

    /**
     * Android Activity Lifecycle Event
     *
     * Called when the user presses a menu item in the options menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean
        = when(item.itemId) {
            R.id.menu_settings -> { startSettingsActivity(); true }
            else               -> super.onOptionsItemSelected(item)
        }

    private fun onAddLocationClicked() {
        Timber.d("CLICK: add-location")
        // TODO: Implement add-location functionality
    }

    private fun onLocationRowClicked(location: UserLocation?) {
        Timber.d("CLICK: location-list-row")
        // TODO: Implement move-to-details functionality
    }

    private fun startSettingsActivity() {
        Timber.d("Starting Settings Activity")
        // TODO: Implement settings activity
    }
}
