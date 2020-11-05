package com.example.android.tutr;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements TextView.OnEditorActionListener {


    private static boolean PARSE_INITIALIZED = false;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://crammer-api.herokuapp.com/api/courses", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("Courses available",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Courses available",error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
        requestQueue.start();

        if (!PARSE_INITIALIZED) {
            Parse.initialize(this);
            PARSE_INITIALIZED = true;
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
//            ParseUser.logOut();
            System.out.println("DEBUG: User already logged in!");
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.get().load("file:///android_asset/tutr_img.jpg").fit().into(imageView);

        mEmailView = (EditText) findViewById(R.id.email_login);
        mEmailView.setOnEditorActionListener(this);

        mPasswordView = (EditText) findViewById(R.id.password_login);
        mPasswordView.setOnEditorActionListener(this);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_login_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        goToRegister();
    }


    // set the "here" clickable in "new user? Register here
    private void goToRegister(){
        SpannableString ss = new SpannableString("New User? Register here");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(R.color.black));
            }
        };
        ss.setSpan(clickableSpan, 19, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView register = (TextView) findViewById(R.id.new_user_text);
        register.setText(ss);
        register.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginUser(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        final String email_pattern = "[0-9a-z]+.?[0-9a-z]+@(mail.)?mcgill.ca";
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == R.id.login || actionId == EditorInfo.IME_ACTION_SEND) {
            attemptLogin();
            return true;
        }
        return false;
    }

    void loginUser(String email, String password) {
//        ParseUser.logInInBackground(email, password, new LogInCallback() {
//            public void done(ParseUser user, ParseException e) {
//                if (user != null) {
//                    // Hooray! The user is logged in.
//                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
//                } else {
//                    // Signup failed. Look at the ParseException to see what happened.
//                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                    Intent intent = getIntent();
//                    finish();
//                    startActivity(intent);
//                }
//            }
//        });

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}

