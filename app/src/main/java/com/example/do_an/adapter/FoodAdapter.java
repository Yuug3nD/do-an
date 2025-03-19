package com.example.do_an.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_an.FoodItem;
import com.example.do_an.R;
import com.example.do_an.RecipeActivity;
import com.example.do_an.Utils;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    Context context;
    ArrayList<FoodItem> listFood;
    ImageButton btnSave;

    public FoodAdapter(ArrayList<FoodItem> listFood) {
        this.listFood = (listFood != null) ? listFood : new ArrayList<>();
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

        holder.foodImg.setImageBitmap(Utils.convertToBitmapFromAssets(context,foodItem.getImage()));
        holder.foodName.setText(foodItem.getTitle());
        holder.foodDes.setText(foodItem.getIngredients());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("postId", foodItem.getPostId());  // Truyền postId sang RecipeActivity
            intent.putExtra("title", foodItem.getTitle());
            intent.putExtra("time", foodItem.getCookingTime());
            context.startActivity(intent);
        });
        FoodItem food = listFood.get(position);
        holder.foodName.setText(food.getTitle());
        holder.foodDes.setText(food.getSteps());

        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(context)
                .load(food.getImage())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh mặc định khi tải
                .into(holder.foodImg);

        // Xử lý sự kiện click nút lưu (chưa triển khai)
//        holder.btnSave.setOnClickListener(v -> {
//            // TODO: Thêm logic để lưu công thức vào danh sách yêu thích
//        });
    }

    @Override
    public int getItemCount() {
        return (listFood != null) ? listFood.size() : 0;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder{
        ImageView foodImg;
        TextView foodName, foodDes;
        ImageButton btbSave;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.Item_Name);
            foodImg = itemView.findViewById(R.id.Item_Img);
            foodDes = itemView.findViewById(R.id.CongThuc);
            btbSave = itemView.findViewById(R.id.btnSave);

        }
    }
}

