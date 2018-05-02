package eu.h2020.sc.ui.ride.myrides;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.map.OsmMaps;
import eu.h2020.sc.ui.commons.RecyclerViewItemCheckedListener;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.utils.AnimationHelper;
import eu.h2020.sc.utils.DimensionUtils;
import eu.h2020.sc.utils.PicassoHelper;
import eu.h2020.sc.utils.PolylineEncoding;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MyRidesAdapter.class.getName();

    private OsmMaps osmMaps;
    private Context context;
    private Activity activity;

    private List<Ride> rides;
    private View view;
    private int width;

    private RecyclerViewItemClickListener deleteItemClickListener;
    private RecyclerViewItemCheckedListener recyclerViewItemCheckedListener;
    private RecyclerViewRideClickListener viewRideClickListener;

    public MyRidesAdapter(List<Ride> rides, Context context, Activity activity) {
        this.rides = rides;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public MyRidesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_card_layout, viewGroup, false);
        MyRidesViewHolder holder = new MyRidesViewHolder(this.view);
        this.width = getScreenSize(context).x;
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyRidesViewHolder myRidesViewHolder, int position) {
        AnimationHelper.runEnterAnimationRow(myRidesViewHolder.itemView, position);

        position = myRidesViewHolder.getAdapterPosition();

        Ride ride = this.rides.get(position);

        myRidesViewHolder.card.setLayoutParams(new LinearLayout.LayoutParams(this.width, ViewGroup.LayoutParams.WRAP_CONTENT));

        myRidesViewHolder.viewOnSwitch.setVisibility(View.GONE);
        myRidesViewHolder.viewOnSwitch.setTag(ride);
        myRidesViewHolder.viewOnSwitch.setOnClickListener(this);
        this.setToggleClick(myRidesViewHolder, ride);

        myRidesViewHolder.textViewTitle.setText(ride.getName());

        String tripDate = DateFormat.getDateFormat(this.context).format(ride.getRoute().getStartPoint().getDate());
        String tripTime = DateFormat.getTimeFormat(this.context).format(ride.getRoute().getStartPoint().getDate());
        myRidesViewHolder.textViewSubTitle.setText(String.format("%s %s %s", tripDate, this.context.getResources().getString(R.string.at_date), tripTime));

        myRidesViewHolder.textViewDeleteTrip.setTag(ride);
        myRidesViewHolder.textViewDeleteTrip.setOnClickListener(this);

        myRidesViewHolder.rideSwitch.setTag(ride);
        this.setTripEnabled(myRidesViewHolder, ride);

        myRidesViewHolder.imageViewExpandMap.setImageResource(R.drawable.ic_chevron_down);
        myRidesViewHolder.imageViewExpandMap.setTag(ride);
        myRidesViewHolder.imageViewExpandMap.setOnClickListener(this);

        this.showRideMessage(myRidesViewHolder, position, ride);
        this.showToolTip(myRidesViewHolder, position);

        Configuration.getInstance().load(this.activity, PreferenceManager.getDefaultSharedPreferences(this.activity));

        this.osmMaps = new OsmMaps((MapView) this.view.findViewById(R.id.mapView), this.context);
        this.updateMapContents(ride);

        View.OnClickListener visibilityOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myRidesViewHolder.mapView.getVisibility() == View.VISIBLE) {
                    myRidesViewHolder.mapView.setVisibility(View.GONE);
                    myRidesViewHolder.imageViewExpandMap.setImageResource(R.drawable.ic_chevron_down);
                } else {
                    myRidesViewHolder.mapView.setVisibility(View.VISIBLE);
                    myRidesViewHolder.imageViewExpandMap.setImageResource(R.drawable.ic_chevron_up);
                }

            }
        };
        myRidesViewHolder.imageViewExpandMap.setOnClickListener(visibilityOnClickListener);

        this.displayPassengers(myRidesViewHolder, ride);
    }

    private void updateMapContents(Ride ride) {

        Configuration.getInstance().load(this.activity, PreferenceManager.getDefaultSharedPreferences(this.activity));

        this.osmMaps.clear();

        final GeoPoint startPoint = new GeoPoint(ride.getRoute().getStartPoint().getPoint().getLat(), ride.getRoute().getStartPoint().getPoint().getLon());
        GeoPoint endPoint = new GeoPoint(ride.getRoute().getEndPoint().getPoint().getLat(), ride.getRoute().getEndPoint().getPoint().getLon());

        Polyline polylineBorder = this.osmMaps.addBorderPolyline(PolylineEncoding.decodeOSM(ride.getPolyline()));
        polylineBorder.setColor(ContextCompat.getColor(this.context, R.color.polyline_selected_border));

        Polyline polylineBody = this.osmMaps.addBodyPolyline(PolylineEncoding.decodeOSM(ride.getPolyline()));
        polylineBody.setColor(ContextCompat.getColor(this.context, R.color.polyline_selected_body));

        this.osmMaps.addPolylineEdge(polylineBody);

        this.osmMaps.addMarker(startPoint.getLatitude(), startPoint.getLongitude(), R.mipmap.ic_starting_point_card, false);
        this.osmMaps.addMarker(endPoint.getLatitude(), endPoint.getLongitude(), R.mipmap.ic_destination_point_card, false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                osmMaps.moveCamera(startPoint);
            }
        }, 800);
    }

    /**
     * @param myRidesViewHolder
     * @param ride
     */
    private void setToggleClick(MyRidesViewHolder myRidesViewHolder, Ride ride) {
        if (!ride.retrievePassengerIDs().isEmpty() && ride.isActivated()) {
            myRidesViewHolder.viewOnSwitch.setVisibility(View.VISIBLE);
        } else {
            myRidesViewHolder.viewOnSwitch.setVisibility(View.GONE);
        }
    }

    /**
     * @param myRidesViewHolder
     * @param ride
     */
    private void setTripEnabled(MyRidesViewHolder myRidesViewHolder, Ride ride) {
        myRidesViewHolder.rideSwitch.setOnCheckedChangeListener(null);

        if (ride.isActivated())
            myRidesViewHolder.rideSwitch.setChecked(true);
        else
            myRidesViewHolder.rideSwitch.setChecked(false);

        myRidesViewHolder.rideSwitch.setOnCheckedChangeListener(this);
    }

    /**
     * Display message "the passenger avatar, will be displayed in this area"
     * only if the first ride hasn't passengers avatar
     *
     * @param myRidesViewHolder
     * @param position
     * @param ride
     */
    private void showRideMessage(MyRidesViewHolder myRidesViewHolder, int position, Ride ride) {
        if (position == 0 && !ride.hasOneActiveLift()) {
            myRidesViewHolder.rideMessage.setVisibility(View.VISIBLE);
        } else
            myRidesViewHolder.rideMessage.setVisibility(View.GONE);
    }

    /**
     * Display tooltip message only if there is one ride
     *
     * @param myRidesViewHolder
     * @param position
     */
    private void showToolTip(MyRidesViewHolder myRidesViewHolder, int position) {
        if (position == 0 && this.rides.size() == 1) {
            myRidesViewHolder.textViewCreateRide.setVisibility(View.VISIBLE);
        } else {
            myRidesViewHolder.textViewCreateRide.setVisibility(View.GONE);
        }
    }

    /**
     * Display passengers avatar
     *
     * @param myRidesViewHolder
     * @param ride
     */
    private void displayPassengers(MyRidesViewHolder myRidesViewHolder, Ride ride) {

        if (myRidesViewHolder.passengerAvatarLayout.getChildCount() > 0)
            myRidesViewHolder.passengerAvatarLayout.removeAllViews();

        float density = this.context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DimensionUtils.obtainRideAvatarDimension(density), DimensionUtils.obtainRideAvatarDimension(density));
        params.setMargins(8, 8, 8, 8);
        params.gravity = Gravity.CENTER;

        if (ride.getLifts().isEmpty()) {
            myRidesViewHolder.passengerAvatarLayout.setVisibility(View.GONE);
            return;
        }

        for (Lift lift : ride.getLifts()) {

            if (lift.isActive()) {

                ImageView imageView = new ImageView(this.context);
                imageView.setLayoutParams(params);

                String passengerImgPath = ride.retrievePassengerImagePathByLift(lift);

                if (passengerImgPath != null)
                    PicassoHelper.loadMedia(this.context, imageView, passengerImgPath, R.drawable.img_default_ride_avatar, true);
                else
                    PicassoHelper.loadPictureByUserID(this.context, imageView, lift.getDriverID(), R.drawable.img_default_ride_avatar, R.drawable.img_default_ride_avatar, true);

                myRidesViewHolder.passengerAvatarLayout.addView(imageView);
            }
        }
    }

    private Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new Point(size.x, size.y);
    }

    public void setDeleteItemClickListener(RecyclerViewItemClickListener deleteItemClickListener) {
        this.deleteItemClickListener = deleteItemClickListener;
    }

    public void setViewRideClickListener(RecyclerViewRideClickListener viewRideClickListener) {
        this.viewRideClickListener = viewRideClickListener;
    }

    public void setRecycleViewItemCheckedListener(RecyclerViewItemCheckedListener recycleViewItemCheckedListener) {
        this.recyclerViewItemCheckedListener = recycleViewItemCheckedListener;
    }

    public void refreshRecyclerView(List<Ride> myRides) {
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.rides.clear();
        this.rides.addAll(myRides);
        this.notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void removeItem(int pos) {
        this.rides.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addItem(Ride item, int position) {
        this.rides.add(position, item);
        notifyItemInserted(position);
    }

    public void updateItem(Ride item, int position) {
        this.rides.set(position, item);
        notifyItemChanged(position);
    }

    @Override
    public void onClick(View v) {
        Ride item = (Ride) v.getTag();
        int position = rides.indexOf(item);

        switch (v.getId()) {
            case R.id.delete_trip_action:
                deleteItemClickListener.onItemClickListener(v, position);
                break;
            case R.id.viewOnSwitch:
                viewRideClickListener.onItemClickListener(v, position, position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Ride item = (Ride) buttonView.getTag();
        int position = rides.indexOf(item);
        recyclerViewItemCheckedListener.onItemCheckedListener(buttonView, position, isChecked);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }
}

