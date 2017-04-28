package com.example.jacek.simplyfootball;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jacek.simplyfootball.databinding.ActivityRegisterBinding;
import com.example.jacek.simplyfootball.viewmodel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity class that represents business model of a Register Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class RegisterActivity extends AppCompatActivity
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "RegisterActivity";

    // Declare data binding object for the activity
    private ActivityRegisterBinding binding;

    // Create instance of a Firebase database
    private DatabaseReference rootDatabase;

    // Declare the FirebaseAuth and AuthStateListener objects.
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseUser currentUser;

    // Declare new User object for accessing user data
    private User user;

    // Declare variables for corresponding views in the RegisterActivity layout file
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private Button btnDate;

    private CheckBox cbOverThirteen;

    private Spinner spinnerSubcription;

    private DatePickerDialog dialog;
    private String dateOfBirth = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        // Setting handlers for data binding
        binding.setHandlers(this);

        // New user object needs to be created and set for Data Binding purposes
        user = new User();
        binding.setUser(user);

        // Store the field now if you'd like without any need for casting
        etFirstName = binding.etFirstname;
        etLastName = binding.etLastname;
        etEmail = binding.etEmail;
        etPassword = binding.etPassword;
        etConfirmPassword = binding.etConfirmPassword;
        btnRegister = binding.btnRegister;
        btnDate = binding.btnDateOfBirth;

        cbOverThirteen = binding.cbOverThirteen;

        spinnerSubcription = binding.spinnerSubscription;

        // point database reference to the root directory of Firebase database
        rootDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize FirebaseAuth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Listen for changes to user authentication status
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                // Get the ID of the user that has been authenticated
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Log.d(TAG, "USER ID BEFORE CALLING storeExtra: " + user.getUid());

                    // Call to storeExtra method with currently authenticated user passed in a parameter
                    storeExtra(user);

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        etFirstName.setText("");
        etLastName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");

        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /** Handles clicks on Register button */
    public void onClickRegister(final View view)
    {
        // Get values from corresponding fields inside the layout
        final String email = binding.getUser().email.get();
        final String password = binding.getUser().password.get();
        final String confirmPassword = binding.getUser().confirmPassword.get();

        if(verifyInput(email, password, confirmPassword))
        {
            // Create a new account using firebaseAuth object
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(view.getContext(), "Registration successful", Toast.LENGTH_SHORT).show();

                            if (!task.isSuccessful()) {
                                Toast.makeText(view.getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /** Verifies user input before proceeding with the register attempt */
    private boolean verifyInput(String email, String password, String confirmPassword)
    {
        View focusView = null;


        if (TextUtils.isEmpty(binding.getUser().firstName.get()))
        {
            etFirstName.setError(getString(R.string.error_field_required));
            focusView = etFirstName;
            focusView.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(binding.getUser().lastName.get()))
        {
            etLastName.setError(getString(R.string.error_field_required));
            focusView = etLastName;
            focusView.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(email))
        {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            focusView.requestFocus();
            return false;
        }
        else if (!isEmailValid(email))
        {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            focusView.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(password))
        {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            focusView.requestFocus();
            return false;

        }
        else if (!isPasswordValid(password))
        {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            focusView.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(confirmPassword))
        {
            etConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = etConfirmPassword;
            focusView.requestFocus();
            return false;
        }
        else if (!isPasswordValid(confirmPassword))
        {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            focusView.requestFocus();
            return false;
        }
        else if(!password.equals(confirmPassword))
        {
            etPassword.setError(getString(R.string.error_invalid_password_match));
            focusView = etPassword;
            focusView.requestFocus();
            return false;
        }
        else if(dateOfBirth.isEmpty())
        {
            Toast.makeText(this, "Date of birth must be set", Toast.LENGTH_SHORT).show();

            return false;
        }
        else if(!cbOverThirteen.isChecked())
        {
            Toast.makeText(this, "You need to be over 13 to register", Toast.LENGTH_SHORT).show();

            return false;
        }
        else
        {
            return true;
        }
    }

    /** Validates input email address */
    private boolean isEmailValid(String email)
    {
        if(email.contains("@") && email.contains("."))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /** Validates input password */
    private boolean isPasswordValid(String password)
    {
        return password.length() > 5;
    }

    /** Retrieves user input from Registration form and places it in Firebase under user ID node */
    private void storeExtra(FirebaseUser user)
    {
        Log.d(TAG, "user id INSIDE storeExtra: " + user.getUid());

        // Get values from corresponding fields inside the layout
        final String firstName = binding.getUser().firstName.get();
        final String lastName = binding.getUser().lastName.get();
        final String subType = spinnerSubcription.getSelectedItem().toString();
        final String dob = dateOfBirth;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Map<String, String> map = new HashMap<>();

        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("subscriptionType", subType);
        map.put("dateOfBirth", dob);

        ref.child("users").child(user.getUid()).setValue(map);
    }

    /** Handles clicks on Date of Birth button */
    public void onClickDate(View view)
    {
        Calendar c = Calendar.getInstance();

        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mMonth = c.get(Calendar.MONTH);
        int mYear = c.get(Calendar.YEAR);

        dialog = new DatePickerDialog(view.getContext(), new mDateSetListener(), mYear, mMonth, mDay);
        dialog.show();
    }

    // Helper class for handling date selection in DatePickerDialog
    private class mDateSetListener implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;

            StringBuilder sb = new StringBuilder();

            // Month is 0 based so add 1
            sb.append(mDay).append("/").append(mMonth + 1).append("/").append(mYear);

            dateOfBirth = sb.toString();
        }
    }
}
