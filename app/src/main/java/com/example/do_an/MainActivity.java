package com.example.do_an;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an.fragments.HomeFragment;
import com.example.do_an.fragments.SettingFragment;
import com.example.do_an.fragments.saveFragment;
import com.example.do_an.fragments.shareFragment;
import com.example.do_an.fragments.userFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    BottomNavigationView mnBottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        mnBottom = findViewById(R.id.navBottom);

        //
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Main");
        actionBar.setDisplayHomeAsUpEnabled(false);

        // load lên fragment
        mnBottom.setOnItemSelectedListener(getListener());
        // Load HomeFragment mặc định khi mở app
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        mAuth = FirebaseAuth.getInstance();





    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()== android.R.id.home)
        {
            finish();
            return true;
        }
        return true;
    }

    @NonNull
    private NavigationBarView.OnItemSelectedListener getListener() {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fmNew;

                if (item.getItemId() == R.id.food) {
                    getSupportActionBar().setTitle(item.getTitle());
                    fmNew = new HomeFragment();
                    loadFragment(fmNew);
                    return true;
                }
                if (item.getItemId() == R.id.share) {
                    getSupportActionBar().setTitle(item.getTitle());
                    fmNew = new userFragment();
                    loadFragment(fmNew);
                    return true;
                }if (item.getItemId() == R.id.setting) {
                    getSupportActionBar().setTitle(item.getTitle());
                    fmNew = new SettingFragment();
                    loadFragment(fmNew);
                    return true;
                }
                return true;
            }
        };
    }

    // hàm load fragment
    void loadFragment(Fragment fmNew)
    {
        FragmentTransaction fmTran = getSupportFragmentManager().beginTransaction();
        fmTran.replace(R.id.fragmentContainer, fmNew);
        fmTran.addToBackStack(null);
        fmTran.commit();
    }
}