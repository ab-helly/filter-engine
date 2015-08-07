package me.abhelly.filterengine.filter;

import java.util.Date;

/**
 * Filter result meta data.
 *
 * Created by abhelly on 05.08.15.
 */
public class Section {

    private final String title;

    private final Date dueDate;

    private final int priority;

    public Section(String title, Date dueDate, int priority) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }
}
