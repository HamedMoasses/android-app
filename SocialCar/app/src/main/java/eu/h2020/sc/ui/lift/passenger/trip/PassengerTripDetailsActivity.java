package eu.h2020.sc.ui.lift.passenger.trip;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.Serializable;
import java.util.HashMap;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftStatus;
import eu.h2020.sc.domain.Step;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.feedback.RatingSurveyActivity;
import eu.h2020.sc.ui.home.driver.DriverDetailsActivity;
import eu.h2020.sc.ui.lift.CreateFeedbackObserver;
import eu.h2020.sc.ui.lift.LiftActivity;
import eu.h2020.sc.ui.lift.passenger.lift.task.CreateFeedbackTask;
import eu.h2020.sc.ui.trip.solution.details.adapter.TripSolutionDetailsAdapter;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.PicassoHelper;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.Utils;

public class PassengerTripDetailsActivity extends GeneralActivity implements RecyclerViewItemClickListener, MaterialDialog.SingleButtonCallback, View.OnClickListener, CreateFeedbackObserver {

    public static final String LIFT_KEY = "LIFT_KEY";

    private TextView textViewTime;
    private TextView textViewTotalPrice;
    private TextView textViewLiftStatus;
    private TextView textViewDriverStatusRequest;
    private ImageView imageViewStatus;
    private ImageView imageViewStatusGif;
    private RecyclerView recyclerViewTripDetails;
    private RelativeLayout relativeLayoutTripStatusContainer;
    private Button btnCancelLift;
    private TripSolutionDetailsAdapter detailsAdapter;
    private Lift lift;

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

    private Feedback feedbackCreated;
    private CreateFeedbackTask createFeedbackTask;
    private int selectedRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_trip_details);

        this.lift = (Lift) ActivityUtils.getSerializableFromIntent(this, LIFT_KEY);

        initToolBar(false);
        initBack();

        this.initUI();
        this.showTripDetails();
    }

    private void initUI() {

        this.relativeLayoutTripStatusContainer = (RelativeLayout) findViewById(R.id.layout_trip_status_container);
        this.textViewTime = (TextView) findViewById(R.id.textView_total_time);
        this.textViewTotalPrice = (TextView) findViewById(R.id.textView_total_price);
        this.textViewLiftStatus = (TextView) findViewById(R.id.textView_status);
        this.textViewDriverStatusRequest = (TextView) findViewById(R.id.textView_user_request_status);
        this.imageViewStatus = (ImageView) findViewById(R.id.image_view_shape);
        this.btnCancelLift = (Button) findViewById(R.id.btn_cancel_lift);
        this.imageViewStatusGif = (ImageView) findViewById(R.id.statusGif);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.status_gif);
        this.imageViewStatusGif.setAnimation(anim);

        this.recyclerViewTripDetails = (RecyclerView) findViewById(R.id.recycler_view_passenger_lift_details);

        this.initRecycleView();
    }

    public void initAdapter(Trip trip) {
        this.detailsAdapter = new TripSolutionDetailsAdapter(trip);
        this.detailsAdapter.setOnItemClickListener(this);
        this.recyclerViewTripDetails.setAdapter(this.detailsAdapter);
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.recyclerViewTripDetails.setLayoutManager(mLayoutManager);
        this.recyclerViewTripDetails.setHasFixedSize(true);
    }

    private void showTripDetails() {
        this.initAdapter(this.lift.getTrip());
        this.showTripDetails(this.lift.getTrip(), this.lift.getStatus());
    }

    public void cancelRequest(View view) {
        if (checkClickTime()) return;
        MaterialDialogHelper.createDialog(PassengerTripDetailsActivity.this, R.string.cancel_request, R.string.cancel_request_confirmation_text, R.string.yes, R.string.no, this).show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        showProgressDialog();
        executeUpdateLift();
    }

    private void executeUpdateLift() {
        this.lift.setStatus(LiftStatus.CANCELLED);

        UpdatePassengerTripTask updatePassengerTripTask = new UpdatePassengerTripTask(this);
        updatePassengerTripTask.execute(this.lift);
    }

    public void onLiftCancelled(Lift lift) {
        ChatController.getInstance().removeContact(lift.getDriverID());
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClickListener(View v, int position) {
        Step step = (Step) this.detailsAdapter.getItem(position);

        if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING)) {
            Car car = this.lift.getTrip().retrieveCar(step.getPrivateTransport().getCar().getId());
            User driver = this.lift.getTrip().retrieveDriver(step.getPrivateTransport().getDriver().getId());

            HashMap<String, Serializable> params = new HashMap<>();
            params.put(DriverDetailsActivity.DRIVER_DETAILS_KEY, driver);
            params.put(DriverDetailsActivity.CAR_DETAILS_KEY, car);

            ActivityUtils.openActivityWithObjectParams(this, DriverDetailsActivity.class, params);
        }
    }

    public void showTripDetails(Trip trip, LiftStatus liftStatus) {

        try {
            switch (liftStatus) {
                case ACTIVE:
                    this.textViewLiftStatus.setText(getString(R.string.active).toUpperCase());
                    this.textViewDriverStatusRequest.append(getString(R.string.lift_status_accepted_message));

                    this.imageViewStatus.setVisibility(View.VISIBLE);
                    this.imageViewStatusGif.setVisibility(View.VISIBLE);
                    this.imageViewStatus.setImageResource(R.drawable.shape_accepted_gif_mask);
                    this.imageViewStatusGif.setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_ATOP);

                    this.btnCancelLift.setVisibility(View.VISIBLE);
                    break;
                case CANCELLED:
                    this.textViewLiftStatus.setText(getString(R.string.cancelled).toUpperCase());
                    this.textViewDriverStatusRequest.setVisibility(View.GONE);
                    break;
                case COMPLETED:
                    initHeaderTripCompleted(trip);
                    initFeedbackUI();
                    initFeedbackEvents();
                    this.createFeedbackTask = new CreateFeedbackTask(this);
                    break;
                case PENDING:
                    this.textViewLiftStatus.setText(getString(R.string.pending).toUpperCase());
                    this.textViewDriverStatusRequest.append(getString(R.string.lift_status_pending_message));

                    this.imageViewStatus.setVisibility(View.VISIBLE);
                    this.imageViewStatusGif.setVisibility(View.VISIBLE);

                    this.imageViewStatus.setImageResource(R.drawable.shape_pending_gif_mask);
                    this.imageViewStatusGif.setColorFilter(ContextCompat.getColor(this, R.color.accent_color), PorterDuff.Mode.SRC_ATOP);

                    this.btnCancelLift.setVisibility(View.VISIBLE);
                    break;
                case REFUSED:
                    this.textViewLiftStatus.setText(getString(R.string.declined).toUpperCase());
                    this.textViewDriverStatusRequest.append(getString(R.string.lift_status_decline_message));
                    this.textViewDriverStatusRequest.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    break;
                case REVIEWED:
                    initHeaderTripCompleted(trip);
                    break;
            }

            this.textViewTime.setText(String.format("%s min", trip.getTripDurationInMinutes()));
            this.textViewTotalPrice.setText(String.format("%s%s", trip.getPrice().getCurrency().getSymbol(), trip.getPrice().getAmountTwoDecimals()));
            this.textViewDriverStatusRequest.setText(String.format("%s ", trip.retrieveDriver(this.lift.getDriverID()).getName()));

            this.recyclerViewTripDetails.setVisibility(View.VISIBLE);
            if ((liftStatus != LiftStatus.COMPLETED) && (liftStatus != LiftStatus.REVIEWED))
                this.relativeLayoutTripStatusContainer.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            Log.e(getTagFromActivity(this), "Unable to show Trip Details...", e);
        }
    }

    private void initHeaderTripCompleted(Trip trip) {
        findViewById(R.id.layout_trip_status_container).setVisibility(View.GONE);
        findViewById(R.id.complete_trip_status_header_container_layout).setVisibility(View.VISIBLE);
        ((TextViewCustom) findViewById(R.id.total_time_complete_text)).setText(String.format("%s min", trip.getTripDurationInMinutes()));
        ((TextViewCustom) findViewById(R.id.total_price_complete_price)).setText(String.format("%s%s", trip.getPrice().getCurrency().getSymbol(), trip.getPrice().getAmountTwoDecimals()));
    }

    private void initFeedbackUI() {
        View addPreviewRatingView = findViewById(R.id.activity_passenger_trip_details_box_add_preview_rating_layout);
        addPreviewRatingView.setVisibility(View.VISIBLE);
        addPreviewRatingView.findViewById(R.id.box_add_rating_info_text).setVisibility(View.VISIBLE);

        TextViewCustom previewRatingTitleText = (TextViewCustom) addPreviewRatingView.findViewById(R.id.box_add_rating_title_text);
        previewRatingTitleText.setText(String.format(getString(R.string.question_rating_title), User.getFirstNameFromCompleteName(getDriver().getName())));

        this.starRatingPreview1 = (ImageView) addPreviewRatingView.findViewById(R.id.box_add_rating_star_preview_1);
        this.starRatingPreview2 = (ImageView) addPreviewRatingView.findViewById(R.id.box_add_rating_star_preview_2);
        this.starRatingPreview3 = (ImageView) addPreviewRatingView.findViewById(R.id.box_add_rating_star_preview_3);
        this.starRatingPreview4 = (ImageView) addPreviewRatingView.findViewById(R.id.box_add_rating_star_preview_4);
        this.starRatingPreview5 = (ImageView) addPreviewRatingView.findViewById(R.id.box_add_rating_star_preview_5);

        this.addRatingLayout = findViewById(R.id.activity_passenger_trip_details_add_rating_layout);

        for (int i = 0; i < 5; i++) {
            this.imageViewsStarRating[i] = (ImageView) this.addRatingLayout.findViewById(this.idsStarRating[i]);
        }

        this.nameReviewedText = (TextViewCustom) this.addRatingLayout.findViewById(R.id.add_rating_layout_name_reviewed_text);
        this.feedbackBoxContainerLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_feedback_container_linear_layout);
        this.sendFeedbackButton = (Button) this.addRatingLayout.findViewById(R.id.add_rating_layout_send_button);
        this.sendFeedbackButton.setVisibility(View.VISIBLE);
        this.feedbackEditText = (EditText) this.addRatingLayout.findViewById(R.id.add_rating_layout_feedback_edit_text);
        this.starsLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_stars_layout);
        this.starsLayout.setVisibility(View.VISIBLE);
        this.insertReviewLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_add_feedback_layout);
        this.insertReviewLayout.setVisibility(View.VISIBLE);

        this.askSurveyTextLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_ask_survey_text_layout);
        this.askSurveyTextLayout.setVisibility(View.GONE);
        this.askSurveyButtonsLayout = this.addRatingLayout.findViewById(R.id.add_rating_layout_buttons_survey_layout);
        this.askSurveyButtonsLayout.setVisibility(View.GONE);

        this.askGoToSurveyText = (TextViewCustom) this.addRatingLayout.findViewById(R.id.add_rating_layout_ask_survey_text);

        this.goToSurveyOkButton = (Button) this.addRatingLayout.findViewById(R.id.add_rating_layout_survey_ok_button);
        this.goToSurveyNotNowButton = (Button) this.addRatingLayout.findViewById(R.id.add_rating_layout_survey_not_now_button);

        ImageView imageViewDriver = (ImageView) this.addRatingLayout.findViewById(R.id.passenger_details_rating_imageview);

        if (this.getDriver().getUserPictures() != null && !this.getDriver().getUserPictures().isEmpty())
            PicassoHelper.loadMedia(this, imageViewDriver, this.getDriver().getUserPictures().get(0).getMediaUri(), R.drawable.img_default_feedback_avatar, true);
        else
            PicassoHelper.loadPictureByUserID(this, imageViewDriver, this.getDriver().getId(), R.drawable.img_default_feedback_avatar, R.drawable.img_default_feedback_avatar, true);
    }

    private void initFeedbackEvents() {
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

    private void showAddFeedbackLayout(int previewStar) {
        this.createFeedbackTask.setFeedbackRating(previewStar);
        this.addRatingLayout.setVisibility(View.VISIBLE);

        this.sendFeedbackButton.setVisibility(View.VISIBLE);
        this.starsLayout.setVisibility(View.VISIBLE);
        this.insertReviewLayout.setVisibility(View.VISIBLE);

        this.askSurveyTextLayout.setVisibility(View.GONE);
        this.askSurveyButtonsLayout.setVisibility(View.GONE);

        this.nameReviewedText.setText(this.getDriver().getName());

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

    @Override
    public void onFeedbackSuccess(Feedback feedbackCreated) {
        this.feedbackCreated = feedbackCreated;

        this.hideAddFeedbackLayout();
        findViewById(R.id.activity_passenger_trip_details_box_add_preview_rating_layout).setVisibility(View.GONE);

        HashMap<String, Serializable> params = new HashMap<>();
        params.put(RatingSurveyActivity.FEEDBACK_CREATED_KEY, this.feedbackCreated);
        ActivityUtils.openActivityWithObjectParams(this, RatingSurveyActivity.class, params);
    }

    public String getFeedbackText() {
        return this.feedbackEditText.getText().toString();
    }

    public Lift getLift() {
        return lift;
    }

    @Override
    public User getReviewed() {
        return getDriver();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.activity_passenger_trip_details_add_rating_layout:
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

    private User getDriver() {
        return this.lift.getTrip().getDriverStep().getPrivateTransport().getDriver();
    }
}
