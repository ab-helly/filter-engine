package me.abhelly.filterengine.filter.parser.token;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import me.abhelly.filterengine.model.Item;

/**
 * Value token, filters input data based on {@link #mSequence} and {@link #mType}.
 *
 * Created by abhelly on 05.08.15.
 */
public class ValueToken extends Token {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public ValueToken(@TokenType int type, String sequence) {
        super(type, sequence);
    }

    public ArrayList<Item> evaluate(ArrayList<Item> items) throws IllegalArgumentException {
        ArrayList<Item> result;
        switch (mType) {
            case TOKEN_VALUE_DATE:
                try {
                    result = filterByDate(items);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Wrong date format");
                }
                break;
            case TOKEN_VALUE_PRIORITY:
                result = filterByPriority(items);
                break;
            case TOKEN_VALUE_TEXT:
                result = filterByText(items);
                break;
            default:
                throw new IllegalArgumentException("Unsupported value token type");
        }
        return result;
    }

    private ArrayList<Item> filterByDate(ArrayList<Item> items) throws ParseException {
        ArrayList<Item> itemsCopy = new ArrayList<>(items);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date dueDate = format.parse(mSequence);
        for (Iterator<Item> it = itemsCopy.iterator(); it.hasNext(); ) {
            if (!it.next().getDueDate().equals(dueDate)) {
                it.remove();
            }
        }
        return itemsCopy;
    }

    private ArrayList<Item> filterByPriority(ArrayList<Item> items) throws NumberFormatException {
        int priority = Integer.parseInt(mSequence.replace("p", ""));
        ArrayList<Item> itemsCopy = new ArrayList<>(items);
        for (Iterator<Item> it = itemsCopy.iterator(); it.hasNext(); ) {
            if (it.next().getPriority() != priority) {
                it.remove();
            }
        }
        return itemsCopy;
    }

    private ArrayList<Item> filterByText(ArrayList<Item> items) {
        ArrayList<Item> itemsCopy = new ArrayList<>(items);
        for (Iterator<Item> it = itemsCopy.iterator(); it.hasNext(); ) {
            if (!it.next().getContent().contains(mSequence)) {
                it.remove();
            }
        }
        return itemsCopy;
    }
}
