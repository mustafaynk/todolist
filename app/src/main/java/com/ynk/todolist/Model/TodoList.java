package com.ynk.todolist.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "todolist")
public class TodoList implements Serializable {

    @PrimaryKey
    private Long listId;

    @ColumnInfo
    private String userId;

    @ColumnInfo
    private String listName;

    @ColumnInfo
    private int listPriority;

    @ColumnInfo
    private Date listAddDate;


    private boolean swiped = false;


    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getListPriority() {
        return listPriority;
    }

    public void setListPriority(int listPriority) {
        this.listPriority = listPriority;
    }

    public Date getListAddDate() {
        return listAddDate;
    }

    public void setListAddDate(Date listAddDate) {
        this.listAddDate = listAddDate;
    }

    public boolean isSwiped() {
        return swiped;
    }

    public void setSwiped(boolean swiped) {
        this.swiped = swiped;
    }

}
