package me.abhelly.filterengine.model;

import java.util.Date;

/**
 * Item data model.
 *
 * Created by abhelly on 02.08.15.
 */
public class Item {

    private final int id;

    private final String content;

    private final Date dueDate;

    private final int priority;

    public Item(int id, String content, Date dueDate, int priority) {
        this.id = id;
        this.content = content;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Item) {
            Item another = ((Item) o);
            return this.content.equals(another.content)
                    && this.dueDate.equals(another.dueDate)
                    && this.priority == another.priority;
        } else {
            return false;
        }
    }
}
