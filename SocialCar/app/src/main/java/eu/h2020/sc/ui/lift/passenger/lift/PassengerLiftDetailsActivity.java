package eu.h2020.sc.ui.lift.passenger.lift;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftStatus;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.domain.socialnetwork.SocialNetwork;
import eu.h2020.sc.socialnetwork.facebook.FacebookManager;
import eu.h2020.sc.socialnetwork.googleplus.GooglePlusManager;
import eu.h2020.sc.ui.feedback.FeedbackAdapter;
import eu.h2020.sc.ui.feedback.RatingSurveyActivity;
import eu.h2020.sc.ui.lift.CreateFeedbackObserver;
import eu.h2020.sc.ui.lift.LiftActivity;
import eu.h2020.sc.ui.lift.passenger.lift.task.CreateFeedbackTask;
import eu.h2020.sc.ui.lift.passenger.lift.task.PassengerLiftDetailsTask;
import eu.h2020.sc.ui.lift.passenger.lift.task.UpdatePassengerLiftTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.MaterialRippleLayout;
import eu.h2020.sc.utils.PhoneUtils;
import eu.h2020.sc.utils.PicassoHelper;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.Utils;
import eu.h2020.sc.utils.WidgetHelper;

/**
 * Created by
 * Fabio Lombardi <fabio.lombardi@movenda.com> on 05/09/2016.
 * © All rights reserved by Movenda S.p.A..
 */
public class PassengerLiftDetailsActivity extends GeneralActivity implements View.OnClickListener, MaterialDialog.SingleButtonCallback, CreateFeedbackObserver {

    public static final String LIFT_KEY = "LIFT_KEY";

    private static final int REQUEST_CODE_CALL_PHONE_PERMISSIONS = 100;
    private static final int REQUEST_CODE_SEND_SMS_PERMISSIONS = 101;

    private View layoutDetailsView;
    private View viewReviewDivider;
    private ImageView imageViewAcceptOrSmsButton;
    private ImageView imageViewDeclineOrCallButton;

    private TextView textViewFrom;
    private TextView textViewOn;
    private TextView textViewTo;
    private ScrollView scrollViewUserDetails;

    private View noNoDataLayout;
    private Button btnNoDataRetry;
    private TextView noDataTextView;

    //rating element:
    //preview
    private ImageView starRatingPreview1;
    private ImageView starRatingPreview2;
    private ImageView starRatingPreview3;
    private ImageView starRatingPreview4;
    private ImageView starRatingPreview5;

    //addRating
    private View addRatingLayout;
    private ImageView starRating1;
    private ImageView starRating2;
    private ImageView starRating3;
    private ImageView starRating4;
    private ImageView starRating5;
    private int[] idsStarRating = {R.id.add_rating_star_1, R.id.add_rating_star_2, R.id.add_rating_star_3, R.id.add_rating_star_4, R.id.add_rating_star_5};
    private ImageView[] imageViewsStarRating = {starRating1, starRating2, starRating3, starRating4, starRating5};
    private View feedbackBoxContainerLayout;
    private Button sendFeedbackButton;
    private EditText feedbackEditText;
    private View starsLayout;
    private View insertReviewLayout;
    private View askSurveyTextLayout;
    private View askSurveyButtonsLayout;
    private TextViewCustom nameReviewedText;
    private Button goToSurveyOkButton;
    private Button goToSurveyNotNowButton;
    private TextViewCustom askGoToSurveyText;
    private TextViewCustom previewRatingTitleText;
    private LinearLayout linearLayoutUserActions;
    private ImageView imageViewPassenger;
    private ImageView ratingUserImageView;

    private Lift lift;
    private User passenger;

    private Feedback feedbackCreated;
    private CreateFeedbackTask createFeedbackTask;
    private int selectedRating = 0;

    private MaterialRippleLayout rippleLayoutSocialProfile;
    private ImageView imageViewSocialIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptance_lift);

        this.lift = (Lift) ActivityUtils.getSerializableFromIntent(this, LIFT_KEY);

        this.initUI();
        this.initEvents();
        this.showLiftDetails();

        this.executeRequests();
    }

    private void initUI() {
        initToolBar(false);
        initBack();

        this.noNoDataLayout = this.findViewById(R.id.acceptance_lift_layout_no_data_retry);
        this.btnNoDataRetry = (Button) this.noNoDataLayout.findViewById(R.id.btn_no_data_retry);
        this.noDataTextView = (TextView) this.noNoDataLayout.findViewById(R.id.no_data_text_view);
        this.viewReviewDivider = findViewById(R.id.lift_details_view_review_divider);
        this.textViewFrom = (TextView) findViewById(R.id.lift_details_text_view_from);
        this.textViewOn = (TextView) findViewById(R.id.lift_details_text_view_on);
        this.textViewTo = (TextView) findViewById(R.id.lift_details_text_view_to);
        this.scrollViewUserDetails = (ScrollView) findViewById(R.id.lift_details_scrollview_user_details);

        this.linearLayoutUserActions = (LinearLayout) findViewById(R.id.layout_user_actions);
        this.imageViewSocialIcon = (ImageView) findViewById(R.id.image_view_social_icon);
        this.rippleLayoutSocialProfile = (MaterialRippleLayout) findViewById(R.id.social_profile);

        this.initLiftStatus();
    }

    private void initLiftStatus() {

        switch (this.lift.getStatus()) {
            case PENDING:
                findViewById(R.id.toolbar_accept_lift).setVisibility(View.VISIBLE);
                findViewById(R.id.image_view_back).setVisibility(View.GONE);

                this.layoutDetailsView = findViewById(R.id.lift_details_relative_layout_pending);
                this.imageViewDeclineOrCallButton = (ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_image_view_decline);
                this.imageViewAcceptOrSmsButton = (ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_image_view_accept);
                initPulseUI(R.drawable.shape_large_pending_gif_mask, R.color.accent_color);
                setTitle(R.string.pending_lift_title);
                break;
            case ACTIVE:
                findViewById(R.id.toolbar_accept_lift).setVisibility(View.GONE);
                findViewById(R.id.image_view_back).setVisibility(View.VISIBLE);

                this.layoutDetailsView = findViewById(R.id.lift_details_relative_layout_accepted);
                this.imageViewDeclineOrCallButton = (ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_image_view_callphone);
                this.imageViewAcceptOrSmsButton = (ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_imageview_send_sms);
                initPulseUI(R.drawable.shape_large_accepted_gif_mask, R.color.green);
                setTitle(R.string.active_lift_title);
                break;
            case REVIEWED:
                findViewById(R.id.toolbar_accept_lift).setVisibility(View.VISIBLE);
                setTitle(R.string.completed_lift_title);
                this.layoutDetailsView = findViewById(R.id.lift_details_relative_layout_completed);
                break;
            case COMPLETED:
                findViewById(R.id.toolbar_accept_lift).setVisibility(View.VISIBLE);
                initUIFeedback();
                initEventsFeedback();
                this.createFeedbackTask = new CreateFeedbackTask(this);
                break;
            case CANCELLED:
                findViewById(R.id.toolbar_accept_lift).setVisibility(View.VISIBLE);
                this.layoutDetailsView = findViewById(R.id.lift_details_relative_layout_completed);
                setTitle(R.string.cancelled_lift_title);
                break;
            case REFUSED:
                findViewById(R.id.toolbar_accept_lift).setVisibility(View.VISIBLE);
                this.layoutDetailsView = findViewById(R.id.lift_details_relative_layout_completed);
                setTitle(R.string.declined_lift_title);
                break;
            default:
                break;
        }
    }

    private void initPulseUI(int statusColor, int gifColor) {
        ((ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_image_view_shape)).setImageResource(statusColor);
        ImageView imageViewStatusGif = (ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_image_view_status_gif);
        imageViewStatusGif.setAnimation(AnimationUtils.loadAnimation(this, R.anim.large_status_gif));
        imageViewStatusGif.setColorFilter(ContextCompat.getColor(this, gifColor), PorterDuff.Mode.SRC_ATOP);
    }

    private void initEvents() {
        this.btnNoDataRetry.setOnClickListener(this);
    }

    private void showLiftDetails() {

        this.textViewFrom.setText(this.lift.getStartPoint().getAddress());
        this.textViewTo.setText(this.lift.getEndPoint().getAddress());

        String liftDate = String.format("%s %s %s",
                DateUtils.dateLongNoHourToString(this.lift.getStartPoint().getDate()),
                getString(R.string.at_what_hour),
                DateUtils.hourLongToString(this.lift.getStartPoint().getDate(), DateFormat.is24HourFormat(this)));

        this.textViewOn.setText(liftDate);
    }

    private void showSocialNetworkAction() {
        if (this.passenger.getSocialProvider() != null && this.passenger.getSocialProvider().getSocialNetwork().equals(SocialNetwork.FACEBOOK)) {
            this.imageViewSocialIcon.setImageResource(R.drawable.ic_facebook);

        } else if (this.passenger.getSocialProvider() != null && this.passenger.getSocialProvider().getSocialNetwork().equals(SocialNetwork.GOOGLE_PLUS)) {
            this.imageViewSocialIcon.setImageResource(R.drawable.ic_google_plus);

        } else
            this.rippleLayoutSocialProfile.setVisibility(View.GONE);
    }

    public void showSocialProfile(View view) {
        if (this.passenger.getSocialProvider() != null && this.passenger.getSocialProvider().getSocialNetwork().equals(SocialNetwork.FACEBOOK)) {
            startActivity(FacebookManager.createIntentFacebookProfile(this, this.passenger.getSocialProvider().getSocialID()));
        } else {
            startActivity(GooglePlusManager.createIntentToGooglePlusProfile(this, this.passenger.getSocialProvider().getSocialID()));
        }
    }

    public void showErrorLayout(String resource) {
        this.layoutDetailsView.setVisibility(View.GONE);

        this.noDataTextView.setText(resource);
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    private void executeRequests() {
        showProgressDialog();

        PassengerLiftDetailsTask taskExecutor = new PassengerLiftDetailsTask(this);
        taskExecutor.execute(this.lift.getPassengerID());
    }

    private void executeCancelLiftRequest() {
        this.lift.setStatus(LiftStatus.CANCELLED);

        UpdatePassengerLiftTask updatePassengerLiftTask = new UpdatePassengerLiftTask(this);
        updatePassengerLiftTask.execute(this.lift);
    }

    public void showLayoutsDetails() {
        this.layoutDetailsView.setVisibility(View.VISIBLE);
        this.scrollViewUserDetails.setVisibility(View.VISIBLE);

        if (this.lift.getStatus() == LiftStatus.ACTIVE) {
            findViewById(R.id.btn_cancel_lift).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_cancel_lift_mask).setVisibility(View.VISIBLE);
        }

        if (this.lift.getStatus() == LiftStatus.ACTIVE) {
            this.linearLayoutUserActions.setVisibility(View.VISIBLE);
        }

        if (this.lift.getStatus() == LiftStatus.PENDING) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.layoutDetailsView.getLayoutParams();
            params.bottomMargin = 0;
            this.layoutDetailsView.setLayoutParams(params);
            this.layoutDetailsView.requestFocus();
            this.linearLayoutUserActions.setVisibility(View.VISIBLE);
        }

        if (this.lift.getStatus() == LiftStatus.COMPLETED)
            showCompletedLayout();
    }

    public void showRatingWithFeedback(User passenger, List<Feedback> feedbackList) {
        ((TextView) this.layoutDetailsView.findViewById(R.id.lift_details_text_view_user_rating)).setText(String.format("%s (%s feedback)", passenger.getRating(), feedbackList.size()));
    }

    public void showFeedback(List<Feedback> feedbackList) {

        if (feedbackList.size() == 0)
            this.viewReviewDivider.setVisibility(View.GONE);

        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(this, (LinearLayout) findViewById(R.id.lift_details_linear_layout_feedback));
        feedbackAdapter.createFeedbackView(feedbackList);
    }

    public void showUserDetails(User passenger) {
        this.showSocialNetworkAction();
        ((TextView) this.layoutDetailsView.findViewById(R.id.lift_details_text_view_user_name)).setText(passenger.getName());

        ImageView imageViewPassenger = (ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_image_view_avatar);

        if (passenger.getUserPictures() != null && !passenger.getUserPictures().isEmpty())
            PicassoHelper.loadMedia(this, imageViewPassenger, passenger.getUserPictures().get(0).getMediaUri(), R.drawable.img_default_feedback_avatar, R.drawable.img_default_feedback_avatar, true);
        else
            PicassoHelper.loadPictureByUserID(this, imageViewPassenger, passenger.getId(), R.drawable.img_default_feedback_avatar, R.drawable.img_default_feedback_avatar, true);
    }

    public void changeResDecline(View view) {
        this.imageViewDeclineOrCallButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.shape_action_decline_selected));

        this.lift.setStatus(LiftStatus.REFUSED);
        this.executeUpdateLiftRequest(this.lift);
    }

    public void changeResAccept(View view) {
        this.imageViewAcceptOrSmsButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.shape_action_accept_selected));

        this.lift.setStatus(LiftStatus.ACTIVE);
        this.executeUpdateLiftRequest(this.lift);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    private void executeUpdateLiftRequest(Lift lift) {
        showProgressDialog();

        UpdatePassengerLiftTask updatePassengerLiftTask = new UpdatePassengerLiftTask(this);
        updatePassengerLiftTask.execute(lift);
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public void callUser(View view) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PHONE_PERMISSIONS);
            return;
        }
        Intent callIntent = PhoneUtils.createCallPhoneIntent(this.passenger.getPhone());
        startActivity(callIntent);
    }

    public void sendSMSToUser(View view) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_CODE_SEND_SMS_PERMISSIONS);
            return;
        }

        Intent smsIntent = PhoneUtils.createSendSMSIntent(this.passenger.getPhone());
        startActivity(smsIntent);
    }

    public void cancelRequest(View view) {
        if (checkClickTime()) return;
        MaterialDialogHelper.createDialog(PassengerLiftDetailsActivity.this, R.string.cancel_request, R.string.cancel_request_confirmation_text, R.string.yes, R.string.no, this).show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        showProgressDialog();
        this.executeCancelLiftRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no_data_retry:
                this.noNoDataLayout.setVisibility(View.GONE);
                this.executeRequests();
                break;
            case R.id.box_add_rating_star_preview_1:
                this.selectedRating = 1;
                showAddFeedbackLayout(1);
                break;
            case R.id.box_add_rating_star_preview_2:
                this.selectedRating = 2;
                showAddFeedbackLayout(2);
                break;
            case R.id.box_add_rating_star_preview_3:
                this.selectedRating = 3;
                showAddFeedbackLayout(3);
                break;
            case R.id.box_add_rating_star_preview_4:
                this.selectedRating = 4;
                showAddFeedbackLayout(4);
                break;
            case R.id.box_add_rating_star_preview_5:
                this.selectedRating = 5;
                showAddFeedbackLayout(5);
                break;
            case R.id.add_rating_star_1:
                this.selectedRating = 1;
                this.createFeedbackTask.setFeedbackRating(1);
                fillStarEffect(1);
                break;
            case R.id.add_rating_star_2:
                this.selectedRating = 2;
                this.createFeedbackTask.setFeedbackRating(2);
                fillStarEffect(2);
                break;
            case R.id.add_rating_star_3:
                this.selectedRating = 3;
                this.createFeedbackTask.setFeedbackRating(3);
                fillStarEffect(3);
                break;
            case R.id.add_rating_star_4:
                this.selectedRating = 4;
                this.createFeedbackTask.setFeedbackRating(4);
                fillStarEffect(4);
                break;
            case R.id.add_rating_star_5:
                this.selectedRating = 5;
                this.createFeedbackTask.setFeedbackRating(5);
                fillStarEffect(5);
                break;
            case R.id.activity_acceptance_lift_add_rating_layout:
                hideAddFeedbackLayout();
                break;
            case R.id.add_rating_layout_feedback_container_linear_layout:
                break;
            case R.id.add_rating_layout_send_button:
                Utils.hideSoftKeyboard(this);
                showProgressDialog();
                this.createFeedbackTask.execute();
                break;
            case R.id.add_rating_layout_survey_ok_button:
                HashMap<String, Serializable> params = new HashMap<>();
                params.put(RatingSurveyActivity.FEEDBACK_CREATED_KEY, feedbackCreated);
                ActivityUtils.openActivityWithObjectParams(this, RatingSurveyActivity.class, params);
                break;
            case R.id.add_rating_layout_survey_not_now_button:
                ActivityUtils.openActivityNoBack(this, LiftActivity.class);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(getTagFromActivity(this), "PERMISSION CODE " + requestCode);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CODE_CALL_PHONE_PERMISSIONS: {
                    Intent callIntent = PhoneUtils.createCallPhoneIntent(this.passenger.getPhone());
                    startActivity(callIntent);
                    break;
                }
                case REQUEST_CODE_SEND_SMS_PERMISSIONS: {
                    Intent callIntent = PhoneUtils.createSendSMSIntent(this.passenger.getPhone());
                    startActivity(callIntent);
                    break;
                }
            }
        }
    }

    public void onFeedbackSuccess(Feedback feedback) {
        this.feedbackCreated = feedback;

        this.hideAddFeedbackLayout();
        findViewById(R.id.lift_details_box_preview_rating_external_linear).setVisibility(View.GONE);

        HashMap<String, Serializable> params = new HashMap<>();
        params.put(RatingSurveyActivity.FEEDBACK_CREATED_KEY, this.feedbackCreated);
        ActivityUtils.openActivityWithObjectParams(this, RatingSurveyActivity.class, params);
    }

    private void initUIFeedback() {
        this.layoutDetailsView = findViewById(R.id.lift_details_relative_layout_completed);
        setTitle(R.string.completed_lift_title);
        findViewById(R.id.lift_details_box_user_detail_linear).setPadding(0, 0, 0, Utils.fromDptoPx(110, this));
        this.previewRatingTitleText = (TextViewCustom) this.layoutDetailsView.findViewById(R.id.box_add_rating_title_text);

        this.starRatingPreview1 = (ImageView) this.layoutDetailsView.findViewById(R.id.box_add_rating_star_preview_1);
        this.starRatingPreview2 = (ImageView) this.layoutDetailsView.findViewById(R.id.box_add_rating_star_preview_2);
        this.starRatingPreview3 = (ImageView) this.layoutDetailsView.findViewById(R.id.box_add_rating_star_preview_3);
        this.starRatingPreview4 = (ImageView) this.layoutDetailsView.findViewById(R.id.box_add_rating_star_preview_4);
        this.starRatingPreview5 = (ImageView) this.layoutDetailsView.findViewById(R.id.box_add_rating_star_preview_5);

        this.addRatingLayout = findViewById(R.id.activity_acceptance_lift_add_rating_layout);

        for (int i = 0; i < 5; i++) {
            this.imageViewsStarRating[i] = (ImageView) this.addRatingLayout.findViewById(this.idsStarRating[i]);
        }
        this.nameReviewedText = (TextViewCustom) this.addRatingLayout.findViewById(R.id.add_rating_layout_name_reviewed_text);
        this.feedbackBoxContainerLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_feedback_container_linear_layout);
        this.sendFeedbackButton = (Button) this.addRatingLayout.findViewById(R.id.add_rating_layout_send_button);
        this.feedbackEditText = (EditText) this.addRatingLayout.findViewById(R.id.add_rating_layout_feedback_edit_text);
        this.starsLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_stars_layout);
        this.insertReviewLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_add_feedback_layout);
        this.askSurveyTextLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_ask_survey_text_layout);
        this.askSurveyButtonsLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_buttons_survey_layout);
        this.askGoToSurveyText = (TextViewCustom) this.addRatingLayout.findViewById(R.id.add_rating_layout_ask_survey_text);
        this.goToSurveyOkButton = (Button) this.addRatingLayout.findViewById(R.id.add_rating_layout_survey_ok_button);
        this.goToSurveyNotNowButton = (Button) this.addRatingLayout.findViewById(R.id.add_rating_layout_survey_not_now_button);
        this.imageViewPassenger = (ImageView) this.layoutDetailsView.findViewById(R.id.lift_details_image_view_avatar);
        this.ratingUserImageView = (ImageView) findViewById(R.id.activity_acceptance_lift_add_rating_layout).findViewById(R.id.passenger_details_rating_imageview);
    }

    private void initEventsFeedback() {
        this.starRatingPreview1.setOnClickListener(this);
        this.starRatingPreview2.setOnClickListener(this);
        this.starRatingPreview3.setOnClickListener(this);
        this.starRatingPreview4.setOnClickListener(this);
        this.starRatingPreview5.setOnClickListener(this);

        for (int i = 0; i < 5; i++) {
            this.imageViewsStarRating[i].setOnClickListener(this);
        }
        this.addRatingLayout.setOnClickListener(this);
        this.feedbackBoxContainerLayout.setOnClickListener(this);
        this.sendFeedbackButton.setOnClickListener(this);

        this.goToSurveyOkButton.setOnClickListener(this);
        this.goToSurveyNotNowButton.setOnClickListener(this);
    }

    public void showCompletedLayout() {
        this.layoutDetailsView.setVisibility(View.VISIBLE);

        this.previewRatingTitleText.setText(String.format(getString(R.string.question_rating_title), User.getFirstNameFromCompleteName(passenger.getName())));
        findViewById(R.id.lift_details_box_preview_rating_external_linear).setVisibility(View.VISIBLE);
        this.layoutDetailsView.findViewById(R.id.box_add_rating_info_text).setVisibility(View.VISIBLE);
        this.sendFeedbackButton.setVisibility(View.VISIBLE);
        this.starsLayout.setVisibility(View.VISIBLE);
        this.insertReviewLayout.setVisibility(View.VISIBLE);
        this.askSurveyTextLayout.setVisibility(View.GONE);
        this.askSurveyButtonsLayout.setVisibility(View.GONE);
    }

    private void showAddFeedbackLayout(int previewStar) {
        this.createFeedbackTask.setFeedbackRating(previewStar);
        this.addRatingLayout.setVisibility(View.VISIBLE);

        this.sendFeedbackButton.setVisibility(View.VISIBLE);
        this.starsLayout.setVisibility(View.VISIBLE);
        this.insertReviewLayout.setVisibility(View.VISIBLE);

        this.askSurveyTextLayout.setVisibility(View.GONE);
        this.askSurveyButtonsLayout.setVisibility(View.GONE);

        this.nameReviewedText.setText(this.passenger.getName());

        ratingUserImageView.setImageDrawable(imageViewPassenger.getDrawable().getConstantState().newDrawable());

        this.fillStarEffect(previewStar);
    }

    private void hideAddFeedbackLayout() {
        this.addRatingLayout.setVisibility(View.GONE);
    }

    private void fillStarEffect(int starsToFill) {
        for (int i = 0; i < starsToFill; i++) {
            this.imageViewsStarRating[i].setImageResource(R.drawable.ic_star_add_rating_filled);
        }
        for (int i = 4; i >= starsToFill; i--) {
            this.imageViewsStarRating[i].setImageResource(R.drawable.ic_star_add_rating_border);
        }
    }

    public String getFeedbackText() {
        return this.feedbackEditText.getText().toString();
    }

    public User getReviewed() {
        return passenger;
    }

    public Lift getLift() {
        return lift;
    }


    @Override
    public void showGenericError() {
        showServerGenericError();
        this.createFeedbackTask = new CreateFeedbackTask(this);
        this.createFeedbackTask.setFeedbackRating(this.selectedRating);
    }

    @Override
    public void showNoConnection() {
        showConnectionError();
        this.createFeedbackTask = new CreateFeedbackTask(this);
        this.createFeedbackTask.setFeedbackRating(this.selectedRating);
    }

    public void liftUpdated(LiftStatus liftStatus) {
        finish();
        switch (liftStatus) {
            case ACTIVE:
                WidgetHelper.showToast(this, getString(R.string.message_lift_updated_as_active));
                break;
            case CANCELLED:
                ChatController.getInstance().removeContact(this.passenger.getId());
                WidgetHelper.showToast(this, getString(R.string.message_lift_updated_as_cancelled));
                break;
            case REFUSED:
                WidgetHelper.showToast(this, getString(R.string.message_lift_updated_as_refused));
                break;
            case COMPLETED:
            case PENDING:
            default:
                WidgetHelper.showToast(this, getString(R.string.message_lift_updated));
                break;

        }
    }
}
