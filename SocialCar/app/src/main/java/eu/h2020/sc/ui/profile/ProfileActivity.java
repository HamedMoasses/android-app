package eu.h2020.sc.ui.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.SpecialNeeds;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.TravelPreferences;
import eu.h2020.sc.domain.TravelSolutionsOptimizations;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.UserGender;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.ui.signup.DobPickerFragment;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.PicassoHelper;
import eu.h2020.sc.utils.Utils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class ProfileActivity extends GeneralActivity implements MaterialDialog.ListCallbackSingleChoice {

    private TextView textViewRating;
    private TextView textViewEmail;
    private TextView textViewPhone;
    private TextView textViewSmokingAllowed;
    private TextView textViewFoodAllowed;
    private TextView textViewMusicAllowed;
    private TextView textViewPetsAllowed;

    private TextView textViewMaxCost;
    private EditText editTextMaxCost;
    private TextView textViewPreferredTravelMode;
    private TextView textViewOptimizeTravelSolutionsBy;
    private TextView textViewCarpoolerGender;
    private TextView textViewCarpoolerAgeRange;
    private TextView textViewSpecialNeeds;
    private TextView textViewLuggageAllowed;
    private TextView textViewGpsTracking;
    private RelativeLayout relativeLayoutEmail;
    private RelativeLayout relativeLayoutName;
    private RelativeLayout relativeLayoutRating;
    private EditText editTextName;
    private EditText editTextPhone;
    private FloatingActionButton fabEditProfile;
    private ImageView imageViewUserPicture;
    private SocialCarStore socialCarStore;
    private User user;
    private boolean profileInEditMode = false;

    private TravelPreferences changedTravelPref;
    private int clickedViewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isAfterKitKatVersion())
            setTheme(R.style.Theme_SocialCar_Status_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initCollapseToolBar();

        this.socialCarStore = SocialCarApplication.getInstance();
        this.initUI();
        this.initUserInfo();
    }

    private void initUI() {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(false);

        this.fabEditProfile = (FloatingActionButton) findViewById(R.id.fab_edit_profile);

        this.textViewRating = (TextView) findViewById(R.id.text_view_rating);
        this.textViewEmail = (TextView) findViewById(R.id.text_view_email);
        this.textViewPhone = (TextView) findViewById(R.id.text_view_phone);

        this.textViewSmokingAllowed = (TextView) findViewById(R.id.text_view_smoking_allowed);
        this.textViewFoodAllowed = (TextView) findViewById(R.id.text_view_food_allowed);
        this.textViewMusicAllowed = (TextView) findViewById(R.id.text_view_music_allowed);
        this.textViewPetsAllowed = (TextView) findViewById(R.id.text_view_pets_allowed);

        this.textViewMaxCost = (TextView) findViewById(R.id.text_view_max_cost);
        this.editTextMaxCost = (EditText) findViewById(R.id.edit_text_max_cost);

        this.textViewPreferredTravelMode = (TextView) findViewById(R.id.text_view_preferred_Travel_mode);

        this.textViewOptimizeTravelSolutionsBy = (TextView) findViewById(R.id.text_view_optimize_travel_solutions_by);
        this.textViewCarpoolerGender = (TextView) findViewById(R.id.text_view_carpooler_gender);
        this.textViewCarpoolerAgeRange = (TextView) findViewById(R.id.text_view_carpooler_age_range);
        this.textViewSpecialNeeds = (TextView) findViewById(R.id.text_view_special_needs);
        this.textViewLuggageAllowed = (TextView) findViewById(R.id.text_view_luggage_allowed);
        this.textViewGpsTracking = (TextView) findViewById(R.id.text_view_gps_tracking);

        this.imageViewUserPicture = (ImageView) findViewById(R.id.user_picture);

        this.editTextName = (EditText) findViewById(R.id.edit_text_name);
        this.editTextPhone = (EditText) findViewById(R.id.edit_text_phone);

        this.relativeLayoutName = (RelativeLayout) findViewById(R.id.name_layout);
        this.relativeLayoutRating = (RelativeLayout) findViewById(R.id.rating_layout);
        this.relativeLayoutEmail = (RelativeLayout) findViewById(R.id.email_layout);
    }

    private void initUserInfo() {
        // todo check all fields valuse asignment inclding Edit Text
        this.user = this.socialCarStore.getUser();
        this.changedTravelPref = new TravelPreferences();

        setTitle(this.user.getName());

        this.editTextName.setText(this.user.getName());
        this.textViewPhone.setText(this.user.getPhone());
        this.editTextPhone.setText(this.user.getPhone());
        this.textViewEmail.setText(this.user.getEmail());
        this.textViewRating.setText(String.valueOf(this.user.getRating()));

        TravelPreferences pref = this.user.getTravelPreferences();
        if (pref != null) {

            this.textViewMaxCost.setText((pref.getMaxTransfers() > 0 ? String.valueOf(pref.getMaxCost()) : getResources().getString(R.string.none)));
            this.editTextMaxCost.setText(String.valueOf(pref.getMaxCost()));
            this.changedTravelPref.setMaxCost(pref.getMaxCost());

            this.textViewSmokingAllowed.setText(parseStringFromBoolean(pref.isSmokingPreferred()));
            this.changedTravelPref.setSmokingPreferred(pref.isSmokingPreferred());
            this.textViewFoodAllowed.setText(parseStringFromBoolean(pref.isFoodPreferred()));
            this.changedTravelPref.setFoodPreferred(pref.isFoodPreferred());
            this.textViewMusicAllowed.setText(parseStringFromBoolean(pref.isMusicPreferred()));
            this.changedTravelPref.setMusicPreferred(pref.isMusicPreferred());
            this.textViewPetsAllowed.setText(parseStringFromBoolean(pref.isPetsPreferred()));
            this.changedTravelPref.setPetsPreferred(pref.isPetsPreferred());

            this.textViewPreferredTravelMode.setText((pref.getTravelModes().isEmpty() ? getResources().getString(R.string.none) : pref.formattedStringOfPreferredTravelModes()));
            this.changedTravelPref.setTravelModes(pref.getTravelModes());

            this.textViewOptimizeTravelSolutionsBy.setText((pref.getListTravelSolutionsOptimizations().isEmpty() ? getResources().getString(R.string.none) : pref.formattedStringOfSelectedTravelSolutionsOrder()));
            this.changedTravelPref.setListTravelSolutionsOptimizations(pref.getListTravelSolutionsOptimizations());

            this.textViewCarpoolerGender.setText((pref.getCarpoolerGender() == null ? getResources().getString(R.string.none) : UserGender.getGenderString(pref.getCarpoolerGender())));
            this.changedTravelPref.setCarpoolerGender(pref.getCarpoolerGender());

            this.textViewCarpoolerAgeRange.setText((pref.getCarpoolerAgeRange() == null ? getResources().getString(R.string.none) : pref.getCarpoolerAgeRange()));
            this.changedTravelPref.setCarpoolerAgeRange(pref.getCarpoolerAgeRange());

            this.textViewSpecialNeeds.setText((pref.getListSpecialNeeds().isEmpty() ? getResources().getString(R.string.none) : pref.formattedStringOfSelectedSpecialNeeds()));
            this.changedTravelPref.setListSpecialNeeds(pref.getListSpecialNeeds());

            this.textViewLuggageAllowed.setText(parseStringFromBoolean(pref.isLuggagePreferred()));
            this.changedTravelPref.setLuggagePreferred(pref.isLuggagePreferred());
            this.textViewGpsTracking.setText(parseStringFromBoolean(pref.isAllowGpsTracking()));
            this.changedTravelPref.setAllowGpsTracking(pref.isAllowGpsTracking());
        } else {
            this.textViewSmokingAllowed.setText(getResources().getString(R.string.none));
            this.textViewFoodAllowed.setText(getResources().getString(R.string.none));
            this.textViewMusicAllowed.setText(getResources().getString(R.string.none));
            this.textViewPetsAllowed.setText(getResources().getString(R.string.none));

            this.textViewMaxCost.setText(getResources().getString(R.string.none));

            this.textViewPreferredTravelMode.setText(getResources().getString(R.string.none));

            this.textViewOptimizeTravelSolutionsBy.setText(getResources().getString(R.string.none));

            this.textViewCarpoolerGender.setText(getResources().getString(R.string.none));

            this.textViewCarpoolerAgeRange.setText(getResources().getString(R.string.none));

            this.textViewSpecialNeeds.setText(getResources().getString(R.string.none));

            this.textViewLuggageAllowed.setText(getResources().getString(R.string.none));
            this.textViewGpsTracking.setText(getResources().getString(R.string.none));
        }

        PicassoHelper.loadImage(this, this.imageViewUserPicture, this.socialCarStore.retrieveUserPicture());
    }

    private String parseStringFromBoolean(boolean value) {
        if (value)
            return getString(R.string.yes);
        else
            return getString(R.string.no);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.initDrawer(getString(R.string.profile));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.socialCarStore.getUser().isDriver()) {
            getMenuInflater().inflate(R.menu.menu_user_profile, menu);
            this.setMenuIconColor(menu, R.color.white);
        }
        return true;
    }

    private void setMenuIconColor(Menu menu, int color) {
        MenuItem showCarsItem = menu.findItem(R.id.show_cars);
        Drawable drawable = showCarsItem.getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, color));
        showCarsItem.setIcon(drawable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_cars) {
            Log.i(getTagFromActivity(this), "SHOW CARS!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void prepareUserProfileToEdit(View view) {
        if (this.profileInEditMode) {
            this.profileInEditMode = false;
            // todo investigate: strange behave: setting new value on 'changedTravelPref' changing value of 'user->travelPreferences' also!!
            //this.updateUserIfNeeded();
            makeUpdate();
        } else {
            this.profileInEditMode = true;

            setTitle(R.string.edit_profile_toolbar_label);
            this.fabEditProfile.setImageResource(R.drawable.ic_done_24dp);

            this.editTextName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            this.relativeLayoutName.setVisibility(View.VISIBLE);
            this.editTextName.requestFocus();

            this.relativeLayoutEmail.setVisibility(View.GONE);
            this.relativeLayoutRating.setVisibility(View.GONE);

            this.textViewPhone.setVisibility(View.GONE);
            this.editTextPhone.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            this.editTextPhone.setVisibility(View.VISIBLE);

            this.textViewMaxCost.setVisibility(View.GONE);
            this.editTextMaxCost.setVisibility(View.VISIBLE);

        }
    }

    private void updateUserIfNeeded() {
        if (this.areUserInfoChanges())
            this.makeUpdate();
        else
            this.restoreActivity();
    }

    private void restoreActivity() {
        hideSoftKeyboard();

        setTitle(this.user.getName());
        this.fabEditProfile.setImageResource(R.drawable.ic_edit_24dp);

        this.relativeLayoutName.setVisibility(View.GONE);

        this.relativeLayoutRating.setVisibility(View.VISIBLE);
        this.relativeLayoutEmail.setVisibility(View.VISIBLE);

        this.editTextPhone.setVisibility(View.GONE);
        this.textViewPhone.setVisibility(View.VISIBLE);

        this.textViewMaxCost.setVisibility(View.VISIBLE);
        this.editTextMaxCost.setVisibility(View.GONE);

    }

    private void makeUpdate() {
        if (checkClickTime()) return;

        hideSoftKeyboard();
        this.editTextPhone.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        this.editTextName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        User user = makeUser();

        UpdateUserProfileTask updateUserProfileTask = new UpdateUserProfileTask(this);
        if (updateUserProfileTask.validate(user)) {
            hideSoftKeyboard();
            showProgressDialog();
            updateUserProfileTask.execute(user);
        }
    }

    private User makeUser() {
        user.setName(this.editTextName.getText().toString().trim());
        user.setEmail(this.user.getEmail().trim());
        user.setPhone(this.editTextPhone.getText().toString().trim());

        if (!TextUtils.isEmpty(editTextMaxCost.getText()))
            changedTravelPref.setMaxCost(Integer.valueOf(editTextMaxCost.getText().toString().trim()));

        // todo set changed Travel Preferences
        user.setTravelPreferences(changedTravelPref);
        return user;
    }

    private boolean areUserInfoChanges() {
        // todo check all travel pref
        return !this.editTextPhone.getText().toString().trim().equals(this.user.getPhone()) ||
                !this.editTextName.getText().toString().trim().equals(this.user.getName()) ||
                !this.editTextMaxCost.getText().toString().trim().equals(this.user.getTravelPreferences().getMaxCost().toString()) ||
                !this.textViewPreferredTravelMode.getText().toString().trim().equals(this.user.getTravelPreferences().formattedStringOfPreferredTravelModes().trim()) ||
                !this.textViewOptimizeTravelSolutionsBy.getText().toString().trim().equals(this.user.getTravelPreferences().formattedStringOfSelectedTravelSolutionsOrder().trim()) ||
                !this.textViewSpecialNeeds.getText().toString().trim().equals(this.user.getTravelPreferences().formattedStringOfSelectedSpecialNeeds().trim());

    }

    public void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void showConnectionError() {
        super.showConnectionError();
        this.profileInEditMode = true;
    }

    @Override
    public void showServerGenericError() {
        super.showServerGenericError();
        this.profileInEditMode = true;
    }

    public void showUsernameMarkerError() {
        this.profileInEditMode = true;
        this.editTextName.setText(this.user.getName());
        this.editTextName.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    public void showPhoneMarkerError() {
        this.profileInEditMode = true;
        this.editTextPhone.setText(this.user.getPhone());
        this.editTextPhone.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    public void showPreferredTravelModesProfile(View view) {
        // todo refactor: same impl using on SignUp activity

        if (!profileInEditMode) return;

        if (checkClickTime()) return;
        hideSoftKeyboard();

        Integer[] selectedIndices = changedTravelPref.findIndicesOfSelectedTravelModes();
        new MaterialDialog.Builder(this)
                .title(R.string.travel_modes)
                .widgetColor(ContextCompat.getColor(this, R.color.primary_color))
                .items(TravelMode.sortedNames())
                .itemsCallbackMultiChoice(selectedIndices, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        changedTravelPref.getTravelModes().clear();
                        for (CharSequence charSequence : text) {
                            changedTravelPref.getTravelModes().add(TravelMode.valueOf(String.valueOf(charSequence)));
                        }

                        if (changedTravelPref.getTravelModes().isEmpty()) {
                            textViewPreferredTravelMode.setText(R.string.none);
                        } else {
                            textViewPreferredTravelMode.setText(changedTravelPref.formattedStringOfPreferredTravelModes());
                        }

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .positiveColorRes(R.color.primary_color)
                .show();
    }

    public void showTravelSolutionsOptimizationOptionsProfile(View view) {
        // todo refactor: same impl using on SignUp activity
        if (!profileInEditMode) return;

        if (checkClickTime()) return;
        hideSoftKeyboard();

        Integer[] selectedIndices = changedTravelPref.findIndicesOfSelectedTravelSolutionsOrder();
        new MaterialDialog.Builder(this)
                .title(R.string.optimize_travel_solutions_by)
                .widgetColor(ContextCompat.getColor(this, R.color.primary_color))
                .items(TravelSolutionsOptimizations.sortedNames())
                .itemsCallbackMultiChoice(selectedIndices, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        changedTravelPref.getListTravelSolutionsOptimizations().clear();
                        for (CharSequence charSequence : text) {
                            TravelSolutionsOptimizations solutionsOrder = TravelSolutionsOptimizations.valueOf(String.valueOf(charSequence));
                            changedTravelPref.getListTravelSolutionsOptimizations().add(solutionsOrder);
                        }

                        if (changedTravelPref.getListTravelSolutionsOptimizations().isEmpty()) {
                            textViewOptimizeTravelSolutionsBy.setText(R.string.none);
                        } else {
                            textViewOptimizeTravelSolutionsBy.setText(changedTravelPref.formattedStringOfSelectedTravelSolutionsOrder());
                        }

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .positiveColorRes(R.color.primary_color)
                .show();
    }

    public void showSpecialNeedsPreferencesProfile(View view) {
        // todo refactor: same impl using on SignUp activity
        if (!profileInEditMode) return;

        if (checkClickTime()) return;
        hideSoftKeyboard();

        Integer[] selectedIndices = changedTravelPref.findIndicesOfSelectedSpecialNeeds();
        new MaterialDialog.Builder(this)
                .title(R.string.special_needs)
                .widgetColor(ContextCompat.getColor(this, R.color.primary_color))
                .items(SpecialNeeds.sortedNames())
                .itemsCallbackMultiChoice(selectedIndices, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        changedTravelPref.getListSpecialNeeds().clear();
                        for (CharSequence charSequence : text) {
                            SpecialNeeds specialNeeds = SpecialNeeds.valueOf(String.valueOf(charSequence));
                            changedTravelPref.getListSpecialNeeds().add(specialNeeds);
                        }

                        if (changedTravelPref.getListSpecialNeeds().isEmpty()) {
                            textViewSpecialNeeds.setText(R.string.none);
                        } else {
                            textViewSpecialNeeds.setText(changedTravelPref.formattedStringOfSelectedSpecialNeeds());
                        }

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .positiveColorRes(R.color.primary_color)
                .show();
    }

    public void showDialogProfileOptions(View view) {

        if (!profileInEditMode) return;

        this.clickedViewId = view.getId();

        int idTitleResource = 0;
        String[] arrayValueAllowed = null;

        switch (this.clickedViewId) {
            case R.id.profile_layout_carpooler_gender:
                idTitleResource = R.string.carpooler_gender;
                arrayValueAllowed = getResources().getStringArray(R.array.array_gender);
                break;
            case R.id.profile_layout_optimize_travel_solutions_by:
                idTitleResource = R.string.optimize_travel_solutions_by;
                arrayValueAllowed = TravelSolutionsOptimizations.sortedNames();
                break;
            case R.id.profile_layout_carpooler_age_range:
                idTitleResource = R.string.carpooler_age_range;
                arrayValueAllowed = getResources().getStringArray(R.array.array_carpooler_age_range);
                break;
            case R.id.profile_layout_gps_tracking:
                idTitleResource = R.string.gps_tracking;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.profile_layout_food:
                idTitleResource = R.string.food;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.profile_layout_smoking:
                idTitleResource = R.string.smoking;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.profile_layout_music:
                idTitleResource = R.string.music;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.profile_layout_pets:
                idTitleResource = R.string.pets;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            case R.id.profile_layout_luggage:
                idTitleResource = R.string.luggage;
                arrayValueAllowed = getResources().getStringArray(R.array.array_yes_no);
                break;
            default:
                break;
        }

        MaterialDialogHelper.createSingleChoice(this, idTitleResource, arrayValueAllowed, R.string.choose, this).show();
    }

    @Override
    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

        switch (this.clickedViewId) {
            case R.id.profile_layout_carpooler_gender:
                resetPreferedCarpoolerGender(which, text.toString());
                break;
            case R.id.profile_layout_optimize_travel_solutions_by:
                resetTravelSolutionsOptimizationOption(text);
                break;
            case R.id.profile_layout_carpooler_age_range:
                this.changedTravelPref.setCarpoolerAgeRange(text.toString());
                textViewCarpoolerAgeRange.setText(text.toString());
                break;
            case R.id.profile_layout_gps_tracking:
                this.changedTravelPref.setAllowGpsTracking(parseBooleanFromString(text.toString()));
                textViewGpsTracking.setText(text.toString());
                break;
            case R.id.profile_layout_food:
                textViewFoodAllowed.setText(text);
                this.changedTravelPref.setFoodPreferred(parseBooleanFromString(text.toString()));
                break;
            case R.id.profile_layout_smoking:
                textViewSmokingAllowed.setText(text);
                this.changedTravelPref.setSmokingPreferred(parseBooleanFromString(text.toString()));
                break;
            case R.id.profile_layout_music:
                textViewMusicAllowed.setText(text);
                this.changedTravelPref.setMusicPreferred(parseBooleanFromString(text.toString()));
                break;
            case R.id.profile_layout_pets:
                textViewPetsAllowed.setText(text);
                this.changedTravelPref.setPetsPreferred(parseBooleanFromString(text.toString()));
                break;
            case R.id.profile_layout_luggage:
                textViewLuggageAllowed.setText(text);
                this.changedTravelPref.setLuggagePreferred(parseBooleanFromString(text.toString()));
                break;
            default:
                break;
        }

        return false;
    }

    private void resetTravelSolutionsOptimizationOption(CharSequence text) {
        // TODO SHOW TRANSLATED OPTIONS
        this.changedTravelPref.getListTravelSolutionsOptimizations().clear();
        TravelSolutionsOptimizations optimizationsOption = TravelSolutionsOptimizations.valueOf(String.valueOf(text));
        this.changedTravelPref.getListTravelSolutionsOptimizations().add(optimizationsOption);
        if (this.changedTravelPref.getListTravelSolutionsOptimizations().isEmpty()) {
            this.textViewOptimizeTravelSolutionsBy.setText(R.string.none);
        } else {
            this.textViewOptimizeTravelSolutionsBy.setText(changedTravelPref.formattedStringOfSelectedTravelSolutionsOrder());
        }
    }

    private void resetPreferedCarpoolerGender(int index, String genderLabel) {
        if (index == 0)
            this.changedTravelPref.setCarpoolerGender(UserGender.MALE);
        else
            this.changedTravelPref.setCarpoolerGender(UserGender.FEMALE);

        textViewCarpoolerGender.setText(genderLabel);
    }

    private boolean parseBooleanFromString(String value) {
        return value.equals(getString(R.string.yes));
    }
}
