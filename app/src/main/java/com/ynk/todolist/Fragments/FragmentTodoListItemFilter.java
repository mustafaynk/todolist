package com.ynk.todolist.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.gson.Gson;
import com.ynk.todolist.Model.Filter;
import com.ynk.todolist.R;


public class FragmentTodoListItemFilter extends AAH_FabulousFragment implements View.OnClickListener {

    private Filter filter;

    public static FragmentTodoListItemFilter newInstance() {
        return new FragmentTodoListItemFilter();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.dialog_filter, null);

        RelativeLayout rl_content = contentView.findViewById(R.id.rlContent);
        LinearLayout ll_buttons = contentView.findViewById(R.id.llButtons);

        final Button btnFilterCompleted, btnFilterContinued, btnFilterExpired;
        ImageButton btnClearSelection, btnFilter;
        btnFilterCompleted = contentView.findViewById(R.id.btnFilterCompleted);
        btnFilterContinued = contentView.findViewById(R.id.btnFilterContinued);
        btnFilterExpired = contentView.findViewById(R.id.btnFilterExpired);
        btnClearSelection = contentView.findViewById(R.id.btnClearSelection);
        btnFilter = contentView.findViewById(R.id.btnFilter);

        if (getArguments() != null)
            filter = new Gson().fromJson(getArguments().getString("filter"), Filter.class);

        btnFilterCompleted.setOnClickListener(this);
        btnFilterContinued.setOnClickListener(this);
        btnFilterExpired.setOnClickListener(this);

        btnClearSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("clear");
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filter filter = new Filter();
                filter.setCompleted(btnFilterCompleted.isSelected());
                filter.setContinued(btnFilterContinued.isSelected());
                filter.setExpired(btnFilterExpired.isSelected());
                closeFilter(filter);
            }
        });

        if (filter != null) {
            if (filter.isExpired()) btToggleClick(btnFilterExpired);
            if (filter.isCompleted()) btToggleClick(btnFilterCompleted);
            if (filter.isContinued()) btToggleClick(btnFilterContinued);
        }

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setAnimationDuration(600);
        setPeekHeight(200);
        setViewgroupStatic(ll_buttons);
        setViewMain(rl_content);
        setMainContentView(contentView);
        super.setupDialog(dialog, style);
    }

    public void btToggleClick(View view) {
        if (view instanceof Button) {
            Button b = (Button) view;
            if (b.isSelected()) {
                b.setTextColor(getResources().getColor(R.color.grey_40));
            } else {
                b.setTextColor(Color.WHITE);
            }
            b.setSelected(!b.isSelected());
        }
    }

    @Override
    public void onClick(View v) {
        btToggleClick(v);
    }
}
