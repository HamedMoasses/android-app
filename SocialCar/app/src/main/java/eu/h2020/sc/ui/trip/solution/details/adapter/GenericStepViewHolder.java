package eu.h2020.sc.ui.trip.solution.details.adapter;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Step;
import eu.h2020.sc.domain.TimeSpacePoint;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.MaterialRippleLayout;
import eu.h2020.sc.utils.PicassoHelper;

public class GenericStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RelativeLayout layoutStepDeparturePoint;
    private MaterialRippleLayout layoutStepTravelMode;
    private ImageView imageViewDeparturePoint;
    private ImageView imageViewArrivingPoint;
    private ImageView imageViewTransportMode;
    private ImageView imageViewDriver;
    private ImageView imageViewMoreDetails;
    private TextView textViewDeparturePoint;
    private TextView textViewArrivingPoint;
    private TextView textViewWayPointAddress;
    private TextView textViewStepDistance;
    private TextView textViewStepDuration;
    private TextView textViewStartDepartureDate;
    private TextView textViewStepDepartureDate;
    private RecyclerViewItemClickListener onItemClickListener;
    private int itemPosition;
    private boolean imgDriverLoaded;

    public GenericStepViewHolder(View itemView) {
        super(itemView);

        this.layoutStepDeparturePoint = (RelativeLayout) itemView.findViewById(R.id.step_departure_point);
        this.layoutStepTravelMode = (MaterialRippleLayout) itemView.findViewById(R.id.row_transport_mode);

        this.imageViewDeparturePoint = (ImageView) itemView.findViewById(R.id.image_view_step_departure_point);
        this.imageViewArrivingPoint = (ImageView) itemView.findViewById(R.id.image_view_step_arriving_point);
        this.imageViewTransportMode = (ImageView) itemView.findViewById(R.id.image_view_transport_mode);
        this.imageViewDriver = (ImageView) itemView.findViewById(R.id.image_view_driver_avatar);
        this.imageViewMoreDetails = (ImageView) itemView.findViewById(R.id.image_view_more_details);

        this.textViewStartDepartureDate = (TextView) itemView.findViewById(R.id.text_view_start_departure);
        this.textViewDeparturePoint = (TextView) itemView.findViewById(R.id.text_view_departure_point);
        this.textViewArrivingPoint = (TextView) itemView.findViewById(R.id.text_view_arriving_point);

        this.textViewWayPointAddress = (TextView) itemView.findViewById(R.id.text_view_step_waypoint);
        this.textViewStepDistance = (TextView) itemView.findViewById(R.id.text_view_step_distance);
        this.textViewStepDuration = (TextView) itemView.findViewById(R.id.text_view_step_duration);
        this.textViewStepDepartureDate = (TextView) itemView.findViewById(R.id.text_view_step_departure);

        this.imgDriverLoaded = false;
    }

    public void init(Trip trip, Object o) {

        this.itemPosition = trip.getSteps().indexOf(o);

        Step step = (Step) o;

        step = resolveUnknownAddress(step);

        this.layoutStepTravelMode.setTag(step);
        this.layoutStepTravelMode.setOnClickListener(this);

        if (trip.getSteps().size() == 1) {

            this.prepareTripDetailsWithOnlyOneStep(step);

        } else {

            if (itemPosition == 0) {
                Step nextStep = trip.getSteps().get(itemPosition + 1);
                this.injectStartTripStep(step, nextStep);

            } else if (itemPosition == trip.getSteps().size() - 1) {
                this.injectEndTripStep(step);

            } else
                this.initGenericStep(step);
        }

    }

    private Step resolveUnknownAddress(Step step) {
        try {
            TimeSpacePoint startPoint = step.getRoute().getStartPoint();
            TimeSpacePoint endPoint = step.getRoute().getEndPoint();
            Geocoder geoCoder = new Geocoder(SocialCarApplication.getContext());

            String startAddress = startPoint.getAddress().trim().toLowerCase();
            if (startAddress.isEmpty() || startAddress.contains("null") || startAddress.contains("unknown")) {
                List<Address> matches = null;
                matches = geoCoder.getFromLocation(startPoint.getPoint().getLat(), startPoint.getPoint().getLon(), 1);
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                if (bestMatch != null) {
                    startPoint.setAddress(bestMatch.getAddressLine(0));
                }

            }
            String endAddress = endPoint.getAddress().trim().toLowerCase();
            if (endAddress.isEmpty() || endAddress.contains("null") || endAddress.contains("unknown")) {
                List<Address> matches = null;
                matches = geoCoder.getFromLocation(endPoint.getPoint().getLat(), endPoint.getPoint().getLon(), 1);
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                if (bestMatch != null) {
                    endPoint.setAddress(bestMatch.getAddressLine(0));
                }
            }

        } catch (Exception e) {
            return step;
        }
        return step;
    }

    private void prepareTripDetailsWithOnlyOneStep(Step step) {
        this.textViewDeparturePoint.setText(step.getRoute().getStartPoint().getAddress());
        this.imageViewDeparturePoint.setImageDrawable(this.getResourceImageId(R.drawable.img_details_departure_point));
        this.injectTransportStep(step);
        this.textViewArrivingPoint.setText(step.getRoute().getEndPoint().getAddress());
        this.imageViewArrivingPoint.setImageDrawable(this.getResourceImageId(R.drawable.img_details_arriving_point));
    }

    private void injectStartTripStep(Step step, Step nextStep) {
        this.textViewStartDepartureDate.setText(DateUtils.hourLongToString(step.getRoute().getStartPoint().getDate(), DateFormat.is24HourFormat(this.itemView.getContext())));
        this.textViewDeparturePoint.setText(step.getRoute().getStartPoint().getAddress());
        this.imageViewDeparturePoint.setImageDrawable(this.getResourceImageId(R.drawable.img_details_departure_point));
        this.injectTransportStep(step);
        this.textViewArrivingPoint.setText(nextStep.getRoute().getStartPoint().getAddress());
    }

    private void injectEndTripStep(Step step) {
        this.layoutStepDeparturePoint.setVisibility(View.GONE);
        this.imageViewArrivingPoint.setImageDrawable(this.getResourceImageId(R.drawable.img_details_arriving_point));
        this.injectTransportStep(step);
        this.textViewArrivingPoint.setText(step.getRoute().getEndPoint().getAddress());
    }

    private void initGenericStep(Step step) {
        this.layoutStepDeparturePoint.setVisibility(View.GONE);
        this.injectTransportStep(step);
        this.textViewArrivingPoint.setText(step.getRoute().getEndPoint().getAddress());
    }

    private void injectTransportStep(Step step) {

        this.textViewDeparturePoint.setText(step.getRoute().getStartPoint().getAddress());

        if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING)) {
            this.initDriverStep(step);

            if (step.getPrivateTransport().getPublicURI() == null) {
                this.loadDriverImage(step.getPrivateTransport().getDriver());
            }

        } else if (step.getTransport().getTravelMode().equals(TravelMode.FEET)) {
            this.initWalkStep(step);

        } else if (step.isPublicTransportStep()) {
            this.injectPublicTransportStep(step);
        }

    }

    private void initWalkStep(Step step) {
        this.textViewWayPointAddress.setText(String.format(itemView.getContext().getString(R.string.walk)));
        this.textViewStepDistance.setText(step.getDistance());

        this.itemView.findViewById(R.id.image_view_single_left_circle).setVisibility(View.GONE);

        this.checkStepDuration(step);

        this.imageViewDriver.setVisibility(View.GONE);
        this.imageViewTransportMode.setImageDrawable(step.getTransportIcon(itemView.getContext(), step));
        this.layoutStepTravelMode.setEnabled(false);
    }

    private void initDriverStep(Step step) {
        this.textViewWayPointAddress.setText(step.getPrivateTransport().getDriver().getName());
        this.textViewStepDistance.setText(step.getDistance());
        this.textViewStepDepartureDate.setText(String.format(itemView.getContext().getString(R.string.departs_at), DateUtils.hourLongToString((step.getRoute().getStartPoint().getDate()), DateFormat.is24HourFormat(this.itemView.getContext()))));
        this.textViewStepDuration.setText(String.format("%s %s", String.valueOf(step.getDurationInMin()), itemView.getContext().getString(R.string.min)));
        this.imageViewTransportMode.setImageDrawable(step.getTransportIcon(itemView.getContext(), step));

        if (step.getPrivateTransport().getPublicURI() == null) {
            this.imageViewTransportMode.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.primary_color));
            this.imageViewDriver.setVisibility(View.VISIBLE);
            this.layoutStepTravelMode.setEnabled(true);
            this.imageViewMoreDetails.setVisibility(View.VISIBLE);
        } else {
            this.layoutStepTravelMode.setEnabled(false);
        }
    }

    private void loadDriverImage(User driver) {
        if (!this.imgDriverLoaded) {
            if (driver.getUserPictures() != null && !driver.getUserPictures().isEmpty()) {
                PicassoHelper.loadMedia(itemView.getContext(), this.imageViewDriver, driver.getUserPictures().get(0).getMediaUri(), R.drawable.img_default_feedback_avatar, true);
                this.imgDriverLoaded = true;
            } else {
                this.imageViewDriver.setImageResource(R.drawable.img_default_feedback_avatar);
            }
        }
    }

    private void injectPublicTransportStep(Step step) {
        this.textViewWayPointAddress.setText(step.getPublicTransport().getLongName());
        this.textViewStepDistance.setText(step.getDistance());
        this.textViewStepDepartureDate.setText(String.format(itemView.getContext().getString(R.string.departs_at), DateUtils.hourLongToString((step.getRoute().getStartPoint().getDate()), DateFormat.is24HourFormat(this.itemView.getContext()))));

        this.checkStepDuration(step);

        this.imageViewDriver.setVisibility(View.GONE);
        this.imageViewTransportMode.setImageDrawable(step.getTransportIcon(itemView.getContext(), step));
        this.layoutStepTravelMode.setEnabled(false);
    }

    private void checkStepDuration(Step step) {
        int stepDuration = step.getDurationInMin();
        if (stepDuration > 0) {
            this.textViewStepDuration.setText(String.format("%s %s", String.valueOf(stepDuration), itemView.getContext().getString(R.string.min)));
        } else {
            this.textViewStepDuration.setVisibility(View.GONE);
            this.itemView.findViewById(R.id.image_view_single_circle).setVisibility(View.GONE);
            this.itemView.findViewById(R.id.tv_estimated_time).setVisibility(View.GONE);
        }
    }


    private Drawable getResourceImageId(int resource) {
        return ContextCompat.getDrawable(itemView.getContext(), resource);
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClickListener(v, itemPosition);
    }
}