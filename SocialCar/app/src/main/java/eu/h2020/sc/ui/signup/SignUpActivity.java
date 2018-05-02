package eu.h2020.sc.ui.signup;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.domain.SocialProvider;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.UserGender;
import eu.h2020.sc.ui.home.HomeActivity;
import eu.h2020.sc.ui.profile.ProfileActivity;
import eu.h2020.sc.ui.signup.task.SignUpTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.CropImageHelper;
import eu.h2020.sc.utils.DimensionUtils;
import eu.h2020.sc.utils.ImagePicker;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.PicassoHelper;
import eu.h2020.sc.utils.WidgetHelper;


public class SignUpActivity extends GeneralActivity implements TextWatcher, View.OnFocusChangeListener, MaterialDialog.ListCallbackSingleChoice {

    public static final String USER_KEY = "USER_KEY";
    public static final String SIGNUP_TITLE_KEY = "SIGNUP_TITLE_KEY";
    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private CropImageHelper cropImageHelper;
    private ImageView profilePicture;
    private ImageView showHidePassword;
    private ImageView showHideConfirmPassword;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPhone;

    private ImageView showErrorPassWord;
    private ImageView showErrorConfirmPassWord;
    private Bitmap selectedPicture;

    private CheckBox acceptedTermsCheckbox;

    //variabili di appoggio
    private String gender;
    private SocialProvider socialNetworkProvider;
    private String toolbarTitle;

    private int clickedViewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.toolbarTitle = getIntent().getStringExtra(SIGNUP_TITLE_KEY);

        this.cropImageHelper = new CropImageHelper();

        initToolBar(false);
        initBack();
        initUI();
        initEvents();
        initUserFromSocial();

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void initUI() {
        setTitle(this.toolbarTitle);

        this.profilePicture = (ImageView) findViewById(R.id.signup_imageview_profilepicture);

        this.editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        this.editTextPassword.setOnFocusChangeListener(this);
        this.editTextConfirmPassword = (EditText) findViewById(R.id.edit_text_confirm_password);
        this.editTextConfirmPassword.setOnFocusChangeListener(this);

        this.editTextName = (EditText) findViewById(R.id.editTextName);
        this.editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);

        this.showHidePassword = (ImageView) findViewById(R.id.showHidePassWord);
        this.showErrorPassWord = (ImageView) findViewById(R.id.showErrorPassWord);

        this.showHideConfirmPassword = (ImageView) findViewById(R.id.showHideConfirmPassWord);
        this.showErrorConfirmPassWord = (ImageView) findViewById(R.id.showErrorConfirmPassWord);

        this.acceptedTermsCheckbox = (CheckBox) findViewById(R.id.accept_terms_checkbox_registration);
    }

    private void initEvents() {
        this.editTextPassword.addTextChangedListener(this);
        this.editTextConfirmPassword.addTextChangedListener(this);
        this.editTextName.addTextChangedListener(this);
        this.editTextEmail.addTextChangedListener(this);
        this.editTextPhone.addTextChangedListener(this);
    }

    private void initUserFromSocial() {

        User socialUser = (User) ActivityUtils.getSerializableFromIntent(this, USER_KEY);

        if (socialUser != null) {
            SignUpTask signUpTask = new SignUpTask(this);
            signUpTask.fillUserForm(socialUser);
            this.socialNetworkProvider = socialUser.getSocialProvider();
        }
    }

    public void showHidePassword(View v) {
        if (this.editTextPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
            this.editTextPassword.setTransformationMethod(null);
        } else {
            this.editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        this.editTextPassword.setSelection(this.editTextPassword.getText().length());
    }

    public void showHideConfirmPassword(View v) {
        if (this.editTextConfirmPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
            this.editTextConfirmPassword.setTransformationMethod(null);
        } else {
            this.editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        this.editTextConfirmPassword.setSelection(this.editTextConfirmPassword.getText().length());
    }

    public void doSignUp(View view) {
        if (checkClickTime()) return;

        hideSoftKeyboard();
        User user = makeUser();

        SignUpTask signUpTask = new SignUpTask(this);
        if (signUpTask.validateSignUpInput(user)) {
            hideSoftKeyboard();
            showProgressDialog();
            signUpTask.execute(user);
        }
    }

    public boolean passwordsAreValid() {

        boolean passwordIsValid = (!this.editTextPassword.getText().toString().isEmpty()
                && !(this.editTextPassword.getText().length() < Globals.PASSWORD_LENGTH));
        boolean confirmPasswordIsValid = (!this.editTextConfirmPassword.getText().toString().isEmpty()
                && !(this.editTextConfirmPassword.getText().length() < Globals.PASSWORD_LENGTH));
        boolean passwordsAreSame = this.editTextPassword.getText().toString().trim().equals(this.editTextConfirmPassword.getText().toString().trim());

        return passwordIsValid && confirmPasswordIsValid && passwordsAreSame;

    }

    private User makeUser() {
        User user = new User(editTextName.getText().toString().trim());
        user.setPassword(editTextPassword.getText().toString().trim());
        user.setEmail(editTextEmail.getText().toString().trim());
        user.setPhone(editTextPhone.getText().toString().trim());
        user.setSocialProvider(this.socialNetworkProvider);
        user.setAcceptedTerms(this.acceptedTermsCheckbox.isChecked());
        if (this.gender != null) {
            user.setGender(UserGender.valueOf(gender.toUpperCase()));
        }

        return user;
    }


    public void showPasswordMarkerError() {
        this.showHidePassword.setVisibility(View.GONE);
        this.showErrorPassWord.setVisibility(View.VISIBLE);

        this.showHideConfirmPassword.setVisibility(View.GONE);
        this.showErrorConfirmPassWord.setVisibility(View.VISIBLE);
    }

    public void showUsernameMarkerError() {
        editTextName.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_face_24dp), null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }


    public void showEmailMarkerError() {
        editTextEmail.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_email_24dp), null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }


    public void showPhoneMarkerError() {
        editTextPhone.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_phone_24dp), null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (editTextPassword.isFocused()) {
            showErrorPassWord.setVisibility(View.GONE);
        }
        if (editTextConfirmPassword.isFocused()) {
            showErrorConfirmPassWord.setVisibility(View.GONE);
        }
        if (editTextName.isFocused()) {
            editTextName.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_face_24dp), null, null, null);
        }
        if (editTextEmail.isFocused()) {
            editTextEmail.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_email_24dp), null, null, null);
        }
        if (editTextPhone.isFocused()) {
            editTextPhone.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_phone_24dp), null, null, null);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (editTextPassword.isFocused()) {
            showHidePassword.setVisibility(View.VISIBLE);
        }
        if (editTextConfirmPassword.isFocused()) {
            showHideConfirmPassword.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (editTextPassword.isFocused() && editTextPassword.getText().toString().isEmpty()) {
            showHidePassword.setVisibility(View.GONE);
        }
        if (editTextConfirmPassword.isFocused() && editTextConfirmPassword.getText().toString().isEmpty()) {
            showHideConfirmPassword.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.editTextPassword:
                if (hasFocus)
                    this.editTextPassword.setHint(R.string.min6);
                else
                    this.editTextPassword.setHint(R.string.password);
                break;
            case R.id.edit_text_confirm_password:
                if (hasFocus)
                    this.editTextConfirmPassword.setHint(R.string.min6);
                else
                    this.editTextConfirmPassword.setHint(R.string.confirm_password);
                break;
        }
    }

    /**
     * ************************ PICK IMAGE & CROPPING **********************************************
     */

    public void pickImage(View view) {
        if (checkClickTime()) return;
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_REQUEST_CODE);
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
                        this.selectedPicture = this.cropImageHelper.getBitmapFromUri(croppedImageURI, this);

                        PicassoHelper.loadCircleImageFromDisk(this, this.profilePicture, croppedImageURI);

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

    public void showUserAlreadyExists() {
        dismissDialog();
        MaterialDialogHelper.createDialogGotIt(this, R.string.note, R.string.user_already_created, R.string.ok).show();
    }

    public void goToHome() {
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }

    public void goToProfile() {
        ActivityUtils.openActivityNoBack(this, ProfileActivity.class);
    }

    public Bitmap getUserPicture() {
        if (selectedPicture != null)
            return selectedPicture;
        return null;
    }

    public void showUsername(String name) {
        this.editTextName.setText(name);
    }

    public void showUserEmail(String email) {
        editTextEmail.setText(email);
    }

    public void showProfilePicture(Bitmap userPhotoData) {
        this.selectedPicture = userPhotoData;
        PicassoHelper.loadCircleImage(this, this.profilePicture, userPhotoData);
    }

    public void disableEditTextEmail() {
        this.editTextEmail.setEnabled(false);
    }

    @Override
    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
        return false;
    }

    public void goToTermsConditions(View view) {
        ActivityUtils.openBrowserAtURL(getString(R.string.url_terms), this);
    }

    public void showNotAcceptTermsError() {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}, // checked
                },
                new int[]{
                        ContextCompat.getColor(SignUpActivity.this, R.color.red),
                        ContextCompat.getColor(SignUpActivity.this, R.color.accent_color),
                }
        );

        CompoundButtonCompat.setButtonTintList(acceptedTermsCheckbox, colorStateList);
        WidgetHelper.showToast(this, R.string.sign_up_accept_terms_conditions_error);
    }
}