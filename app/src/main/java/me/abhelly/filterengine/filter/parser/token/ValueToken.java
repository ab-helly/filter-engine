package me.abhelly.filterengine.filter.parser.token;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import me.abhelly.filterengine.R;
import me.abhelly.filterengine.model.Item;

/**
 * Value token, filters input data based on {@link #mSequence} and {@link #mType}.
 *
 * Created by abhelly on 05.08.15.
 */
public class ValueToken extends Token {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public ValueToken(@TokenType int type, String sequence) {
        super(type, sequence);
    }

    @Override
    public String getFormattedSequence(Context context) {
        switch (mType) {
            case TOKEN_VALUE_DATE:
                try {
                    DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    Date date = format.parse(mSequence);
                    String dateFormat = context.getString(R.string.formatted_date);
                    SimpleDateFormat userFormat = new SimpleDateFormat(dateFormat,
                            Locale.getDefault());
                    return userFormat.format(date);
                } catch (ParseException e) {
                    return "";
                }
            case TOKEN_VALUE_PRIORITY:
                int priority = Integer.parseInt(mSequence.replace("p", ""));
                return context.getString(R.string.formatted_priority, priority);
            case TOKEN_VALUE_TEXT:
                return context.getString(R.string.formatted_text, mSequence);
            default:
                throw new IllegalArgumentException("Unsupported value token type");
        }
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
            if (!isSameDay(it.next().getDueDate(), dueDate)) {
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

    private boolean isSameDay(Date one, Date another) {
        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTime(one);
        GregorianCalendar gc2 = new GregorianCalendar();
        gc2.setTime(another);
        return gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR) &&
                gc1.get(Calendar.DAY_OF_YEAR) == gc2.get(Calendar.DAY_OF_YEAR);
    }
}
