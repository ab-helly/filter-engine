package me.abhelly.filterengine;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import java.util.LinkedList;

import me.abhelly.filterengine.filter.parser.Tokenizer;
import me.abhelly.filterengine.filter.parser.token.Token;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for Tokenizer class.
 *
 * Created by abhelly on 07.08.15.
 */
@RunWith(AndroidJUnit4.class)
public class TokenizerUnitTest {

    static final String TEST_SIMPLE_STRING = "2015-09-01";

    static final String TEST_TEXT = "test text";

    static final String TEST_COMPOUND_STRING = "p1 | (2015-09-01 | 2015-09-02) & " + TEST_TEXT;

    @Test
    public void tokenizer_tokenizeSimple() {
        LinkedList<Token> result = Tokenizer.tokenize(TEST_SIMPLE_STRING);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getType(), is(Token.TOKEN_VALUE_DATE));
        assertThat(result.get(0).getSequence(), is(TEST_SIMPLE_STRING));
    }

    @Test
    public void tokenizer_tokenizeCompound() {
        LinkedList<Token> result = Tokenizer.tokenize(TEST_COMPOUND_STRING);
        assertThat(result.size(), is(9));
        // remember reverse order
        assertThat(result.get(0).getType(), is(Token.TOKEN_VALUE_TEXT));
        assertThat(result.get(0).getSequence(), is(TEST_TEXT));
        assertThat(result.get(1).getType(), is(Token.TOKEN_AND));
        assertThat(result.get(2).getType(), is(Token.TOKEN_RIGHT_BRACKET));
        assertThat(result.get(3).getType(), is(Token.TOKEN_VALUE_DATE));
        assertThat(result.get(4).getType(), is(Token.TOKEN_OR));
        assertThat(result.get(5).getType(), is(Token.TOKEN_VALUE_DATE));
        assertThat(result.get(6).getType(), is(Token.TOKEN_LEFT_BRACKET));
        assertThat(result.get(7).getType(), is(Token.TOKEN_OR));
        assertThat(result.get(8).getType(), is(Token.TOKEN_VALUE_PRIORITY));
    }
}