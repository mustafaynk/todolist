package com.ynk.todolist.Listeners;

import android.view.View;

public interface RecyclerListItemClick {

    void endTask(View view, Object item, int position);
    void editTask(View view, Object item, int position);
    void deleteTask(View view, Object item, int position);

}
