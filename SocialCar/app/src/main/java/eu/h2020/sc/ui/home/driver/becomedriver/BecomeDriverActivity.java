package eu.h2020.sc.ui.home.driver.becomedriver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.CarUsagePreferences;
import eu.h2020.sc.domain.LuggageType;
import eu.h2020.sc.utils.CropImageHelper;
import eu.h2020.sc.utils.DimensionUtils;
import eu.h2020.sc.utils.ImagePicker;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.WidgetHelper;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

public class BecomeDriverActivity extends GeneralActivity implements TextWatcher, MaterialDialog.ListCallbackSingleChoice {

    private static final int PICK_IMAGE_REQUEST_CODE = 100;

    private TextView textViewFoodAllowed;
    private TextView textViewSmokingAllowed;
    private TextView textViewMusicAllowed;
    private TextView textViewAirConditioning;
    private TextView textViewPetsAllowed;
    private TextView textViewLuggageAllowed;
    private TextView textViewChildSeat;
    private TextView textViewColor;
    private ImageView imageViewCarImage;

    private EditText editTextModel;
    private EditText editTextCarPlate;
    private EditText editTextSeat;
    private View viewChooseColor;
    private MaterialDialog materialDialog;

    private int layoutClickedId;
    private Bitmap selectedPictureCar;
    private CropImageHelper cropImageHelper;

    private CarUsagePreferences carUsagePreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_driver);

        this.cropImageHelper = new CropImageHelper();

        this.initUI();
        this.initEvents();

        this.initCarUsagePreferences();
    }

    private void initCarUsagePreferences() {
        this.carUsagePreferences = new CarUsagePreferences();
        this.carUsagePreferences.setLuggageType(LuggageType.MEDIUM);
    }

    private void initUI() {
        setTitle(getString(R.string.add_car));

        initToolBar(false);
        initBack();

        textViewFoodAllowed = (TextView) findViewById(R.id.textViewFoodAllowed);
        textViewSmokingAllowed = (TextView) findViewById(R.id.textViewSmokingAllowed);
        textViewMusicAllowed = (TextView) findViewById(R.id.textViewMusicAllowed);
        textViewAirConditioning = (TextView) findViewById(R.id.textViewAirConditioning);
        textViewPetsAllowed = (TextView) findViewById(R.id.textViewPetsAllowed);
        textViewLuggageAllowed = (TextView) findViewById(R.id.textViewLuggageAllowed);
        textViewChildSeat = (TextView) findViewById(R.id.textViewChildSeat);
        textViewColor = (TextView) findViewById(R.id.textViewColor);

        imageViewCarImage = (ImageView) findViewById(R.id.signup_driver_car_image_view);

        editTextModel = (EditText) findViewById(R.id.editTextModel);
        Drawable carDrawable = setDrawableTint(R.drawable.ic_car_24dp, R.color.medium_grey);
        editTextModel.setCompoundDrawablesWithIntrinsicBounds(carDrawable, null, null, null);

        editTextCarPlate = (EditText) findViewById(R.id.editTextCarPlate);
        editTextSeat = (EditText) findViewById(R.id.editTextSeat);

        viewChooseColor = findViewById(R.id.viewChoosedColor);
    }

    private void initEvents() {
        editTextModel.addTextChangedListener(this);
        editTextCarPlate.addTextChangedListener(this);
        editTextSeat.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_car, menu);
        MenuItem item = menu.findItem(R.id.action_add_car);
        item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_add_car) {
            BecomeDriverTask becomeDriverTask = new BecomeDriverTask(this);
            Car car = makeCar();

            if (becomeDriverTask.validateCar(car)) {
                hideSoftKeyboard();
                showProgressDialog();
                becomeDriverTask.execute(car);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Car makeCar() {
        String model = editTextModel.getText().toString().trim();
        String plate = editTextCarPlate.getText().toString().trim();
        String seats = editTextSeat.getText().toString();

        Car car = new Car(plate);
        car.setModel(model);

        if (!seats.equals("")) {
            car.setSeats(Integer.parseInt(editTextSeat.getText().toString()));
        }

        ColorDrawable colorView = (ColorDrawable) viewChooseColor.getBackground();
        car.setColour(String.format("#%s", Integer.toHexString(colorView.getColor())));

        this.carUsagePreferences.setSmokingAllowed(parseBooleanFromString(textViewSmokingAllowed.getText().toString()));
        this.carUsagePreferences.setMusicAllowed(parseBooleanFromString(textViewMusicAllowed.getText().toString()));
        this.carUsagePreferences.setAirConditioning(parseBooleanFromString(textViewAirConditioning.getText().toString()));
        this.carUsagePreferences.setChildSeat(parseBooleanFromString(textViewChildSeat.getText().toString()));
        this.carUsagePreferences.setFoodAllowed(parseBooleanFromString(textViewFoodAllowed.getText().toString()));

        this.carUsagePreferences.setPetsAllowed(parseBooleanFromString(textViewPetsAllowed.getText().toString()));
        car.setCarUsagePreferences(carUsagePreferences);

        return car;
    }

    private boolean parseBooleanFromString(String value) {
        return value.equals(getString(R.string.yes));
    }

    public void showDialogCarPref(View view) {
        int idTitleResource = 0;
        String[] arrayValueAllowed = null;

        this.layoutClickedId = view.getId();

        switch (layoutClickedId) {
            case R.id.layoutFoodAllowed:
                idTitleResource = R.string.food_allowed;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.layoutSmokingAllowed:
                idTitleResource = R.string.smoking_allowed;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.layoutMusicAllowed:
                idTitleResource = R.string.music_allowed;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.layoutAirConditioning:
                idTitleResource = R.string.air_conditioning;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.layoutPetsAllowed:
                idTitleResource = R.string.pets_allowed;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.layoutLuggageAllowed:
                idTitleResource = R.string.luggage_allowed;
                arrayValueAllowed = getResources().getStringArray(R.array.array_dialog_luggage_size);
                break;
            case R.id.layoutChildSeat:
                idTitleResource = R.string.child_seat;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
        }

        MaterialDialogHelper.createSingleChoice(this, idTitleResource, arrayValueAllowed, R.string.choose, this).show();
    }

    public void showDialogColour(View view) {
        materialDialog = MaterialDialogHelper.createCustomViewDialog(this, R.string.select_colour, R.layout.custom_color_picker);
        materialDialog.show();
    }

    public void pickColour(View view) {
        textViewColor.setVisibility(View.GONE);
        viewChooseColor.setVisibility(View.VISIBLE);
        viewChooseColor.setBackgroundColor(Color.parseColor((String) view.getTag()));
        materialDialog.dismiss();
    }

    @Override
    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

        switch (layoutClickedId) {
            case R.id.layoutFoodAllowed:
                textViewFoodAllowed.setText(text);
                break;
            case R.id.layoutSmokingAllowed:
                textViewSmokingAllowed.setText(text);
                break;
            case R.id.layoutMusicAllowed:
                textViewMusicAllowed.setText(text);
                break;
            case R.id.layoutAirConditioning:
                textViewAirConditioning.setText(text);
                break;
            case R.id.layoutPetsAllowed:
                textViewPetsAllowed.setText(text);
                break;
            case R.id.layoutLuggageAllowed:
                this.resetLuggageType(which);
                textViewLuggageAllowed.setText(text);
                break;
            case R.id.layoutChildSeat:
                textViewChildSeat.setText(text);
                break;
        }

        return false;
    }

    private void resetLuggageType(int index) {
        switch(index){
            case 0:
                this.carUsagePreferences.setLuggageType(LuggageType.SMALL);
                break;
            case 1:
                this.carUsagePreferences.setLuggageType(LuggageType.MEDIUM);
                break;
            case 2:
                this.carUsagePreferences.setLuggageType(LuggageType.LARGE);
                break;
            case 3:
                this.carUsagePreferences.setLuggageType(LuggageType.NONE);
                break;
            default:
                this.carUsagePreferences.setLuggageType(LuggageType.NONE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (editTextModel.isFocused()) {
            Drawable carDrawable = setDrawableTint(R.drawable.ic_car_24dp, R.color.medium_grey);
            editTextModel.setCompoundDrawablesWithIntrinsicBounds(carDrawable, null, null, null);
        }
        if (editTextCarPlate.isFocused()) {
            editTextCarPlate.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_car_plate_24dp), null, null, null);
        }
        if (editTextSeat.isFocused()) {
            editTextSeat.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.seat_legroom_normal), null, null, null);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void showModelMarkerError() {
        editTextModel.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_car_24dp), null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    public void showPlateMarkerError() {
        editTextCarPlate.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_car_plate_24dp), null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    public void showSeatsMarkerError() {
        editTextSeat.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.seat_legroom_normal), null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    public void showColourMarkerError() {
        textViewColor.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    public Bitmap getSelectedPictureCar() {
        return selectedPictureCar;
    }

    public void closeActivity() {
        this.finish();
    }

    private void startCropImageActivity(Uri imageURI) {

        float density = getResources().getDisplayMetrics().density;

        int minCropWindowWidth = DimensionUtils.obtainCropWindowWidthDimension(density);
        int minCropWindowHeight = DimensionUtils.obtainCropWindowHeightDimension(density);

        this.cropImageHelper.setMinCropSizeWindowWidth(minCropWindowWidth);
        this.cropImageHelper.setMinCropSizeWindowHeight(minCropWindowHeight);
        this.cropImageHelper.setMaxZoom(1);

        this.cropImageHelper.startCropperActivity(this, imageURI);
    }


    public void pickCarImage(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    Uri imageURI = this.cropImageHelper.getPickImageResultUri(this, data);
                    this.startCropImageActivity(imageURI);
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                try {
                    if (resultCode == Activity.RESULT_OK) {

                        Uri croppedImageURI = this.cropImageHelper.getActivityResult(data);
                        this.selectedPictureCar = this.cropImageHelper.getBitmapFromUri(croppedImageURI, this);

                        this.imageViewCarImage.setImageURI(croppedImageURI);

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        WidgetHelper.showToast(this, getString(R.string.generic_error));
                    }

                } catch (IOException e) {
                    WidgetHelper.showToast(this, getString(R.string.generic_error));
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
