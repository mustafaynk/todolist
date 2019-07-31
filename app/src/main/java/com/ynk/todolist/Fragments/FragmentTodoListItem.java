package com.ynk.todolist.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.ynk.todolist.Adapters.AdapterTodoListItem;
import com.ynk.todolist.Database.AppDatabase;
import com.ynk.todolist.Database.DAO;
import com.ynk.todolist.Listeners.RecyclerListItemClick;
import com.ynk.todolist.Model.Filter;
import com.ynk.todolist.Model.TodoListItem;
import com.ynk.todolist.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import muyan.snacktoa.SnackToa;


public class FragmentTodoListItem extends Fragment implements AAH_FabulousFragment.Callbacks {

    private DAO dao;

    private SimpleDateFormat sdf;
    private long mLastClickTime = 0;
    private Long listId;
    private String listName, lastSearch = "";

    private View llEmptyBox;
    private List<TodoListItem> todoListItems, searchedLists;
    private AdapterTodoListItem adapterTodoListItem;

    private Filter filter;
    private FragmentTodoListItemFilter filterDialog;

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            System.out.println("New Text:" + newText);
            searchedLists.clear();
            adapterTodoListItem.notifyDataSetChanged();
            for (TodoListItem pp : todoListItems) {
                if (pp.getListItemName().toUpperCase().contains(newText.toUpperCase(new Locale("tr")))) {
                    searchedLists.add(pp);
                }
            }
            adapterTodoListItem.notifyDataSetChanged();
            lastSearch = newText;
            return false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listId = getArguments().getLong("listId");
            listName = getArguments().getString("listName");
        }
        setHasOptionsMenu(true);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dao = AppDatabase.getDb(getActivity()).getDAO();

        todoListItems = new ArrayList<>();
        searchedLists = new ArrayList<>();
        adapterTodoListItem = new AdapterTodoListItem(searchedLists, new RecyclerListItemClick() {
            @Override
            public void endTask(View view, Object item, int position) {
                if (mLastClickTime - System.currentTimeMillis() > 2000)
                    return;
                mLastClickTime = System.currentTimeMillis();
                final TodoListItem todoListItem = (TodoListItem) item;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.todoListItemEndTask));
                builder.setMessage(getString(R.string.todoListItemEndTaskMessage));
                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        todoListItem.setListItemStatusCode(1);
                        todoListItem.setExpanded(false);
                        dao.insertTodoListItem(todoListItem);
                        getTodoListItems();
                        SnackToa.snackBarSuccess(getActivity(), getString(R.string.todoListItemStatusMessage));
                    }
                });
                builder.setNegativeButton(R.string.CANCEL, null);
                builder.show();
            }

            @Override
            public void editTask(View view, Object item, int position) {
                if (mLastClickTime - System.currentTimeMillis() > 2000)
                    return;
                mLastClickTime = System.currentTimeMillis();
                TodoListItem todoListItem = (TodoListItem) item;
                showAddListItemDialog(todoListItem);
            }

            @Override
            public void deleteTask(View view, final Object item, int position) {
                if (mLastClickTime - System.currentTimeMillis() > 2000)
                    return;
                mLastClickTime = System.currentTimeMillis();
                final TodoListItem todoListItem = (TodoListItem) item;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.todoListItemDeleteTask));
                builder.setMessage(getString(R.string.todoListItemDeleteTaskMessage));
                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        dao.deleteTodoListItem(todoListItem.getListItemId());
                        getTodoListItems();
                        SnackToa.snackBarSuccess(getActivity(), getString(R.string.todoListItemDeleteMessage));
                    }
                });
                builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todolistitem, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setTitle(getString(R.string.todoListPageTitle, getString(R.string.todolistItemTitle, listName)));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.todolistItemTitle, listName));

        llEmptyBox = view.findViewById(R.id.llEmptyBox);
        RecyclerView recyclerViewTodoList = view.findViewById(R.id.recyclerViewTodoListItems);
        recyclerViewTodoList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTodoList.setHasFixedSize(true);
        recyclerViewTodoList.setAdapter(adapterTodoListItem);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fabNewListItem);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddListItemDialog(null);
            }
        });

        FloatingActionButton fabFilter = view.findViewById(R.id.fabFilter);
        filterDialog = FragmentTodoListItemFilter.newInstance();
        filterDialog.setParentFab(fabFilter);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.setCallbacks(FragmentTodoListItem.this);
                Bundle bundle = new Bundle();
                bundle.putString("filter", new Gson().toJson(filter));
                filterDialog.setArguments(bundle);
                filterDialog.show(getActivity().getSupportFragmentManager(), filterDialog.getTag());
            }
        });

        getTodoListItems();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_todolistitem, menu);
        ImageButton orderButton = (ImageButton) menu.findItem(R.id.action_order).getActionView();
        orderButton.setImageResource(R.drawable.ic_order);
        orderButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 20;
        orderButton.setLayoutParams(params);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        List<TodoListItem> todoListItems1 = new ArrayList<>();
                        switch (item.getItemId()) {
                            case R.id.action_createdate:
                                todoListItems1.addAll(dao.getTodoListItemOrder(String.valueOf(listId), "listItemCreateDate"));
                                break;
                            case R.id.action_deadline:
                                todoListItems1.addAll(dao.getTodoListItemOrder(String.valueOf(listId), "listItemDeadline"));
                                break;
                            case R.id.action_name:
                                todoListItems1.addAll(dao.getTodoListItemOrder(String.valueOf(listId), "listItemName"));
                                break;
                            case R.id.action_status:
                                todoListItems1.addAll(dao.getTodoListItemOrder(String.valueOf(listId), "listItemStatusCode"));
                                break;
                        }
                        todoListItems.clear();
                        searchedLists.clear();
                        if (todoListItems1.isEmpty())
                            llEmptyBox.setVisibility(View.VISIBLE);
                        else {
                            llEmptyBox.setVisibility(View.GONE);
                            todoListItems.addAll(todoListItems1);
                            searchedLists.addAll(todoListItems1);
                        }
                        adapterTodoListItem.notifyDataSetChanged();
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.menu_todolistitem_order);
                popupMenu.show();
            }
        });

        MenuItem searchItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.todoListItemSearch));
        searchView.setOnQueryTextListener(searchListener);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchedLists.clear();
                searchedLists.addAll(todoListItems);
                adapterTodoListItem.notifyDataSetChanged();
                return false;
            }
        });
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        if (lastSearch != null && !lastSearch.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(lastSearch, false);
        }
    }

    private void getTodoListItems() {
        todoListItems.clear();
        searchedLists.clear();
        List<TodoListItem> todoListItems1 = dao.getTodoListItems(String.valueOf(listId));
        if (todoListItems1.isEmpty())
            llEmptyBox.setVisibility(View.VISIBLE);
        else {
            llEmptyBox.setVisibility(View.GONE);
            todoListItems.addAll(todoListItems1);
            searchedLists.addAll(todoListItems1);
            adapterTodoListItem.notifyDataSetChanged();
        }
    }

    private void showAddListItemDialog(final TodoListItem todoListItem) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_new_list_item);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText etListItemName = dialog.findViewById(R.id.etListItemName);
        final EditText etListItemDesc = dialog.findViewById(R.id.etListItemDesc);
        final TextView tvListItemDeadline = dialog.findViewById(R.id.tvListItemDeadline);
        final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
        final Button buttonSave = dialog.findViewById(R.id.buttonSave);

        if (todoListItem != null) {
            etListItemName.setText(todoListItem.getListItemName());
            etListItemDesc.setText(todoListItem.getListItemDesc());
            tvListItemDeadline.setText(sdf.format(todoListItem.getListItemDeadline()));
            tvHeader.setText(getString(R.string.todoListItemDialogHeaderUpdateItem));
            buttonSave.setText(getString(R.string.todoListItemDialogSubmitUpdate));
        } else {
            etListItemName.getText().clear();
            etListItemDesc.getText().clear();
            tvListItemDeadline.setText(sdf.format(Calendar.getInstance().getTime()));
            tvHeader.setText(getString(R.string.todoListItemDialogHeaderNewItem));
            buttonSave.setText(getString(R.string.todoListItemDialogSubmitNew));
        }


        tvListItemDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar cur_calender = Calendar.getInstance();
                cur_calender.add(Calendar.DAY_OF_YEAR, 1);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                ((TextView) v).setText(sdf.format(calendar.getTime()));
                            }
                        },
                        cur_calender.get(Calendar.YEAR),
                        cur_calender.get(Calendar.MONTH),
                        cur_calender.get(Calendar.DAY_OF_MONTH)
                );
                datePicker.setThemeDark(false);
                datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
                datePicker.setMinDate(cur_calender);
                datePicker.show(getActivity().getFragmentManager(), "Deadline");
            }
        });

        dialog.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etListItemName.getText().toString().trim())) {
                    etListItemName.setError(getString(R.string.todolistAddListItemNameError));
                    return;
                }
                if (TextUtils.isEmpty(etListItemDesc.getText().toString().trim())) {
                    etListItemDesc.setError(getString(R.string.todolistAddListItemDescError));
                    return;
                }
                TodoListItem todoListItemNew;
                String message = "";
                if (todoListItem != null) {
                    todoListItemNew = todoListItem;
                    message = getString(R.string.todoListItemUpdateMessage);
                } else {
                    todoListItemNew = new TodoListItem();
                    message = getString(R.string.todoListItemCreateMessage);
                }
                todoListItemNew.setListId(String.valueOf(listId));
                todoListItemNew.setListItemName(etListItemName.getText().toString());
                todoListItemNew.setListItemDesc(etListItemDesc.getText().toString());
                try {
                    todoListItemNew.setListItemDeadline(sdf.parse(tvListItemDeadline.getText().toString()));
                } catch (ParseException ignored) {
                }
                todoListItemNew.setListItemStatusCode(0);
                todoListItemNew.setExpanded(false);
                todoListItemNew.setListItemCreateDate(new Date());
                dao.insertTodoListItem(todoListItemNew);
                dialog.dismiss();
                getTodoListItems();
                SnackToa.snackBarSuccess(getActivity(), message);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    @Override
    public void onResult(Object result) {
        if (result == null || result.equals(""))
            return;

        if (result.equals("clear") || result.equals("swiped_down")) {
            filter = null;
            getTodoListItems();
        } else {
            List<TodoListItem> todoListItems1 = new ArrayList<>();
            String status = "";
            filter = (Filter) result;

            if (filter.isCompleted() && filter.isContinued()) status = "0, 1";
            else if (filter.isCompleted()) status = "1";
            else if (filter.isContinued()) status = "0";

            if (!status.equals("") && filter.isExpired()) {
                todoListItems1.addAll(dao.getTodoListItemFilterStExp(String.valueOf(listId), status, sdf.format(new Date())));
            } else if (!status.equals("")) {
                todoListItems1.addAll(dao.getTodoListItemFilterSt(String.valueOf(listId), status));
            } else if (filter.isExpired()) {
                todoListItems1.addAll(dao.getTodoListItemFilterEx(String.valueOf(listId), sdf.format(new Date())));
            }

            todoListItems.clear();
            searchedLists.clear();
            if (todoListItems1.isEmpty())
                llEmptyBox.setVisibility(View.VISIBLE);
            else {
                llEmptyBox.setVisibility(View.GONE);
                todoListItems.addAll(todoListItems1);
                searchedLists.addAll(todoListItems1);
            }
            adapterTodoListItem.notifyDataSetChanged();
        }
    }
}