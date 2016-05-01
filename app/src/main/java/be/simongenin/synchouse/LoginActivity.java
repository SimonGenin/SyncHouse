package be.simongenin.synchouse;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.requests.PostRequest;
import be.simongenin.synchouse.utils.ServerUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mHomeIDView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private SyncHouseApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        application = (SyncHouseApplication) getApplication();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mHomeIDView = (EditText) findViewById(R.id.email);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mHomeIDView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String homeID = mHomeIDView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid Home ID
        if (TextUtils.isEmpty(homeID)) {
            mHomeIDView.setError("Ce champs est requis");
            focusView = mHomeIDView;
            cancel = true;
        } else if (!isHomeIDValid(homeID)) {
            mHomeIDView.setError("Cet ID est incorrect");
            focusView = mHomeIDView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Ce champs est requis");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("Ce mot de passe est incorrect");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(homeID, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isHomeIDValid(String id) {
        return id.length() >= 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * AUTHOR : GOOGLE
     *
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String homeID;
        private final String password;

        /**
         * Values that will be used by the task
         */
        private boolean isHomeIDRight = false;
        private boolean isPasswordRight = false;

        UserLoginTask(String homeID, String password) {
            this.homeID = homeID;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = ServerUtils.LOGIN_URL;
            Map<String, String> parameters = new HashMap<>();
            parameters.put("home_id", homeID);
            parameters.put("password", password);

            PostRequest loginRequest = new PostRequest(url, parameters, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    try {

                        /**
                         * We retrieve the infos back from the server
                         */

                        JSONObject jsonResponse = new JSONObject(response);
                        isHomeIDRight = jsonResponse.getBoolean("home_Id");
                        isPasswordRight = jsonResponse.getBoolean("password");

                    } catch (JSONException e) {

                        /**
                         * Should never happen.
                         * If it does, it means there is a typo with the server code.
                         */

                        e.printStackTrace();
                        cancel(true);

                    }

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    /**
                     * Simply informs the user an error has occured
                     */

                    Toast.makeText(LoginActivity.this, "Une erreur est survenue lors de la tentative de connexion.", Toast.LENGTH_LONG).show();
                    cancel(true);

                }

            });

            /**
             * Start the request
             */
            application.requestQueue.add(loginRequest);

            /**
             * Value for the success variable in "OnPostExecute"
             */
            return isHomeIDRight && isPasswordRight;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                application.isUserConnected = true;
                finish();
            } else {

                if (isHomeIDRight) {
                    mPasswordView.setError("Le mot de passe est incorrect.");
                    mPasswordView.requestFocus();
                }

                if (isPasswordRight) {
                    mHomeIDView.setError("Vos informations sont erronées");
                    mHomeIDView.requestFocus();
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

