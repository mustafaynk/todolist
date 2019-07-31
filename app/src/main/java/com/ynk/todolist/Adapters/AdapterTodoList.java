package com.ynk.todolist.Adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ynk.todolist.Listeners.RecyclerListClickListener;
import com.ynk.todolist.Model.TodoList;
import com.ynk.todolist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterTodoList extends RecyclerView.Adapter<AdapterTodoList.ViewHolder> {

    private RecyclerListClickListener clickListener;
    private List<TodoList> items;
    private SimpleDateFormat sdf;
    private int[] colors;
    private Context context;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public AdapterTodoList(Context context, List<TodoList> items, RecyclerListClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        selected_items = new SparseBooleanArray();

        TypedArray ta = context.getResources().obtainTypedArray(R.array.listItemColors);
        colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvListName;
        TextView tvListAddDate;
        ImageView ibMore;
        ImageView ivCircleView;
        View parentView;
        View layoutChecked;

        ViewHolder(View v) {
            super(v);
            tvListName = v.findViewById(R.id.tvListName);
            tvListAddDate = v.findViewById(R.id.tvListAddDate);
            ibMore = v.findViewById(R.id.ibMore);
            ivCircleView = v.findViewById(R.id.ivCircleView);
            parentView = v.findViewById(R.id.parentView);
            layoutChecked = v.findViewById(R.id.layoutChecked);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todolist, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TodoList todoList = getItem(position);
        holder.tvListName.setText(todoList.getListName());
        holder.tvListAddDate.setText(sdf.format(todoList.getListAddDate()));

        holder.ivCircleView.setColorFilter(colors[todoList.getListPriority()], PorterDuff.Mode.SRC_ATOP);
        //holder.ivCircleView.setBackgroundColor(colors[todoList.getListPriority()]);

        holder.parentView.setActivated(selected_items.get(position, false));

        holder.parentView.setTag(position);
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.itemClick(v, todoList, (int) v.getTag());
            }
        });


        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clickListener == null) return false;
                clickListener.longItemClick(v, todoList, (int) v.getTag());
                return true;
            }
        });

        holder.ibMore.setTag(position);
        holder.ibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                onMoreButtonClick(v, todoList);
            }
        });

        toggleCheckedIcon(holder, position);
    }

    private void onMoreButtonClick(final View view, final TodoList todoList) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                clickListener.moreItemClick(view, todoList, (int) view.getTag(), item);
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_todolist_more);
        popupMenu.show();
    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.layoutChecked.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.layoutChecked.setVisibility(View.GONE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        items.remove(position);
        resetCurrentIndex();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    private TodoList getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}