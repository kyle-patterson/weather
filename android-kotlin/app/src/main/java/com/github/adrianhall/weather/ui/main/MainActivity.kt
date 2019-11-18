package com.github.adrianhall.weather.ui.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import com.github.adrianhall.weather.R
import com.github.adrianhall.weather.models.UserLocation
import com.github.adrianhall.weather.models.UserSettings
import com.github.adrianhall.weather.services.WeatherService
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
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
class MainActivity : AppCompatActivity(), PermissionListener {
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

        // Ask for permissions to use location
        Dexter
            .withActivity(this)
            .withPermission(Manifest.permission_group.LOCATION)
            .withListener(this)
            .check()

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

    /**
     * Method called whenever a requested permission has been granted
     *
     * @param response A response object that contains the permission that has been requested and
     * any additional flags relevant to this response
     */
    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        current_location_fragment.visibility = View.VISIBLE
        supportFragmentManager.run {
            if (fragments.count() > 0) {
                beginTransaction().replace(R.id.current_location_fragment, CurrentLocationFragment()).commit()
            } else {
                beginTransaction().add(R.id.current_location_fragment, CurrentLocationFragment()).commit()
            }
        }

        // Connect the recycler view top to the fragment instead of the app bar
        val constraints = ConstraintSet()
        constraints.clone(main_layout)
        constraints.connect(R.id.locations_list, ConstraintSet.TOP, R.id.current_location_fragment, ConstraintSet.BOTTOM, 0)
        constraints.applyTo(main_layout)
    }

    /**
     * Method called whenever Android asks the application to inform the user of the need for the
     * requested permission. The request process won't continue until the token is properly used
     *
     * @param permission The permission that has been requested
     * @param token Token used to continue or cancel the permission request process. The permission
     * request process will remain blocked until one of the token methods is called
     */
    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
        // TODO: Implement show-rationale for permissions properly
        token?.continuePermissionRequest()
    }

    /**
     * Method called whenever a requested permission has been denied
     *
     * @param response A response object that contains the permission that has been requested and
     * any additional flags relevant to this response
     */
    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        // Connect the recycler view top to the app bar instead of the fragment
        val constraints = ConstraintSet()
        constraints.clone(main_layout)
        constraints.connect(R.id.locations_list, ConstraintSet.TOP, R.id.main_app_bar, ConstraintSet.BOTTOM, 0)
        constraints.applyTo(main_layout)
        // Make the fragment for the current location invisible
        current_location_fragment.visibility = View.INVISIBLE
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
