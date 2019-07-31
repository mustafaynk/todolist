package com.ynk.todolist.Model;

public class Filter {

    private boolean completed;
    private boolean continued;
    private boolean expired;

    public Filter() {

    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isContinued() {
        return continued;
    }

    public void setContinued(boolean continued) {
        this.continued = continued;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
