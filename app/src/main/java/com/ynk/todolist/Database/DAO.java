package com.ynk.todolist.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ynk.todolist.Model.TodoList;
import com.ynk.todolist.Model.TodoListItem;
import com.ynk.todolist.Model.User;

import java.util.List;

@Dao
public interface DAO {

    /* table notification transaction ----------------------------------------------------------- */

    //Insert Querys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTodoList(TodoList todoList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTodoListItem(TodoListItem todoListItem);

    //Delete Querys
    @Query("DELETE FROM todolist WHERE listId = :listId")
    void deleteTodoList(Long listId);

    @Query("DELETE FROM todolistitem WHERE listId = :listId")
    void deleteTodoListItemsByListId(Long listId);

    @Query("DELETE FROM todolistitem WHERE listItemId = :listId")
    void deleteTodoListItem(Long listId);

    //Select Querys
    @Query("SELECT * FROM user WHERE userName = :userName AND userPassword = :password")
    User login(String userName, String password);

    @Query("SELECT * FROM user WHERE userName = :userName")
    User loginControl(String userName);

    @Query("SELECT COUNT(*) FROM user WHERE userName = :userName OR userMail = :userMail")
    Integer signUpControl(String userName, String userMail);

    @Query("SELECT COUNT(todolistitem.listId) FROM todolist " +
            " LEFT JOIN todolistitem ON todolistitem.listId = todolist.listId " +
            " WHERE todolist.userId = :userId AND CASE :countType " +
            "WHEN '0' THEN todolistitem.listItemStatusCode = 0 " +
            "WHEN '1' THEN todolistitem.listItemStatusCode = 1 " +
            "WHEN '-1' THEN todolistitem.listItemDeadline > :expiry " +
            "END")
    int getTaskCount(Long userId, String countType, String expiry);

    @Query("SELECT todolist.* FROM todolist WHERE userId = :userId")
    List<TodoList> getTodolist(String userId);

    @Query("SELECT * FROM todolistitem WHERE listId = :listId")
    List<TodoListItem> getTodoListItems(String listId);

    //Filters
    @Query("SELECT * FROM todolistitem WHERE listId = :listId AND listItemStatusCode in(:status) AND listItemDeadline > :expiry")
    List<TodoListItem> getTodoListItemFilterStExp(String listId, String status, String expiry);

    @Query("SELECT * FROM todolistitem WHERE listId = :listId AND listItemStatusCode in(:status)")
    List<TodoListItem> getTodoListItemFilterSt(String listId, String status);

    @Query("SELECT * FROM todolistitem WHERE listId = :listId AND listItemDeadline > :expiry")
    List<TodoListItem> getTodoListItemFilterEx(String listId, String expiry);
    //!Filters

    @Query("SELECT * FROM todolistitem WHERE listId = :listId ORDER BY CASE :orderType " +
            "WHEN 'listItemCreateDate' THEN listItemCreateDate " +
            "WHEN 'listItemDeadline' THEN listItemDeadline " +
            "WHEN 'listItemName' THEN listItemName " +
            "WHEN 'listItemName' THEN listItemStatusCode " +
            "END ASC")
    List<TodoListItem> getTodoListItemOrder(String listId, String orderType);

}
