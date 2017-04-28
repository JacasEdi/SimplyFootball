package com.example.jacek.simplyfootball;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.jacek.simplyfootball.databinding.ActivityUpdateDetailsBinding;
import com.example.jacek.simplyfootball.viewmodel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Activity class that represents business model of an Update Details Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class UpdateDetailsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "UpdateDetailsActivity";

    // Declare data binding object for the activity
    private ActivityUpdateDetailsBinding binding;

    // Declare new User object for accessing user data
    private User user;

    // Declare variables for corresponding views in the UpdateDetailsActivity layout file
    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private EditText etNewEmail;
    private Switch switchUpdateDetails;

    // Declare the FirebaseAuth and AuthStateListener objects.
    private FirebaseAuth firebaseAuth;

    // Declare the FirebaseUser object
    private FirebaseUser firebaseUser;

    // Create instance of a Firebase database
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_details);

        // Setting handlers for data binding
        binding.setHandlers(this);

        // New user object needs to be created and set for Data Binding purposes
        user = new User();
        binding.setUser(user);

        // Store the field now if you'd like without any need for casting
        etCurrentPassword = binding.etCurrentPassword;
        etNewPassword = binding.etNewPassword;
        etConfirmPassword = binding.etConfirmNewPassword;
        etNewEmail = binding.etNewEmail;
        switchUpdateDetails = binding.switchUpdateDetails;

        // Setting up initial switch state
        switchUpdateDetails.setChecked(false);
        switchUpdateDetails.setText("Updating Password");
        etNewPassword.setEnabled(true);
        etConfirmPassword.setEnabled(true);
        etNewEmail.setEnabled(false);

        // Initialize FirebaseAuth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Get instance of currently logged in user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        switchUpdateDetails.setOnCheckedChangeListener(this);
    }

    /** Handles switch clicks between "Editing Password" and "Editing Email" */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (isChecked)
        {
            // Allow the user to update email address only when switch is in "on" position

            switchUpdateDetails.setText("Updating Email");
            etNewEmail.setEnabled(true);

            etNewPassword.setEnabled(false);
            etConfirmPassword.setEnabled(false);
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        }
        else
        {
            // Allow the user to update password only when switch is in "off" position

            switchUpdateDetails.setText("Updating Password");
            etNewPassword.setEnabled(true);
            etConfirmPassword.setEnabled(true);

            etNewEmail.setEnabled(false);
            etNewEmail.setText("");
        }
    }

    /** Handles clicks on Update Details button */
    public void onClickUpdate(final View view)
    {
        // Get values from corresponding fields inside the layout
        final String currentPassword = binding.getUser().currentPassword.get();
        final String newPassword = binding.getUser().password.get();
        final String confirmPassword = binding.getUser().confirmPassword.get();
        final String newEmail = binding.getUser().email.get();


        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        if(verifyPassword(currentPassword, etCurrentPassword))
        {
            // Firebase credential object used to re-authenticate the user with current email and password
            final AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);

            // Re-authenticate user using credential object
            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        if (!switchUpdateDetails.isChecked())
                        {
                            updatePassword(newPassword, confirmPassword);
                        }
                        else
                        {
                            updateEmail(newEmail);
                        }

                    }
                    else
                    {
                        Toast.makeText(UpdateDetailsActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();

                        View focusView = null;
                        focusView = etCurrentPassword;
                        focusView.requestFocus();
                    }
                }
            });

        }

    }

    /** Handles attempts to update password and overrides password stored in Firebase if successful */
    private void updatePassword(final String newPassword, final String confirmPassword)
    {
        if (verifyPassword(newPassword, etNewPassword))
        {
            if (verifyPassword(confirmPassword, etConfirmPassword) &&
                    confirmPassword.equalsIgnoreCase(newPassword))
            {
                firebaseUser.updatePassword(newPassword).addOnCompleteListener
                        (new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG, "User password updated.");
                                    Toast.makeText(UpdateDetailsActivity.this,
                                            "Password updated", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    Log.d(TAG, "Failed to update password.");
                                    Toast.makeText(UpdateDetailsActivity.this,
                                            "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else
            {
                Toast.makeText(UpdateDetailsActivity.this, "Password does not match!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Handles attempts to update email address and overrides email stored in Firebase if successful */
    private void updateEmail(final String newEmail)
    {
        if(verifyEmail(newEmail, etNewEmail))
        {
            firebaseUser.updateEmail(newEmail).addOnCompleteListener
                    (new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Log.d(TAG, "User email updated.");
                                Toast.makeText(UpdateDetailsActivity.this,
                                        "Email updated", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Log.d(TAG, "Failed to update email.");
                                Toast.makeText(UpdateDetailsActivity.this,
                                        "Failed to update email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /** Validation checks on email address EditText view */
    private boolean verifyEmail(String email, EditText view)
    {
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            view.setError(getString(R.string.error_field_required));
            focusView = view;
            focusView.requestFocus();
            return false;
        }
        else if (!isEmailValid(email)) {
            view.setError(getString(R.string.error_invalid_email));
            focusView = view;
            focusView.requestFocus();
            return false;
        }
        else
            return true;
    }

    /** Validation checks on password EditText view */
    private boolean verifyPassword(String password, EditText view)
    {
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            view.setError(getString(R.string.error_field_required));
            focusView = view;
            focusView.requestFocus();
            return false;
        }
        else if (!isPasswordValid(password)) {
            view.setError(getString(R.string.error_invalid_password));
            focusView = view;
            focusView.requestFocus();
            return false;
        }
        else
            return true;
    }

    /** Validates input email address */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /** Validates input password */
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    @Override
    protected void onStop() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
        etNewEmail.setText("");

        super.onStop();
    }
}
