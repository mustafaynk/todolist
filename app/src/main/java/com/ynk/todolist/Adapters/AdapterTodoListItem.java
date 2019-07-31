package com.ynk.todolist.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ynk.todolist.Listeners.RecyclerListItemClick;
import com.ynk.todolist.Model.TodoListItem;
import com.ynk.todolist.R;
import com.ynk.todolist.Tools.Tools;
import com.ynk.todolist.Tools.ViewAnimation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterTodoListItem extends RecyclerView.Adapter<AdapterTodoListItem.ViewHolder> {

    private RecyclerListItemClick clickListener;
    private List<TodoListItem> items;
    private SimpleDateFormat sdf;
    private Context context;

    public AdapterTodoListItem(List<TodoListItem> items, RecyclerListItemClick clickListener) {
        this.items = items;
        this.clickListener = clickListener;
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvItemStatus;
        TextView tvItemDeadlineDate;
        ProgressBar progressDeadline;
        TextView tvItemDesc;
        TextView tvItemCreateDate;
        TextView tvRemainingDay;
        View layoutEnded;
        View layoutEdit;
        View layoutDelete;
        View parentView;
        View expandView;
        View ivExpandLogo;
        View llActionLayout;

        ViewHolder(View v) {
            super(v);
            tvItemName = v.findViewById(R.id.tvItemName);
            tvItemStatus = v.findViewById(R.id.tvItemStatus);
            tvItemDeadlineDate = v.findViewById(R.id.tvItemDeadlineDate);
            progressDeadline = v.findViewById(R.id.progressDeadline);
            tvItemDesc = v.findViewById(R.id.tvItemDesc);
            tvItemCreateDate = v.findViewById(R.id.tvItemCreateDate);
            layoutEnded = v.findViewById(R.id.layoutEnded);
            layoutEdit = v.findViewById(R.id.layoutEdit);
            layoutDelete = v.findViewById(R.id.layoutDelete);
            parentView = v.findViewById(R.id.parentView);
            expandView = v.findViewById(R.id.expandView);
            ivExpandLogo = v.findViewById(R.id.ivExpandLogo);
            llActionLayout = v.findViewById(R.id.llActionLayout);
            tvRemainingDay = v.findViewById(R.id.tvRemainingDay);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todolistitem, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TodoListItem todoListItem = getItem(position);
        holder.tvItemName.setText(todoListItem.getListItemName());
        holder.tvItemDeadlineDate.setText(sdf.format(todoListItem.getListItemDeadline()));
        holder.tvItemCreateDate.setText(context.getString(R.string.todoListItemCreateDate, sdf.format(todoListItem.getListItemCreateDate())));
        holder.tvItemDesc.setText(context.getString(R.string.todoListItemDesc, todoListItem.getListItemDesc()));

        holder.layoutEnded.setTag(position);
        holder.layoutEnded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.endTask(v, todoListItem, (int) v.getTag());
            }
        });


        Calendar remainingTime = Calendar.getInstance();
        remainingTime.setTimeInMillis(todoListItem.getListItemDeadline().getTime() - Calendar.getInstance().getTimeInMillis());


        Calendar taskTime = Calendar.getInstance();
        taskTime.setTimeInMillis(todoListItem.getListItemDeadline().getTime() - todoListItem.getListItemCreateDate().getTime());

        int progress = (100 * remainingTime.get(Calendar.DAY_OF_YEAR)) / taskTime.get(Calendar.DAY_OF_YEAR);
        holder.progressDeadline.setProgress(progress);

        holder.layoutEdit.setTag(position);
        holder.layoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.editTask(v, todoListItem, (int) v.getTag());
            }
        });


        holder.layoutDelete.setTag(position);
        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.deleteTask(v, todoListItem, (int) v.getTag());
            }
        });


        holder.parentView.setTag(position);
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = toggleLayoutExpand(!todoListItem.isExpanded(), holder.ivExpandLogo, holder.expandView);
                todoListItem.setExpanded(show);
            }
        });

        if (todoListItem.getListItemStatusCode() != 0) {
            holder.progressDeadline.setVisibility(View.GONE);
            holder.llActionLayout.setVisibility(View.GONE);
            holder.tvRemainingDay.setVisibility(View.GONE);
            holder.tvItemStatus.setText(context.getString(R.string.todoListItemStatusCompleted));
            holder.parentView.setBackgroundColor(context.getResources().getColor(R.color.overlay_green_10));
        } else {
            holder.progressDeadline.setVisibility(View.VISIBLE);
            holder.llActionLayout.setVisibility(View.VISIBLE);
            holder.tvRemainingDay.setVisibility(View.VISIBLE);
            holder.tvRemainingDay.setText(context.getString(R.string.todoListItemRemainingDate, String.valueOf(remainingTime.get(Calendar.DAY_OF_YEAR))));
            if (remainingTime.before(Calendar.getInstance())) {
                holder.tvItemStatus.setText(context.getString(R.string.todoListItemStatusContinue));
                holder.tvRemainingDay.setTextColor(context.getResources().getColor(R.color.grey_40));
            } else {
                holder.tvItemStatus.setText(context.getString(R.string.todoListItemStatusExpired));
                holder.tvRemainingDay.setTextColor(context.getResources().getColor(R.color.red_500));
            }
            holder.parentView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        if (todoListItem.isExpanded()) {
            holder.expandView.setVisibility(View.VISIBLE);
        } else {
            holder.expandView.setVisibility(View.GONE);
        }

        Tools.toggleArrow(todoListItem.isExpanded(), holder.ivExpandLogo, false);
    }

    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    private TodoListItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}