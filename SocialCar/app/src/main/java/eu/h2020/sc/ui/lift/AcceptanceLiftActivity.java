package eu.h2020.sc.ui.lift;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.LiftStatus;
import eu.h2020.sc.domain.PushMessageData;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.domain.messages.Contact;
import eu.h2020.sc.domain.socialnetwork.SocialNetwork;
import eu.h2020.sc.socialnetwork.facebook.FacebookManager;
import eu.h2020.sc.socialnetwork.googleplus.GooglePlusManager;
import eu.h2020.sc.ui.feedback.FeedbackAdapter;
import eu.h2020.sc.ui.lift.passenger.PassengerFeedbackTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.ImageUtils;
import eu.h2020.sc.utils.MaterialRippleLayout;
import eu.h2020.sc.utils.PhoneUtils;
import eu.h2020.sc.utils.PicassoHelper;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class AcceptanceLiftActivity extends GeneralActivity implements View.OnClickListener {

    public static final String TAG = AcceptanceLiftActivity.class.getSimpleName();

    private static final int REQUEST_CODE_CALL_PHONE_PERMISSIONS = 100;
    private static final int REQUEST_CODE_SEND_SMS_PERMISSIONS = 101;

    private ImageView imageViewDecline;
    private ImageView imageViewAccept;
    private TextView textViewUsername;
    private TextView textViewRating;
    private ScrollView scrollViewDetails;
    private RelativeLayout relativeLayoutDetails;
    private LinearLayout linearLayoutFeedback;
    private View viewReviewDivider;
    private LinearLayout linearLayoutAcceptLift;

    private TextView textViewFrom;
    private TextView textViewTo;
    private TextView textViewOn;

    private View noNoDataLayout;
    private Button btnNoDataRetry;
    private TextView noDataTextView;

    private PushMessageData pushMessageData;

    private LinearLayout linearLayoutUserActions;
    private MaterialRippleLayout rippleLayoutSocialProfile;
    private ImageView imageViewSocialIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptance_lift);

        this.pushMessageData = (PushMessageData) ActivityUtils.getSerializableFromIntent(this, TAG);

        this.initUI();
        this.initEvents();
        this.getUserFeedback();
    }

    private void initUI() {
        initToolBar(false);

        findViewById(R.id.toolbar_accept_lift).setVisibility(View.VISIBLE);

        this.textViewFrom = (TextView) findViewById(R.id.lift_details_text_view_from);
        this.textViewTo = (TextView) findViewById(R.id.lift_details_text_view_to);
        this.textViewOn = (TextView) findViewById(R.id.lift_details_text_view_on);

        this.scrollViewDetails = (ScrollView) findViewById(R.id.lift_details_scrollview_user_details);
        this.relativeLayoutDetails = (RelativeLayout) findViewById(R.id.lift_details_relative_layout_pending);
        this.linearLayoutFeedback = (LinearLayout) findViewById(R.id.lift_details_linear_layout_feedback);
        this.imageViewDecline = (ImageView) this.relativeLayoutDetails.findViewById(R.id.lift_details_image_view_decline);
        this.imageViewAccept = (ImageView) this.relativeLayoutDetails.findViewById(R.id.lift_details_image_view_accept);

        ImageView imageViewUser = (ImageView) this.relativeLayoutDetails.findViewById(R.id.lift_details_image_view_avatar);
        this.textViewUsername = (TextView) this.relativeLayoutDetails.findViewById(R.id.lift_details_text_view_user_name);
        this.textViewRating = (TextView) this.relativeLayoutDetails.findViewById(R.id.lift_details_text_view_user_rating);
        this.viewReviewDivider = findViewById(R.id.lift_details_view_review_divider);

        this.linearLayoutAcceptLift = (LinearLayout) findViewById(R.id.lift_details_frame_layout_accept_lift);
        this.noNoDataLayout = this.findViewById(R.id.acceptance_lift_layout_no_data_retry);

        assert this.noNoDataLayout != null;
        this.btnNoDataRetry = (Button) this.noNoDataLayout.findViewById(R.id.btn_no_data_retry);
        this.noDataTextView = (TextView) this.noNoDataLayout.findViewById(R.id.no_data_text_view);

        ImageView imageViewStatus = (ImageView) this.relativeLayoutDetails.findViewById(R.id.lift_details_image_view_shape);
        ImageView imageViewStatusGif = (ImageView) this.relativeLayoutDetails.findViewById(R.id.lift_details_image_view_status_gif);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.large_status_gif);
        imageViewStatusGif.setAnimation(anim);

        imageViewStatus.setVisibility(View.VISIBLE);
        imageViewStatusGif.setVisibility(View.VISIBLE);

        imageViewStatus.setImageResource(R.drawable.shape_large_pending_gif_mask);
        imageViewStatusGif.setColorFilter(ContextCompat.getColor(this, R.color.accent_color), PorterDuff.Mode.SRC_ATOP);

        this.imageViewSocialIcon = (ImageView) findViewById(R.id.image_view_social_icon);
        this.rippleLayoutSocialProfile = (MaterialRippleLayout) findViewById(R.id.social_profile);
        this.linearLayoutUserActions = (LinearLayout) findViewById(R.id.layout_user_actions);

        this.showLiftDetails();
        this.showUserDetails();

        PicassoHelper.loadCircleImage(this, imageViewUser, ImageUtils.fromByteArrayToBitmap(this.pushMessageData.getPicture()));
    }

    private void initEvents() {
        this.btnNoDataRetry.setOnClickListener(this);
    }

    private void showLiftDetails() {

        this.textViewFrom.setText(this.pushMessageData.getLift().getStartPoint().getAddress());
        this.textViewTo.setText(this.pushMessageData.getLift().getEndPoint().getAddress());

        this.textViewOn.setText(String.format("%s %s %s", DateUtils.dateLongNoHourToString(this.pushMessageData.getLift().getStartPoint().getDate()),
                getString(R.string.at_what_hour),
                DateUtils.hourLongToString(this.pushMessageData.getLift().getStartPoint().getDate(), DateFormat.is24HourFormat(this))));
    }

    private void showSocialNetworkAction() {
        if (this.pushMessageData.getUser().getSocialProvider() != null && this.pushMessageData.getUser().getSocialProvider().getSocialNetwork().equals(SocialNetwork.FACEBOOK)) {
            this.imageViewSocialIcon.setImageResource(R.drawable.ic_facebook);

        } else if (this.pushMessageData.getUser().getSocialProvider() != null && this.pushMessageData.getUser().getSocialProvider().getSocialNetwork().equals(SocialNetwork.GOOGLE_PLUS)) {
            this.imageViewSocialIcon.setImageResource(R.drawable.ic_google_plus);

        } else
            this.rippleLayoutSocialProfile.setVisibility(View.GONE);
    }

    public void showSocialProfile(View view) {
        if (this.pushMessageData.getUser().getSocialProvider() != null && this.pushMessageData.getUser().getSocialProvider().getSocialNetwork().equals(SocialNetwork.FACEBOOK)) {
            startActivity(FacebookManager.createIntentFacebookProfile(this, this.pushMessageData.getUser().getSocialProvider().getSocialID()));
        } else {
            startActivity(GooglePlusManager.createIntentToGooglePlusProfile(this, this.pushMessageData.getUser().getSocialProvider().getSocialID()));
        }
    }

    public void changeResDecline(View view) {
        this.imageViewDecline.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.shape_action_decline_selected));
        this.pushMessageData.getLift().setStatus(LiftStatus.REFUSED);

        this.acceptDeclineLift();
    }

    public void changeResAccept(View view) {
        this.imageViewAccept.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.shape_action_accept_selected));
        this.pushMessageData.getLift().setStatus(LiftStatus.ACTIVE);

        this.acceptDeclineLift();
    }

    private void acceptDeclineLift() {
        showProgressDialog();

        AcceptanceLiftTask acceptanceLiftTask = new AcceptanceLiftTask(this);
        acceptanceLiftTask.execute(this.pushMessageData.getLift());
    }

    private void getUserFeedback() {
        showProgressDialog();
        PassengerFeedbackTask passengerFeedbackTask = new PassengerFeedbackTask(this);
        passengerFeedbackTask.execute(this.pushMessageData.getLift().getPassengerID());
    }

    private void showUserDetails() {
        this.textViewUsername.setText(this.pushMessageData.getUser().getName());
        this.textViewRating.setText(String.valueOf(this.pushMessageData.getUser().getRating()));
    }

    public void showFeedback(List<Feedback> feedbackList) {

        this.textViewRating.setText(String.format("%s (%s feedback)", String.valueOf(this.pushMessageData.getUser().getRating()), feedbackList.size()));

        if (feedbackList.size() == 0)
            this.viewReviewDivider.setVisibility(View.GONE);

        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(AcceptanceLiftActivity.this, this.linearLayoutFeedback);
        feedbackAdapter.createFeedbackView(feedbackList);

        this.relativeLayoutDetails.setVisibility(View.VISIBLE);
        this.scrollViewDetails.setVisibility(View.VISIBLE);

        this.showSocialNetworkAction();
        this.showUserActions();
    }

    @Override
    public void showServerGenericError() {
        dismissDialog();

        this.relativeLayoutDetails.setVisibility(View.GONE);
        this.scrollViewDetails.setVisibility(View.GONE);
        this.linearLayoutUserActions.setVisibility(View.GONE);

        this.linearLayoutAcceptLift.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
        this.noDataTextView.setText(getResources().getString(R.string.generic_error));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showConnectionError() {
        dismissDialog();

        this.relativeLayoutDetails.setVisibility(View.GONE);
        this.scrollViewDetails.setVisibility(View.GONE);
        this.linearLayoutUserActions.setVisibility(View.GONE);

        this.linearLayoutAcceptLift.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
        this.noDataTextView.setText(getResources().getString(R.string.no_connection_error));

        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        this.linearLayoutAcceptLift.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        this.noNoDataLayout.setVisibility(View.GONE);

        this.getUserFeedback();
    }

    private void showUserActions() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.relativeLayoutDetails.getLayoutParams();
        params.bottomMargin = 0;
        this.relativeLayoutDetails.setLayoutParams(params);
        this.relativeLayoutDetails.requestFocus();

        this.linearLayoutUserActions.setVisibility(View.VISIBLE);
    }

    public void callUser(View view) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PHONE_PERMISSIONS);
            return;
        }
        Intent callIntent = PhoneUtils.createCallPhoneIntent(this.pushMessageData.getUser().getPhone());
        startActivity(callIntent);
    }

    public void sendSMSToUser(View view) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_CODE_SEND_SMS_PERMISSIONS);
            return;
        }
        Intent smsIntent = PhoneUtils.createSendSMSIntent(this.pushMessageData.getUser().getPhone());
        startActivity(smsIntent);
    }

    public void liftUpdated() {

        if (this.pushMessageData.getLift().getStatus() == LiftStatus.ACTIVE) {

            Contact contact;

            if (this.pushMessageData.getUser().getUserPictures() != null && !this.pushMessageData.getUser().getUserPictures().isEmpty())
                contact = new Contact(this.pushMessageData.getUser().getId(), this.pushMessageData.getUser().getName(), this.pushMessageData.getUser().getUserPictures().get(0).getMediaUri(), this.pushMessageData.getLift().getID());
            else
                contact = new Contact(this.pushMessageData.getUser().getId(), this.pushMessageData.getUser().getName(), this.pushMessageData.getLift().getID());

            ChatController.getInstance().storeContact(contact);
        }

        ActivityUtils.openActivityWithParam(this, LiftActivity.class, LiftActivity.TAG, 1);
    }
}

