package com.example.do_an;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.do_an.adapter.SearchAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    SearchView searchView;
    RecyclerView recyclerView;
    ArrayList<FoodItem> arrayList = new ArrayList<>();
    ArrayList<SearchItem> searchList = new ArrayList<>();
    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        loadDataFromFirebase();
        handleSearch();
    }

    private void addControl() {
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(this, new ArrayList<>()); // Khởi tạo rỗng
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear(); // Xóa danh sách cũ

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Tạo một đối tượng FoodItem từ dữ liệu Firebase
                    FoodItem foodItem = snapshot.getValue(FoodItem.class);

                    // Kiểm tra nếu dữ liệu hợp lệ thì thêm vào danh sách
                    if (foodItem != null) {
                        // Kiểm tra nếu category bị null thì gán giá trị mặc định
                        if (foodItem.getCategory() == null) {
                            foodItem.setCategory("Chưa phân loại"); // ✅ Gán category mặc định
                        }
                        arrayList.add(foodItem);
                    }
                }

                searchAdapter.updateData(arrayList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu!", error.toException());
                Toast.makeText(SearchActivity.this, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void handleSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
    }

    private void filterList(String query) {
        ArrayList<FoodItem> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(arrayList);
        } else {
            for (FoodItem item : arrayList) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        searchAdapter.updateData(filteredList);
    }
}
