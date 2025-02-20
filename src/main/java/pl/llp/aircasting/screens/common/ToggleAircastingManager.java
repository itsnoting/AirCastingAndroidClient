package pl.llp.aircasting.screens.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import pl.llp.aircasting.R;
import pl.llp.aircasting.model.Session;
import pl.llp.aircasting.screens.common.helpers.LocationHelper;
import pl.llp.aircasting.screens.common.helpers.SettingsHelper;
import pl.llp.aircasting.screens.common.sessionState.CurrentSessionManager;
import pl.llp.aircasting.screens.common.sessionState.CurrentSessionSensorManager;
import pl.llp.aircasting.screens.dashboard.DashboardChartManager;
import pl.llp.aircasting.screens.sessionRecord.StartMobileSessionActivity;

import static pl.llp.aircasting.util.Constants.LOCATION_PERMISSION;
import static pl.llp.aircasting.util.Constants.PERMISSIONS_REQUEST_FINE_LOCATION;

/**
 * Created by radek on 21/06/17.
 */
public class ToggleAircastingManager {
    public AppCompatDelegate delegate;
    private Context context;
    private Activity activity;
    private CurrentSessionManager currentSessionManager;
    private SettingsHelper settingsHelper;
    private LocationHelper locationHelper;
    private DashboardChartManager dashboardChartManager;

    public ToggleAircastingManager(Activity activity,
                                   CurrentSessionManager currentSessionManager,
                                   SettingsHelper settingsHelper,
                                   CurrentSessionSensorManager currentSessionSensorManager,
                                   LocationHelper locationHelper,
                                   AppCompatDelegate delegate,
                                   Context context,
                                   DashboardChartManager dashboardChartManager) {
        this.activity = activity;
        this.currentSessionManager = currentSessionManager;
        this.settingsHelper = settingsHelper;
        this.locationHelper = locationHelper;
        this.delegate = delegate;
        this.context = context;
        this.dashboardChartManager = dashboardChartManager;
    }

    public void toggleAirCasting() {
        if (currentSessionManager.isSessionRecording()) {
            stopAirCasting();
        } else {
            startMobileAirCasting();
        }
    }

    public void stopAirCasting() {
        Session session = currentSessionManager.getCurrentSession();
        dashboardChartManager.stop();
        locationHelper.stopLocationUpdates();

        stopMobileAirCasting(session);
    }

    private void stopMobileAirCasting(Session session) {
        Long sessionId = session.getId();
        if (session.isEmpty()) {
            ToastHelper.show(context, R.string.no_data, Toast.LENGTH_SHORT);
            currentSessionManager.discardSession(sessionId);
        } else {
            currentSessionManager.stopSession();

            if (session.isLocationless()) {
                currentSessionManager.finishSession(sessionId, false);
            } else if (settingsHelper.isContributingToCrowdMap()) {
                currentSessionManager.finishSession(sessionId, true);
            }
        }
    }

    public void startMobileAirCasting() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, LOCATION_PERMISSION, PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }

        dashboardChartManager.start();

        activity.startActivity(new Intent(activity, StartMobileSessionActivity.class));
    }
}
