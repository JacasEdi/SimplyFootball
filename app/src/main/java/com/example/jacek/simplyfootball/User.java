package com.example.jacek.simplyfootball;

import android.databinding.ObservableField;


/**
 * Simple POJO class used as a model for a single User object.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class User
{
    public final ObservableField<String> firstName = new ObservableField<>("");
    public final ObservableField<String> lastName = new ObservableField<>("");
    public final ObservableField<String> email = new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");
    public final ObservableField<String> confirmPassword = new ObservableField<>("");
    public final ObservableField<String> currentPassword = new ObservableField<>("");

    public User()
    {
        // Default constructor required for calls to Firebase's DataSnapshot.getValue(User.class)
    }

    //public User(String name, String email) {}

}
