package eu.h2020.sc.ui.trip.recording;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.osmdroid.bonuspack.routing.Road;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.location.PositionTrackingCallback;
import eu.h2020.sc.location.PositionTrackingManager;
import eu.h2020.sc.ui.trip.recording.task.RecordedTripTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.WidgetHelper;

public class TripRecordingActivity extends GeneralActivity implements MaterialDialog.SingleButtonCallback, PositionTrackingCallback, ResultCallback<LocationSettingsResult> {

    private final int ID_REQUEST_PERMISSION = 1000;
    private final int REQUEST_CODE_ASK_LOCATION = 1001;

    private TextView textView;
    private ImageButton btnPlayRecording;
    private ImageButton btnStopRecording;
    private TextView twRecordingMessage;
    private MaterialDialog dialogSaveTrip;
    private FrameLayout frameLayoutPause;
    private PositionTrackingManager positionTrackingManager;
    private CoordinatorLayout coordinatorLayout;

    private ImageView imageViewCity;
    private ImageView imageViewCitySecond;
    private ImageView imageViewCar;
    private ImageView imageViewClouds;
    private ImageView imageViewCloudsSecond;

    private Animation moveCity;
    private Animation moveCitySecond;
    private Animation moveCar;
    private Animation resumeCarMovement;
    private Animation moveClouds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_recording);

        this.initUI();
        this.positionTrackingManager = new PositionTrackingManager(this, this, this, ID_REQUEST_PERMISSION);
    }


    private void initUI() {
        setTitle(getString(R.string.live_recorded_trip));

        initToolBar(false);
        initBack();

        //TODO Remove this TextView because is only for test
        this.textView = (TextView) findViewById(R.id.testo_prova);

        this.btnPlayRecording = (ImageButton) findViewById(R.id.imageButton_play);
        this.twRecordingMessage = (TextView) findViewById(R.id.textView_messages_recording);
        this.frameLayoutPause = (FrameLayout) findViewById(R.id.act_trip_rec_frame_layout_pause);
        this.btnStopRecording = (ImageButton) findViewById(R.id.btn_stop_recording);
        this.coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        this.dialogSaveTrip = MaterialDialogHelper.createCustomViewDialog(this, R.string.dialog_save_trip, R.layout.custom_dialog_save_trip);
        this.dialogSaveTrip.setCancelable(false);

        this.imageViewCity = (ImageView) findViewById(R.id.imageView_city);
        this.imageViewCitySecond = (ImageView) findViewById(R.id.imageView_city_second);
        this.imageViewCar = (ImageView) findViewById(R.id.imageView_car);
        this.imageViewClouds = (ImageView) findViewById(R.id.imageView_clouds);
        this.imageViewCloudsSecond = (ImageView) findViewById(R.id.imageView_clouds_second);
        ImageView imageViewPauseGif = (ImageView) findViewById(R.id.pauseGif);

        this.moveCity = AnimationUtils.loadAnimation(this, R.anim.city_move);
        this.moveCitySecond = AnimationUtils.loadAnimation(this, R.anim.city_move_second);
        this.moveCar = AnimationUtils.loadAnimation(this, R.anim.car_move);
        this.resumeCarMovement = AnimationUtils.loadAnimation(this, R.anim.car_resume_move);
        this.moveClouds = AnimationUtils.loadAnimation(this, R.anim.clouds_move);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.pause_animation);

        imageViewPauseGif.setAnimation(anim);
    }

    public void actionPlayRecording(View view) {
        this.positionTrackingManager.startTracking();
    }

    private void showLayoutPlay() {
        this.btnPlayRecording.setVisibility(View.GONE);
        this.btnStopRecording.setVisibility(View.VISIBLE);
        this.frameLayoutPause.setVisibility(View.VISIBLE);
        this.twRecordingMessage.setText(getString(R.string.currently_recording));

        this.imageViewCity.startAnimation(moveCity);
        this.imageViewCitySecond.startAnimation(moveCitySecond);
        this.imageViewCloudsSecond.startAnimation(moveClouds);
        this.imageViewClouds.startAnimation(moveClouds);
        this.imageViewCar.startAnimation(moveCar);
    }


    public void actionPauseRecording(View view) {
        this.positionTrackingManager.stopTracking();
        this.twRecordingMessage.setText(getString(R.string.resume_recording_label));
        this.btnPlayRecording.setVisibility(View.VISIBLE);
        this.frameLayoutPause.setVisibility(View.GONE);

        this.imageViewCity.clearAnimation();
        this.imageViewCitySecond.clearAnimation();
        this.imageViewCloudsSecond.clearAnimation();
        this.imageViewClouds.clearAnimation();
        this.imageViewCar.clearAnimation();
        this.imageViewCar.startAnimation(resumeCarMovement);
    }

    public void actionStopRecording(View view) {
        this.positionTrackingManager.stopTracking();

        this.twRecordingMessage.setText(getString(R.string.start_recording_label));
        this.btnPlayRecording.setVisibility(View.VISIBLE);

        this.imageViewCity.clearAnimation();
        this.imageViewCitySecond.clearAnimation();
        this.imageViewCloudsSecond.clearAnimation();
        this.imageViewClouds.clearAnimation();

        if (this.btnPlayRecording.getVisibility() == View.VISIBLE) {
            this.imageViewCar.clearAnimation();
        }

        if (this.frameLayoutPause.getVisibility() == View.VISIBLE) {
            this.frameLayoutPause.setVisibility(View.GONE);
            this.imageViewCar.startAnimation(resumeCarMovement);
            this.dialogSaveTrip.show();
            return;
        }

        this.dialogSaveTrip.show();
    }

    public void actionSaveRecordedTrip(View view) {
        this.dialogSaveTrip.dismiss();
        showProgressDialog();
        RecordedTripTask recordedTripTask = new RecordedTripTask(this, this.positionTrackingManager.getStoredTrip());
        recordedTripTask.execute();
    }

    public void actionResumeRecordedTrip(View view) {
        this.positionTrackingManager.startTracking();
        this.dialogSaveTrip.dismiss();
        this.frameLayoutPause.setVisibility(View.VISIBLE);
        this.btnPlayRecording.setVisibility(View.GONE);
        this.twRecordingMessage.setText(getString(R.string.currently_recording));

        this.imageViewCity.startAnimation(moveCity);
        this.imageViewCitySecond.startAnimation(moveCitySecond);
        this.imageViewCloudsSecond.startAnimation(moveClouds);
        this.imageViewClouds.startAnimation(moveClouds);
        this.imageViewCar.startAnimation(moveCar);
    }

    public void actionDeleteRecordedTrip(View view) {
        WidgetHelper.showToast(this, R.string.delete_recorded_trip_message);
        this.positionTrackingManager.deleteStoredTrip();
        this.btnStopRecording.setVisibility(View.GONE);
        this.btnPlayRecording.setVisibility(View.VISIBLE);
        this.frameLayoutPause.setVisibility(View.GONE);
        this.twRecordingMessage.setText(getString(R.string.start_recording_label));
        this.dialogSaveTrip.dismiss();

        this.imageViewCity.clearAnimation();
        this.imageViewCitySecond.clearAnimation();
        this.imageViewCloudsSecond.clearAnimation();
        this.imageViewClouds.clearAnimation();
        this.imageViewCar.clearAnimation();
    }

    @Override
    public void onResume() {
        if (this.positionTrackingManager.isRunning()) {
            this.positionTrackingManager.startTracking();
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    ID_REQUEST_PERMISSION);
        }

        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ID_REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.positionTrackingManager.startTracking();
                this.showLayoutPlay();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    WidgetHelper.showSnackbar(this, this.coordinatorLayout, getString(R.string.error_enable_permission_app), getString(R.string.enable), this.snackBarButtonListener, this.snackBarCallback);
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ASK_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    this.positionTrackingManager.startTracking();
                    this.showLayoutPlay();
                    break;
                case Activity.RESULT_CANCELED:
                    WidgetHelper.showToast(this, R.string.turn_on_gps_message_trip_recording);
                    break;
            }
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(this, REQUEST_CODE_ASK_LOCATION);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(getTagFromActivity(this), "Error during startResolutionForResult: " + e);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.positionTrackingManager.isRunning()) {
            MaterialDialogHelper.createDialog(this, R.string.title_dialog_back_trip_recording, R.string.message_dialog_back_trip_recording, R.string.ok, R.string.cancel,
                    this).show();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        this.finish();
    }

    @Override
    public void onStarted() {
        this.showLayoutPlay();
    }

    @Override
    public void onStopped() {
        Log.i(getTagFromActivity(this), "Stop recording");
    }

    @Override
    public void networkError() {
        this.showConnectionError();
    }

    @Override
    public void genericError(int errorType) {
        this.showServerGenericError();
    }

    public void goToTripRecordedMapActivity(Road road) {
        this.btnStopRecording.setVisibility(View.GONE);
        ActivityUtils.openActivityWithObjectParam(this, TripRecordedMapActivity.class, TripRecordedMapActivity.RECORDED_TRIP, road);
    }

    public void deleteStoredTrip() {
        this.positionTrackingManager.deleteStoredTrip();
    }

    private View.OnClickListener snackBarButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityUtils.openApplicationDetailsSettings(TripRecordingActivity.this);
        }
    };

    private Snackbar.Callback snackBarCallback = new Snackbar.Callback() {
        @Override
        public void onDismissed(Snackbar snackbar, int event) {
        }
    };

    public void showErrorRecordedTripTooShort() {
        WidgetHelper.showToast(this, R.string.recorded_trip_too_short);
    }
}