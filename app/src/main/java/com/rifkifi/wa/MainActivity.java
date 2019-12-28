package com.rifkifi.wa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText et_code, et_phonenumber;
    private Button btn_verify, btn_verifycode;
    private TextView tv_dontgetecode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String mVerificationId, smsCode;

    ProgressDialog loading;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        userIsLoggedIn();

        et_code = findViewById(R.id.et_code);
        et_phonenumber = findViewById(R.id.et_phonenumber);
        btn_verify = findViewById(R.id.btn_verify);
        btn_verifycode = findViewById(R.id.btn_verifycode);
        tv_dontgetecode = findViewById(R.id.tv_dontgetecode);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberVerification();
                loading = ProgressDialog.show(MainActivity.this, null, "Harap Tunggu...", true, false);
            }
        });

        btn_verifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumberWithCode();
                loading = ProgressDialog.show(MainActivity.this, null, "Harap Tunggu...", true, false);
            }
        });
        tv_dontgetecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberVerification();
                loading = ProgressDialog.show(MainActivity.this, null, "Harap Tunggu...", true, false);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                loading.dismiss();
                smsCode = phoneAuthCredential.getSmsCode();
                btn_verifycode.setVisibility(View.VISIBLE);
                et_code.setVisibility(View.VISIBLE);
                tv_dontgetecode.setVisibility(View.VISIBLE);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
            }
        };
    }

    private void verifyPhoneNumberWithCode(){
        if (!smsCode.equals(et_code.getText().toString())){
            Toast.makeText(this, "Maaf, kode anda masukkan salah", Toast.LENGTH_SHORT).show();
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, et_code.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            userIsLoggedIn();
                        }
                    }
                });
    }

    private void userIsLoggedIn() {
        FirebaseUser loginUser = FirebaseAuth.getInstance().getCurrentUser();
        if (loginUser != null) {
            loading.dismiss();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    private void phoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(et_phonenumber.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }
}
