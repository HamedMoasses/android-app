package eu.h2020.sc.ui.home.driver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.socialnetwork.SocialNetwork;
import eu.h2020.sc.socialnetwork.facebook.FacebookManager;
import eu.h2020.sc.socialnetwork.googleplus.GooglePlusManager;
import eu.h2020.sc.ui.feedback.FeedbackAdapter;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.MaterialRippleLayout;
import eu.h2020.sc.utils.PhoneUtils;
import eu.h2020.sc.utils.PicassoHelper;

import java.util.List;


public class DriverDetailsActivity extends GeneralActivity implements View.OnClickListener {

    public static final String DRIVER_DETAILS_KEY = "DRIVER_DETAILS_KEY";
    public static final String CAR_DETAILS_KEY = "CAR_DETAILS_KEY";

    private static final int REQUEST_CODE_CALL_PHONE_PERMISSIONS = 100;
    private static final int REQUEST_CODE_SEND_SMS_PERMISSIONS = 101;

    private FrameLayout frameLayoutDriverDetails;
    private FrameLayout frameLayoutCarInfo;
    private TextView textViewRating;
    private TextView textViewDriverName;
    private ImageView imageViewDriver;
    private ScrollView scrollViewDetails;
    private RelativeLayout relativeLayoutDetails;
    private LinearLayout linearLayoutUserActions;
    private TextView textViewCarPlate;
    private TextView textViewCarModel;
    private TextView textViewCarSeat;
    private TextView textViewColor;
    private View carColor;
    private View noNoDataLayout;
    private Button btnNoDataRetry;
    private TextView noDataTextView;
    private ImageView imageViewCarDriver;
    private MaterialRippleLayout rippleLayoutSocialProfile;
    private ImageView imageViewSocialIcon;
    private Car car;
    private User driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        this.car = (Car) ActivityUtils.getSerializableFromIntent(this, CAR_DETAILS_KEY);
        this.driver = (User) ActivityUtils.getSerializableFromIntent(this, DRIVER_DETAILS_KEY);

        this.initUI();
        this.initEvents();
        this.executeRequests();
    }

    private void initUI() {
        setTitle(String.format("%s %s", getString(R.string.driver), getString((R.string.solutions_details_toolbar_label))));

        this.scrollViewDetails = (ScrollView) findViewById(R.id.scrollViewDriverDetails);
        this.relativeLayoutDetails = (RelativeLayout) findViewById(R.id.relativeLayoutHeaderView);
        this.imageViewDriver = (ImageView) findViewById(R.id.imageView_avatar);
        this.textViewRating = (TextView) findViewById(R.id.textView_driver_rating);
        this.textViewDriverName = (TextView) findViewById(R.id.textView_driver_name);
        this.frameLayoutDriverDetails = (FrameLayout) findViewById(R.id.frame_layout_driver_details);
        this.textViewColor = (TextView) findViewById(R.id.textView_color);
        this.carColor = findViewById(R.id.view_car_color);
        this.textViewCarModel = (TextView) findViewById(R.id.textView_car_model);
        this.textViewCarPlate = (TextView) findViewById(R.id.textView_car_plate);
        this.textViewCarSeat = (TextView) findViewById(R.id.textView_car_seat);
        this.frameLayoutCarInfo = (FrameLayout) findViewById(R.id.frame_layout_car);
        this.imageViewCarDriver = (ImageView) findViewById(R.id.driver_details_image_view_car);

        this.noNoDataLayout = this.findViewById(R.id.layout_driver_details_error_retry);
        this.btnNoDataRetry = (Button) this.noNoDataLayout.findViewById(R.id.btn_no_data_retry);
        this.noDataTextView = (TextView) this.noNoDataLayout.findViewById(R.id.no_data_text_view);

        this.linearLayoutUserActions = (LinearLayout) findViewById(R.id.layout_user_actions);
        this.imageViewSocialIcon = (ImageView) findViewById(R.id.image_view_social_icon);
        this.rippleLayoutSocialProfile = (MaterialRippleLayout) findViewById(R.id.social_profile);

        this.showDriverDetails();
        this.showSocialNetworkAction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initEvents() {
        this.btnNoDataRetry.setOnClickListener(this);
    }

    private void showDriverDetails() {
        this.textViewDriverName.setText(driver.getName());
        this.textViewColor.append(": ");
        this.carColor.setBackgroundColor(Color.parseColor(car.getColour()));
        this.textViewCarModel.setText(car.getModel());
        this.textViewCarPlate.append(String.format(": %s", car.getPlate()));
        this.textViewCarSeat.append(String.format(": %s", car.getSeats()));
    }

    public void showDriverFeedback(List<Feedback> feedbackList) {

        this.textViewRating.setText(String.format("%s (%s feedback)", this.driver.getRating(), feedbackList.size()));

        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(DriverDetailsActivity.this, (LinearLayout) findViewById(R.id.linear_layout_feedback));
        feedbackAdapter.createFeedbackView(feedbackList);
    }

    private void executeRequests() {
        showProgressDialog();
        this.executeFeedbackRequest();
        this.executePictureRequest();
        this.executeCarPictureRequest();
    }

    private void executeFeedbackRequest() {
        DriverFeedBackTask driverFeedBackTask = new DriverFeedBackTask(this);
        driverFeedBackTask.execute(this.driver);
    }

    private void executePictureRequest() {

        if (this.driver.getUserPictures() != null && !this.driver.getUserPictures().isEmpty()) {
            PicassoHelper.loadMedia(this, this.imageViewDriver, this.driver.getUserPictures().get(0).getMediaUri(), this.driver.obtainAvatarDefaultResource(), null, true);
        } else {
            this.imageViewDriver.setImageResource(R.drawable.img_default_feedback_avatar);
        }
    }

    private void executeCarPictureRequest() {

        if (this.car.getCarPictures() != null && !this.car.getCarPictures().isEmpty()) {
            PicassoHelper.loadMedia(this, this.imageViewCarDriver, this.car.getCarPictures().get(0).getMediaUri(), R.mipmap.img_default_car, null, false);
        } else {
            this.imageViewCarDriver.setImageResource(R.mipmap.img_default_car);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no_data_retry:
                retry(v);
                break;
        }
    }

    public void retry(View v) {
        this.frameLayoutDriverDetails.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        this.noNoDataLayout.setVisibility(View.GONE);

        this.executeRequests();
    }

    public void showSocialProfile(View view) {
        if (this.driver.getSocialProvider() != null && this.driver.getSocialProvider().getSocialNetwork().equals(SocialNetwork.FACEBOOK)) {
            startActivity(FacebookManager.createIntentFacebookProfile(this, this.driver.getSocialProvider().getSocialID()));
        } else {
            startActivity(GooglePlusManager.createIntentToGooglePlusProfile(this, this.driver.getSocialProvider().getSocialID()));
        }

    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void showActivityLayout() {
        this.imageViewCarDriver.setVisibility(View.VISIBLE);
        this.frameLayoutCarInfo.setVisibility(View.VISIBLE);

        this.relativeLayoutDetails.setVisibility(View.VISIBLE);
        this.scrollViewDetails.setVisibility(View.VISIBLE);
        this.linearLayoutUserActions.setVisibility(View.VISIBLE);
    }

    private void showSocialNetworkAction() {
        if (this.driver.getSocialProvider() != null && this.driver.getSocialProvider().getSocialNetwork().equals(SocialNetwork.FACEBOOK)) {
            this.imageViewSocialIcon.setImageResource(R.drawable.ic_facebook);

        } else if (this.driver.getSocialProvider() != null && this.driver.getSocialProvider().getSocialNetwork().equals(SocialNetwork.GOOGLE_PLUS)) {
            this.imageViewSocialIcon.setImageResource(R.drawable.ic_google_plus);

        } else
            this.rippleLayoutSocialProfile.setVisibility(View.GONE);
    }

    public void sendSMSToDriver(View v) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_CODE_SEND_SMS_PERMISSIONS);
            return;
        }

        Intent smsIntent = PhoneUtils.createSendSMSIntent(this.driver.getPhone());
        startActivity(smsIntent);
    }

    public void callDriver(View v) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PHONE_PERMISSIONS);
            return;
        }
        Intent callIntent = PhoneUtils.createCallPhoneIntent(this.driver.getPhone());
        startActivity(callIntent);
    }

    public void showErrorLayout() {
        this.frameLayoutDriverDetails.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    public void setErrorMessage(String string) {
        this.noDataTextView.setText(string);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(getTagFromActivity(this), "PERMISSION CODE " + requestCode);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CODE_CALL_PHONE_PERMISSIONS: {
                    Intent callIntent = PhoneUtils.createCallPhoneIntent(this.driver.getPhone());
                    startActivity(callIntent);
                    break;
                }
                case REQUEST_CODE_SEND_SMS_PERMISSIONS: {
                    Intent callIntent = PhoneUtils.createSendSMSIntent(this.driver.getPhone());
                    startActivity(callIntent);
                    break;
                }
            }
        }
    }

}
