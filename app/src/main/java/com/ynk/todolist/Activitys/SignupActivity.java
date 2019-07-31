package com.ynk.todolist.Activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.ynk.todolist.Database.AppDatabase;
import com.ynk.todolist.Database.DAO;
import com.ynk.todolist.Model.User;
import com.ynk.todolist.R;

import muyan.snacktoa.SnackToa;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private DAO dao;

    private EditText etName, etUserName, etPassword, etMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dao = AppDatabase.getDb(this).getDAO();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponent() {
        etName = findViewById(R.id.etName);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        etMail = findViewById(R.id.etMail);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                boolean isError = false;
                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    etName.setError(getString(R.string.signUpNameError));
                    isError = true;
                }
                if (TextUtils.isEmpty(etUserName.getText().toString().trim())) {
                    etUserName.setError(getString(R.string.signUpUserNameError));
                    isError = true;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
                    etPassword.setError(getString(R.string.signUpPasswordError));
                    isError = true;
                }
                if (TextUtils.isEmpty(etMail.getText().toString().trim())) {
                    etMail.setError(getString(R.string.signUpEmailError));
                    isError = true;
                }
                if (isError) return;

                if (dao.signUpControl(etUserName.getText().toString(), etMail.getText().toString()) == 0) {
                    User user = new User();
                    user.setUserMail(etMail.getText().toString());
                    user.setUserName(etUserName.getText().toString());
                    user.setUserNameSurname(etName.getText().toString());
                    user.setUserPassword(etPassword.getText().toString());
                    dao.insertUser(user);
                    SnackToa.toastSuccess(this, getString(R.string.signUpSuccessMessage));
                    finish();
                } else {
                    SnackToa.snackBarError(this, getString(R.string.signUpErrorMessage));
                }

                break;

        }
    }
}
