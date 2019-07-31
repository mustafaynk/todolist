package com.ynk.todolist.Listeners;

import android.view.MenuItem;
import android.view.View;

//RecyclerView itemClick Interface
public interface RecyclerListClickListener {

    void itemClick(View view, Object item, int position);

    void longItemClick(View view, Object item, int position);

    void moreItemClick(View view, Object item, int position, MenuItem menuItem);

}
