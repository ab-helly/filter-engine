package me.abhelly.filterengine.model;

import java.util.Date;

/**
 * Item data model.
 *
 * Created by abhelly on 02.08.15.
 */
public class Item {

    private final String content;

    private final Date dueDate;

    private final int priority;

    public Item(String content, Date dueDate, int priority) {
        this.content = content;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getContent() {
        return content;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

}
