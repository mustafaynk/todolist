package com.ynk.todolist.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ynk.todolist.Fragments.FragmentTodoList;
import com.ynk.todolist.Listeners.MainListener;
import com.ynk.todolist.Model.User;
import com.ynk.todolist.R;
import com.ynk.todolist.Tools.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainListener {

    private User user;
    private Gson gson;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gson = new Gson();

        if (getIntent().getExtras() != null)
            user = gson.fromJson(getIntent().getExtras().getString("user"), User.class);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        //toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);
        navigationView.getMenu().findItem(R.id.nav_todolists).getIcon().setColorFilter(getResources().getColor(R.color.blue_500), PorterDuff.Mode.SRC_ATOP);
        navigationView.getMenu().findItem(R.id.nav_exit).getIcon().setColorFilter(getResources().getColor(R.color.red_500), PorterDuff.Mode.SRC_ATOP);

        TextView tvHeader = navigationView.getHeaderView(0).findViewById(R.id.tvHeader);
        tvHeader.setText(user.getUserNameSurname());

        openPage("TL");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_todolists) {
            openPage("TL");
        } else if (id == R.id.nav_exit) {
            signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        //Clear Authentication
        SharedPreferences preferences = getSharedPreferences(Utils.APP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Utils.loginControlKey);
        editor.remove(Utils.loginUserNameKey);
        editor.remove(Utils.loginUserPassword);
        editor.apply();

        Intent loginSuccessIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        loginSuccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginSuccessIntent);
        finish();
    }

    @Override
    public void openPage(String pageCode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("user", gson.toJson(user));
        switch (pageCode) {
            case "TL"://TodoList
                fragment = new FragmentTodoList();
                break;
        }
        if (fragment != null) {
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content, fragment);
            fragmentTransaction.commit();
        }
    }
}
