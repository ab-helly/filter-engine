package me.abhelly.filterengine.filter;

import java.util.ArrayList;

import me.abhelly.filterengine.model.Item;

/**
 * Filter result contains list of filtered items and meta data.
 *
 * Created by abhelly on 05.08.15.
 */
public class FilterResult {

    private final Section section;

    private final ArrayList<Item> items;

    public FilterResult(Section section, ArrayList<Item> items) {
        this.section = section;
        this.items = items;
    }

    public Section getSection() {
        return section;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
