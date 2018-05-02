package eu.h2020.sc.ui.signin;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.ui.home.HomeActivity;
import eu.h2020.sc.ui.signin.task.SignInTask;
import eu.h2020.sc.ui.signup.SignUpConnectSocialActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.WidgetHelper;

/**
 * Created by khairul.alam on 09/06/16.
 */

public class SignInActivity extends GeneralActivity implements TextWatcher, EditText.OnEditorActionListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIn;
    private TextView textViewForgotPasswordMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        this.initUI();
        this.initEvents();
    }

    private void initUI() {
        this.editTextEmail = (EditText) findViewById(R.id.email);
        this.editTextPassword = (EditText) findViewById(R.id.password);
        this.buttonSignIn = (Button) findViewById(R.id.btnSignin);

        this.textViewForgotPasswordMsg = (TextView) findViewById(R.id.text_view_forgot_password_msg);
        this.textViewForgotPasswordMsg.setText(Html.fromHtml(getString(R.string.forgot_password_msg)));
        this.textViewForgotPasswordMsg.setMovementMethod(LinkMovementMethod.getInstance());
        this.textViewForgotPasswordMsg.setLinkTextColor(getResources().getColor(R.color.primary_color));


        initToolBar(false);
        initBack();
        getToolbar().getBackground().setAlpha(0);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initEvents() {
        this.editTextPassword.setOnEditorActionListener(this);
        this.editTextEmail.addTextChangedListener(this);
        this.editTextPassword.addTextChangedListener(this);
    }

    public void doLogin(View v) {
        Credentials credentials = new Credentials(this.editTextEmail.getText().toString(), this.editTextPassword.getText().toString());
        SignInTask signInTask = new SignInTask(this);
        if (signInTask.validate(credentials)) {
            showProgressDialog();
            signInTask.execute(credentials);
        }
    }

    public void showInsertEmail() {
        WidgetHelper.showToast(getApplicationContext(), getString(R.string.insert_email));
    }

    public void showInsertPassword() {
        WidgetHelper.showToast(this, getString(R.string.insert_password));
    }

    public void showInvalidEmailPassword() {
        WidgetHelper.showToast(this, getString(R.string.invalid_email_password));
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (textView.getId() == R.id.password && actionId == EditorInfo.IME_ACTION_DONE) {
            doLogin(textView);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityUtils.openActivity(this, SocialSigninActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!this.editTextEmail.getText().toString().isEmpty() && !this.editTextPassword.getText().toString().isEmpty())
            this.buttonSignIn.setAlpha(1);
        else
            this.buttonSignIn.setAlpha(.5f);
    }

    @Override
    public void showUnauthorizedError() {
        showInvalidEmailPassword();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void goToSignUpConnectSocial(View view) {
        ActivityUtils.openActivity(this, SignUpConnectSocialActivity.class);
    }

    public void goToHome() {
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }
}
