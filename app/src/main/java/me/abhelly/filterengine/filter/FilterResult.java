package me.abhelly.filterengine.filter;

import java.util.List;

import me.abhelly.filterengine.model.Item;

/**
 * Filter result contains list of filtered items and meta data.
 *
 * Created by abhelly on 05.08.15.
 */
public class FilterResult {

    private Section section;

    private List<Item> items;

    public FilterResult(Section section, List<Item> items) {
        this.section = section;
        this.items = items;
    }

    public Section getSection() {
        return section;
    }

    public List<Item> getItems() {
        return items;
    }
}
