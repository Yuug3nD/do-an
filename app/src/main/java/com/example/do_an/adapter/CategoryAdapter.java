package com.example.do_an.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_an.R;
import com.example.do_an.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewModel> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public static OnCategoryClickListener mListener;

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.equals(newItem);
        }
    };

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        mListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_category, parent, false);
        return new CategoryViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewModel holder, int position) {
        Category category = getItem(position);
        holder.bind(category);
    }

    public class CategoryViewModel extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView img;
        public CategoryViewModel(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name_category);
            img = itemView.findViewById(R.id.img_category);
            itemView.setOnClickListener(v -> mListener.onCategoryClick(getItem(getAdapterPosition())));
        }

        public void bind(Category category){
            tvName.setText(category.getName());
            Glide.with(itemView.getContext())
                    .load(category.getImage())
                    .into(img);
        }
    }
}

