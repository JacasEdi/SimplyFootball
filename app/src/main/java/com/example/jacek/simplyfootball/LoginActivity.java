package com.example.jacek.simplyfootball;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jacek.simplyfootball.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Activity class that represents business model of a Login Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class LoginActivity extends AppCompatActivity
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "LoginActivity";

    // Data binding object for the activity
    private ActivityLoginBinding binding;

    // User object for accessing input data
    private User user;

    // Variables for corresponding views from the layout file
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    // FirebaseAuth and AuthStateListener objects
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Instance of a Firebase database
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Setting handlers for data binding purposes
        binding.setHandlers(this);

        // Create and bind new user object
        user = new User();
        binding.setUser(user);

        // Bind variables to their views
        etEmail = binding.etEmailLogin;
        etPassword = binding.etPasswordLogin;
        btnLogin = binding.btnLogin;

        // Initialize FirebaseAuth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Point database reference to the users node of Firebase database
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Listen for changes to user authorisation status
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    Log.i(TAG, "User already signed in, should go to News Feed");
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    // Send the user to HomeScreenActivity
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                }
                else
                {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.i(TAG, "User not signed in, needs to sign in");
                }
            }
        };
    }

    /** Handles Login button clicks */
    public void onClickLogin(final View view)
    {
        // Get values from corresponding fields inside the layout
        final String email = binding.getUser().email.get();
        final String password = binding.getUser().password.get();

        if(verifyInput(email, password))
        {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Log.i(TAG, "User successfully signed in.");
                    }
                    else
                    {
                        Toast.makeText(view.getContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    /** Verifies user input before proceeding with the login attempt */
    private boolean verifyInput(String email, String password)
    {
        View focusView;

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            focusView.requestFocus();
            return false;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            focusView.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            focusView.requestFocus();
            return false;

        } else if (!isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            focusView.requestFocus();
            return false;
        }
        else
        {
            return true;
        }
    }

    /** Validates input email address */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /** Validates input password */
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /** Handles "Register here" button clicks */
    public void onClickRegisterHere(View view)
    {
        // Send the user to RegisterActivity
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        // Delete any user input inside EditText fields
        etEmail.setText("");
        etPassword.setText("");

        super.onStop();

        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
