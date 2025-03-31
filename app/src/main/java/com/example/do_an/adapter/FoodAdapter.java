package com.example.do_an.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_an.FoodItem;
import com.example.do_an.R;
import com.example.do_an.RecipeActivity;
import com.example.do_an.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    Context context;
    ArrayList<FoodItem> listFood;
//    ImageButton btnSave;
    private Map<String, Boolean> savedPosts = new HashMap<>();

    public FoodAdapter(ArrayList<FoodItem> listFood) {
        this.listFood = (listFood != null) ? listFood : new ArrayList<>();
    }

    public void setListFood(ArrayList<FoodItem> listFood) {
        this.listFood = (listFood != null) ? listFood : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Nap layout cho view bieu dien phan tu user
        View foodView = inflater.inflate(R.layout.foodsave, parent, false);


        FoodViewHolder foodViewHolder = new FoodViewHolder(foodView);
        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.FoodViewHolder holder, int position) {
        FoodItem foodItem = listFood.get(position);

        // Hiển thị thông tin món ăn
        holder.foodName.setText(foodItem.getTitle());
        holder.foodDes.setText(foodItem.getSteps());

        // Tải ảnh bằng Glide
        Glide.with(context)
                .load(foodItem.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.foodImg);

        // Xử lý khi nhấn vào item để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("postId", foodItem.getPostId());
            intent.putExtra("title", foodItem.getTitle());
            intent.putExtra("time", foodItem.getCookingTime());
            context.startActivity(intent);
        });

        // Kiểm tra người dùng đã đăng nhập chưa
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            holder.btnSave.setVisibility(View.GONE); // Nếu chưa đăng nhập, ẩn nút Save
            return;
        }

        DatabaseReference savedRef = FirebaseDatabase.getInstance()
                .getReference("SavedPosts")
                .child(user.getUid())
                .child(foodItem.getPostId());

        // Kiểm tra xem công thức đã được lưu chưa
        savedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isSaved = snapshot.exists();
                holder.btnSave.setImageResource(isSaved ? R.drawable.baseline_bookmark_24 : R.drawable.baseline_bookmark_border_24);

                // Xử lý sự kiện click nút Save
                holder.btnSave.setOnClickListener(v -> {
                    if (isSaved) {
                        // Nếu đã lưu, xóa khỏi Firebase và cập nhật giao diện ngay lập tức
                        savedRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    holder.btnSave.setImageResource(R.drawable.baseline_bookmark_border_24);
                                    Toast.makeText(context, "Đã xóa khỏi danh sách lưu!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        // Nếu chưa lưu, thêm vào Firebase và cập nhật giao diện ngay lập tức
                        savedRef.setValue(true)
                                .addOnSuccessListener(aVoid -> {
                                    holder.btnSave.setImageResource(R.drawable.baseline_bookmark_24);
                                    Toast.makeText(context, "Đã lưu công thức!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi khi kiểm tra trạng thái lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return (listFood != null) ? listFood.size() : 0;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder{
        ImageView foodImg;
        TextView foodName, foodDes;
        ImageButton btnSave;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.Item_Name);
            foodImg = itemView.findViewById(R.id.Item_Img);
            foodDes = itemView.findViewById(R.id.CongThuc);
            btnSave = itemView.findViewById(R.id.btnSave);

        }
    }
    // Hàm lưu/xóa bài viết khỏi danh sách đã lưu
    private void toggleSavePost(FoodItem foodItem, FoodViewHolder holder) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "Bạn cần đăng nhập để lưu công thức!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference savedRef = FirebaseDatabase.getInstance()
                .getReference("SavedPosts")
                .child(user.getUid())
                .child(foodItem.getPostId());

        boolean isCurrentlySaved = savedPosts.getOrDefault(foodItem.getPostId(), false);
        if (isCurrentlySaved) {
            // Nếu đã lưu → xóa khỏi danh sách
            savedRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        savedPosts.put(foodItem.getPostId(), false);
                        holder.btnSave.setImageResource(R.drawable.baseline_bookmark_border_24);
                        Toast.makeText(context, "Đã xóa khỏi danh sách đã lưu!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } else {
            // Nếu chưa lưu → thêm vào danh sách đã lưu
            savedRef.setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        savedPosts.put(foodItem.getPostId(), true);
                        holder.btnSave.setImageResource(R.drawable.baseline_bookmark_24);
                        Toast.makeText(context, "Đã lưu công thức!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}

