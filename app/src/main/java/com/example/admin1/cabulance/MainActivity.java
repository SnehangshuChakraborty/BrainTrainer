package com.example.admin1.cabulance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.admin1.cabulance.users.DBUsers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    Button SignUpBtn, RegisterBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("/fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("Users");
        SignUpBtn = (Button) findViewById(R.id.btnSignIn);
        RegisterBtn = (Button) findViewById(R.id.btnRegister);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInDialog();
            }
        });
    }

    private void signInDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email id as login key");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View loginLayout = layoutInflater.inflate(R.layout.layout_login, null);
        final MaterialEditText editEmail = loginLayout.findViewById(R.id.emailText);
        final MaterialEditText editPassword = loginLayout.findViewById(R.id.passwordText);
        dialog.setView(loginLayout);
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                    Snackbar.make(relativeLayout, "Please Enter an Email Id", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(relativeLayout, "Please Enter a valid password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(MainActivity.this,welcome.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(relativeLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email id as login key");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View registerLayout = layoutInflater.inflate(R.layout.layout_register, null);
        final MaterialEditText editEmail = registerLayout.findViewById(R.id.emailText);
        final MaterialEditText editPassword = registerLayout.findViewById(R.id.passwordText);
        final MaterialEditText editName = registerLayout.findViewById(R.id.nameText);
        final MaterialEditText editPhone = registerLayout.findViewById(R.id.phoneText);
        dialog.setView(registerLayout);
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                    Snackbar.make(relativeLayout, "Please Enter an Email Id", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editName.getText().toString())) {
                    Snackbar.make(relativeLayout, "Please Enter a valid name", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(relativeLayout, "Please Enter a valid password", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editPhone.getText().toString())) {
                    Snackbar.make(relativeLayout, "Please Enter a valid phone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                DBUsers dbUsers = new DBUsers(editEmail.getText().toString(), editName.getText().toString(), editPassword.getText().toString(), editPhone.getText().toString());
                                users.child(firebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(dbUsers)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(relativeLayout, "Data Added Successfully !!!", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(relativeLayout, "Error " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(relativeLayout, "Error " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

