package me.abhelly.filterengine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import me.abhelly.filterengine.filter.parser.token.Token;
import me.abhelly.filterengine.filter.parser.token.ValueToken;
import me.abhelly.filterengine.model.Item;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for ValueToken class.
 *
 * Created by abhelly on 07.08.15.
 */
@RunWith(AndroidJUnit4.class)
public class ValueTokenUnitTest {

    static final ArrayList<Item> ITEMS = new ArrayList<>();

    static final String TEST_DATE_STRING = "2015-09-01";

    static final String TEST_PRIORITY_STRING = "p2";

    static final String TEST_STRING = "TEST STRING";

    static final int TEST_PRIORITY = 2;

    static Date sTestDate;

    @Before
    public void fillData() throws ParseException {
        if (sTestDate == null && ITEMS.size() == 0) {
            DateFormat format = new SimpleDateFormat(ValueToken.DATE_FORMAT, Locale.getDefault());
            sTestDate = format.parse(TEST_DATE_STRING);

            GregorianCalendar gc = new GregorianCalendar(2015, 8, 1);
            gc.setTime(sTestDate);
            gc.add(Calendar.MINUTE, 60);
            Date sameDayDate = gc.getTime();

            gc = new GregorianCalendar();
            gc.setTime(sTestDate);
            gc.add(Calendar.YEAR, 1);
            Date anotherDate = gc.getTime();

            ITEMS.add(new Item(0, TEST_STRING, anotherDate, 1));
            ITEMS.add(new Item(1, TEST_STRING + " x", anotherDate, TEST_PRIORITY));
            ITEMS.add(new Item(2, "x " + TEST_STRING, anotherDate, 3));
            ITEMS.add(new Item(3, "x " + TEST_STRING + " x", sTestDate, 4));
            ITEMS.add(new Item(4, "another", sameDayDate, 1));
            ITEMS.add(new Item(5, "another test", anotherDate, TEST_PRIORITY));
            ITEMS.add(new Item(6, "another string", anotherDate, 3));
            ITEMS.add(new Item(7, "testing string", anotherDate, 4));
            ITEMS.add(new Item(8, "string", anotherDate, 1));
            ITEMS.add(new Item(9, "test", anotherDate, TEST_PRIORITY));
        }
    }

    @Test
    public void valueToken_evaluateDate() {
        ValueToken token = new ValueToken(Token.TOKEN_VALUE_DATE, TEST_DATE_STRING);
        ArrayList<Item> result = token.evaluate(ITEMS);

        assertThat(result.size(), is(2));
    }

    @Test
    public void valueToken_evaluatePriority() {
        ValueToken token = new ValueToken(Token.TOKEN_VALUE_PRIORITY, TEST_PRIORITY_STRING);
        ArrayList<Item> result = token.evaluate(ITEMS);

        assertThat(result.size(), is(3));
    }

    @Test
    public void valueToken_evaluateText() {
        ValueToken token = new ValueToken(Token.TOKEN_VALUE_TEXT, TEST_STRING);
        ArrayList<Item> result = token.evaluate(ITEMS);

        assertThat(result.size(), is(4));
    }

}
