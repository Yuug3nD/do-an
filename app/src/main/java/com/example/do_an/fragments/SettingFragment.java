package com.example.do_an.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.do_an.LoginActivity;
import com.example.do_an.MainActivity;
import com.example.do_an.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SettingFragment extends Fragment {
private TextView idUser, idVersion, tvLogin;
private ImageButton btnLogIn, btnLogOut, btnTerms;
private  FirebaseAuth mAuthLogout;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idUser = view.findViewById(R.id.id_User);
        idVersion = view.findViewById(R.id.id_version);
        btnTerms = view.findViewById(R.id.btn_DieuKhoan);
        tvLogin = view.findViewById(R.id.text_DangNhap);
        btnLogIn = view.findViewById(R.id.btn_DangNhap);
        btnLogOut = view.findViewById(R.id.btn_Logout);
        mAuthLogout = FirebaseAuth.getInstance();

        updateUI(mAuthLogout.getCurrentUser());

        btnLogIn.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        btnLogOut.setOnClickListener(v -> showLogoutDialog());
        btnTerms.setOnClickListener(v -> showTermsDialog());

        try {
            String versionName = requireActivity()
                    .getPackageManager()
                    .getPackageInfo(requireActivity().getPackageName(), 0).versionName;
            idVersion.setText("v" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String displayName = user.getDisplayName();
            tvLogin.setText(displayName != null && !displayName.isEmpty() ? displayName : user.getEmail());
            idUser.setText(user.getUid());

            btnLogIn.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.VISIBLE);
        } else {
            idUser.setText("Bạn chưa đăng nhập");
            btnLogIn.setVisibility(View.VISIBLE);
            btnLogOut.setVisibility(View.GONE);
        }
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> logoutUser())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logoutUser() {
        mAuthLogout.signOut();
        updateUI(null);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void showTermsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Điều khoản sử dụng");

        TextView textView = new TextView(requireActivity());
        textView.setText("Đây là điều khoản sử dụng ứng dụng của chúng tôi. \n\n" +
                "1. Bạn đồng ý không sử dụng ứng dụng cho mục đích bất hợp pháp. \n" +
                "2. Chúng tôi không chịu trách nhiệm về dữ liệu của bạn. \n" +
                "3. Ứng dụng có thể được cập nhật mà không cần thông báo trước. \n" +
                "4. Nếu bạn có câu hỏi, vui lòng liên hệ với chúng tôi.");

        textView.setPadding(50, 20, 50, 20);
        textView.setTextSize(16);

        ScrollView scrollView = new ScrollView(requireActivity());
        scrollView.addView(textView);

        builder.setView(scrollView);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

}