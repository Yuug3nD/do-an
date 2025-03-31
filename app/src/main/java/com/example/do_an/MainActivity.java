package com.example.do_an;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    BottomNavigationView mnBottom;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        mnBottom = findViewById(R.id.navBottom);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view); // Đảm bảo navigationView không bị null

        if (navigationView == null) {
            Log.e("MainActivity", "NavigationView is NULL! Kiểm tra ID trong XML.");
        } else {
            navigationView.setNavigationItemSelectedListener(getDrawerListener());
            Menu menu = navigationView.getMenu();
            MenuItem versionItem = menu.findItem(R.id.nav_Version);

            // Lấy phiên bản ứng dụng từ hệ thống
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                versionItem.setTitle("App Version: " + versionName);
            } catch (PackageManager.NameNotFoundException e) {
                versionItem.setTitle("App Version: ?");
                e.printStackTrace();
            }

            MenuItem logoutItem = menu.findItem(R.id.nav_logout);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            logoutItem.setVisible(user != null);
        }

        mnBottom.setOnItemSelectedListener(getListener());

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
    @NonNull
    private NavigationView.OnNavigationItemSelectedListener getDrawerListener() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_login) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return true;
                }
                if (item.getItemId() == R.id.nav_DieuKhoan) {
                    showTermsDialog(); // Hiển thị dialog điều khoản
                    return true;
                }
                if (item.getItemId() == R.id.nav_logout) {
                   showLogoutDialog();
                    return true;
                }
                if (item.getItemId() == R.id.nav_Version) {
                    return true;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        };
    }
    private void showTermsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Điều khoản sử dụng");

        TextView textView = new TextView(this);
        textView.setText("Đây là điều khoản sử dụng ứng dụng của chúng tôi. \n\n" +
                "1. Bạn đồng ý không sử dụng ứng dụng cho mục đích bất hợp pháp. \n" +
                "2. Chúng tôi không chịu trách nhiệm về dữ liệu của bạn. \n" +
                "3. Ứng dụng có thể được cập nhật mà không cần thông báo trước. \n" +
                "4. Nếu bạn có câu hỏi, vui lòng liên hệ với chúng tôi.");

        textView.setPadding(50, 20, 50, 20);
        textView.setTextSize(16);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(textView);

        builder.setView(scrollView);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> logoutUser())
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        logoutItem.setVisible(false);


        drawerLayout.closeDrawer(GravityCompat.START);

        loadFragment(new HomeFragment());

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