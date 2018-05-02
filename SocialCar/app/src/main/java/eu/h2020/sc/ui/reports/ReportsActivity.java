package eu.h2020.sc.ui.reports;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.location.PositionManager;
import eu.h2020.sc.location.PositionManagerCallback;
import eu.h2020.sc.ui.reports.adapter.ReportAdapter;
import eu.h2020.sc.ui.reports.task.FindReportAroundTask;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.Utils;

import static eu.h2020.sc.SocialCarApplication.getContext;

public class ReportsActivity extends GeneralActivity implements PositionManagerCallback, ResultCallback<LocationSettingsResult> {

    private PositionManager positionManager;
    public static final int ID_REQ_POSITION_MY = 1001;
    private final int ID_REQUEST_PERMISSION = 1002;
    private final int REQUEST_CODE_ASK_LOCATION = 1003;

    private RecyclerView recyclerViewReports;
    private TextViewCustom textViewCustomErrorMessage;
    private Button buttonRetry;
    private ReportAdapter reportAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isAfterKitKatVersion())
            setTheme(R.style.Theme_SocialCar_Status_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        this.initUI();
        this.initPositionManager();
        initToolBar(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDrawer(getString(R.string.reports));
    }


    public void buttonRetry(View view) {
        retryAction();
    }

    private void initPositionManager() {
        this.showProgressDialog();
        this.positionManager = new PositionManager(this, ID_REQ_POSITION_MY, this);
        this.positionManager.setResultCallBackLocationSettings(this);
    }

    private void initUI() {
        this.recyclerViewReports = (RecyclerView) findViewById(R.id.recycler_view_reports);
        this.textViewCustomErrorMessage = (TextViewCustom) findViewById(R.id.activity_reports_error_message);
        this.buttonRetry = (Button) findViewById(R.id.activity_reports_button_retry);
        this.recyclerViewReports.setHasFixedSize(true);
        this.reportAdapter = new ReportAdapter(new ArrayList<Report>());
        this.recyclerViewReports.setAdapter(this.reportAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        this.recyclerViewReports.setLayoutManager(mLayoutManager);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage(getString(R.string.message_progress_dialog));
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ID_REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.positionManager.getLastPosition();
            } else {
                this.setErrorLayout(getString(R.string.error_enable_permission_app));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            this.retryAction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPosition(Location location, int requestID) {
        FindReportAroundTask findReportAroundTask = new FindReportAroundTask(this);
        findReportAroundTask.execute(new Point(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void networkError() {
        this.showConnectionError();
    }

    @Override
    public void genericError(int errorType) {
        this.showServerGenericError();
    }

    @Override
    public void locationServiceDisable() {
        this.positionManager.promptActiveLocation();
    }

    @Override
    public void permissionDisabled(String permissionType) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ID_REQUEST_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ASK_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    this.retryAction();
                    break;
                case Activity.RESULT_CANCELED:
                    setErrorLayout(getString(R.string.turn_on_gps_message));
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

    public void showLayoutReportsList(List<Report> reports) {
        this.recyclerViewReports.setVisibility(View.VISIBLE);
        this.buttonRetry.setVisibility(View.GONE);
        this.textViewCustomErrorMessage.setVisibility(View.GONE);
        this.reportAdapter.refreshRecyclerView(reports);
    }

    public void showLayoutEmptyReports() {
        this.setErrorLayout(getString(R.string.no_reports_found));
    }

    @Override
    public void showConnectionError() {
        this.setErrorLayout(getString(R.string.no_connection_error));
    }

    @Override
    public void showServerGenericError() {
        this.setErrorLayout(getString(R.string.generic_error));
    }

    public void showNotFoundError() {
        this.setErrorLayout(getString(R.string.no_reports_found));
    }

    private void setErrorLayout(String message) {
        dismissDialog();
        this.recyclerViewReports.setVisibility(View.GONE);
        this.textViewCustomErrorMessage.setVisibility(View.VISIBLE);
        this.buttonRetry.setVisibility(View.VISIBLE);
        this.textViewCustomErrorMessage.setText(message);
    }

    private void retryAction() {
        showProgressDialog();
        this.positionManager.getLastPosition();
        this.buttonRetry.setVisibility(View.GONE);
        this.textViewCustomErrorMessage.setVisibility(View.GONE);
    }

    @Override
    public void showProgressDialog() {
        this.progressDialog.show();
    }

    @Override
    public void dismissDialog() {
        if (this.progressDialog.isShowing())
            this.progressDialog.dismiss();
    }
}
