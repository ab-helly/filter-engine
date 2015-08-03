package me.abhelly.filterengine.dummy;

import java.util.ArrayList;
import java.util.Date;

import me.abhelly.filterengine.model.Item;

/**
 * Generates sample data.
 *
 * Created by abhelly on 02.08.15.
 */
public class DataGenerator {

    private static final String CONTENT_TEMPLATE = "Content #";

    // min due date, current is 09/01/2015 (PST)
    private static final long DATE_START_MS = 1441090800000L;

    // max due date, current is 01/01/2016 (PST)
    private static final long DATE_END_MS = 1451635200000L;

    private static final int PRIORITY_START = 1;

    private static final int PRIORITY_END = 4;

    private static DataGenerator mInstance;

    private DataGenerator() {
    }

    public static DataGenerator getInstance() {
        if (mInstance == null) {
            mInstance = new DataGenerator();
        }
        return mInstance;
    }

    public ArrayList<Item> generateDataList(final int count) {
        ArrayList<Item> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add(new Item(getRandomContent(i), getRandomDate(), getRandomPriority()));
        }
        return items;
    }

    private Date getRandomDate() {
        long ms = randomNumber(DATE_START_MS, DATE_END_MS);
        return new Date(ms);
    }

    private String getRandomContent(int index) {
        return CONTENT_TEMPLATE + index;
    }

    private int getRandomPriority() {
        return (int) randomNumber(PRIORITY_START, PRIORITY_END);
    }

    private long randomNumber(long start, long end) {
        return start + Math.round(Math.random() * (end - start));
    }

}
