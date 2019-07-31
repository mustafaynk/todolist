package com.ynk.todolist.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ynk.todolist.Database.AppDatabase;
import com.ynk.todolist.Database.DAO;
import com.ynk.todolist.Model.User;
import com.ynk.todolist.R;
import com.ynk.todolist.Tools.Utils;

import muyan.snacktoa.SnackToa;


public class LoginActivity extends AppCompatActivity {

    //Room Database
    private DAO dao;

    //Login Authentication
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //Components
    private TextInputEditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dao = AppDatabase.getDb(this).getDAO();
        preferences = getSharedPreferences(Utils.APP_NAME, MODE_PRIVATE);

        if (preferences.getBoolean(Utils.loginControlKey, false)) {
            User user = dao.loginControl(preferences.getString(Utils.loginUserNameKey, ""));
            openMainActivity(user);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        edtUsername = findViewById(R.id.etUserName);
        edtPassword = findViewById(R.id.etPassword);


        findViewById(R.id.btnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isError = false;
                if (TextUtils.isEmpty(edtUsername.getText().toString().trim())) {
                    edtUsername.setError(getString(R.string.loginUserNameError));
                    isError = true;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    edtPassword.setError(getString(R.string.loginPasswordError));
                    isError = true;
                }
                if (isError) return;
                User user = dao.login(edtUsername.getText().toString(), edtPassword.getText().toString());
                if (user != null) {
                    createLoginSession(edtUsername.getText().toString(), edtPassword.getText().toString());
                    openMainActivity(user);
                } else {
                    SnackToa.snackBarError(LoginActivity.this, getString(R.string.loginErrorMessage));
                }

            }
        });

        findViewById(R.id.tvSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signUpIntent);
            }
        });

    }

    private void openMainActivity(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("user", new Gson().toJson(user));
        Intent loginSuccessIntent = new Intent(LoginActivity.this, MainActivity.class);
        loginSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        loginSuccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginSuccessIntent.putExtras(bundle);
        startActivity(loginSuccessIntent);
        finish();
    }


    public void createLoginSession(String userName, String password) {
        editor = preferences.edit();
        // giriş bilgileri doğru ise login değerini true yapıyoruz.
        editor.putBoolean(Utils.loginControlKey, true);

        // username ve password bilgilerini editor ile kaydediyoruz.
        editor.putString(Utils.loginUserNameKey, userName);

        // değişiklikleri commit ediyoruz.
        editor.apply();
    }

}
