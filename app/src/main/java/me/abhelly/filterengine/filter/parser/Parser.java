package me.abhelly.filterengine.filter.parser;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Stack;

import me.abhelly.filterengine.filter.FilterResult;
import me.abhelly.filterengine.filter.Section;
import me.abhelly.filterengine.filter.parser.token.OperatorToken;
import me.abhelly.filterengine.filter.parser.token.Token;
import me.abhelly.filterengine.filter.parser.token.ValueToken;
import me.abhelly.filterengine.model.Item;

/**
 * Parses filter query and evaluates given data.
 *
 * Created by abhelly on 05.08.15.
 */
public class Parser {

    /**
     * Evaluates given data according to given token list.
     *
     * @param tokens  list of tokens in reverse order
     * @param items   given data to filter
     * @param context context
     * @return filterResult if params are valid, null if params are invalid
     */
    public FilterResult evaluate(LinkedList<Token> tokens, ArrayList<Item> items, Context context) {
        try {
            Section section = parseSectionData(tokens, context);
            ArrayList<Item> filtered = calculate(items, parse(tokens));
            return new FilterResult(section, filtered);
        } catch (EmptyStackException e) {
            Log.e(this.getClass().getSimpleName(), "Invalid filter parameters");
            return null;
        }
    }

    /**
     * Implements Shunting-yard algorithm to order tokens in Reverse Polish notation.
     *
     * @param tokens reversed token list
     * @return list of token in RPN
     */
    private LinkedList<Token> parse(LinkedList<Token> tokens) {
        Stack<OperatorToken> operators = new Stack<>();
        LinkedList<Token> output = new LinkedList<>();

        while (!tokens.isEmpty()) {
            Token token = tokens.pollFirst();

            if (token instanceof ValueToken) {
                output.add(token);
            } else if (token instanceof OperatorToken) {
                OperatorToken operator = (OperatorToken) token;
                OperatorToken topOperator = operators.size() > 0 ? operators.peek() : null;

                if (token.getType() == Token.TOKEN_LEFT_BRACKET) {
                    while (topOperator != null
                            && topOperator.getType() != Token.TOKEN_RIGHT_BRACKET) {
                        output.add(topOperator);
                        operators.pop();
                        topOperator = operators.size() > 0 ? operators.peek() : null;
                    }
                    operators.pop();
                } else if (token.getType() == Token.TOKEN_RIGHT_BRACKET) {
                    operators.push(operator);
                } else {
                    while (topOperator != null
                            && topOperator.precedence() > operator.precedence()) {
                        output.add(topOperator);
                        operators.pop();
                        topOperator = operators.size() > 0 ? operators.peek() : null;
                    }
                    operators.push(operator);
                }
            }
        }
        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        return output;
    }

    /**
     * Evaluates the expression.
     *
     * @param items data to be filtered
     * @param tokens token list in RPN
     * @return result of filter operation
     */
    private ArrayList<Item> calculate(ArrayList<Item> items, LinkedList<Token> tokens) {
        Stack<ArrayList<Item>> result = new Stack<>();

        for (Token item : tokens) {
            if (item instanceof ValueToken) {
                ArrayList<Item> filteredValue = ((ValueToken) item).evaluate(items);
                result.push(filteredValue);
            } else {
                ArrayList<Item> right = new ArrayList<>(result.pop());
                ArrayList<Item> left = new ArrayList<>(result.pop());

                if (item.getType() == Token.TOKEN_AND) {
                    left.retainAll(right);
                    result.push(left);
                } else if (item.getType() == Token.TOKEN_OR) {
                    right.removeAll(left);
                    left.addAll(right);
                    result.push(left);
                } else {
                    throw new IllegalArgumentException("Unknown operation token type");
                }
            }
        }

        return result.pop();
    }

    /**
     * Calculates Section data
     * Rules:
     * section.title:
     * - “2015-04-14” => formatted date (e.g. “Apr 14”)
     * - “p1”, “p2”, “p3”, or “p4” => “Priority [1-4]”
     * - “foo bar” = > “Search for: ‘foo bar’”
     * - “(p1 | p2) & foo bar” => “(Priority 1 | Priority 2) & Search for: ‘foo bar’”
     *
     * section.dueDate / section.priority
     * - should be set with the parsed query date/priority (simple query)
     * - for the AND operator, if the same property is different on both operands, it should be set
     * to null
     * - for the OR operator, these properties should just be set to null
     *
     * @param tokens reversed token list
     * @return given query Section
     */
    private Section parseSectionData(LinkedList<Token> tokens, Context context) {
        LinkedList<Token> tokensCopy = new LinkedList<>(tokens);
        Collections.reverse(tokensCopy);
        DateFormat format = new SimpleDateFormat(ValueToken.DATE_FORMAT, Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        Date date = null;
        int priority = -1;
        boolean dateSet = false;
        boolean prioritySet = false;
        for (int i = 0; i < tokensCopy.size(); i++) {
            Token token = tokensCopy.get(i);
            sb.append(token.getFormattedSequence(context));
            switch (token.getType()) {
                case Token.TOKEN_VALUE_DATE:
                    if (dateSet) {
                        break;
                    }
                    try {
                        date = format.parse(token.getSequence());
                    } catch (ParseException e) {
                        Log.e(this.getClass().getSimpleName(), "Date parse exception");
                        e.printStackTrace();
                    }
                    break;
                case Token.TOKEN_VALUE_PRIORITY:
                    if (prioritySet) {
                        break;
                    }
                    priority = Integer.parseInt(token.getSequence().replace("p", ""));
                    break;
                case Token.TOKEN_OR: {
                    Token next = (i < tokensCopy.size() - 1) ? tokensCopy.get(i + 1) : null;
                    Token prev = (i > 1) ? tokensCopy.get(i - 1) : null;
                    if (!dateSet) {
                        dateSet = (next != null && next.getType() == Token.TOKEN_VALUE_DATE)
                                || (prev != null && prev.getType() == Token.TOKEN_VALUE_DATE);
                        if (dateSet) {
                            date = null;
                        }
                    }
                    if (!prioritySet) {
                        prioritySet = (next != null && next.getType() == Token.TOKEN_VALUE_PRIORITY)
                                || (prev != null && prev.getType() == Token.TOKEN_VALUE_PRIORITY);
                        if (prioritySet) {
                            priority = -1;
                        }
                    }
                    break;
                }
                case Token.TOKEN_AND: {
                    // if date or priority before/after => compare to already set one
                    Token next = (i < tokensCopy.size() - 1) ? tokensCopy.get(i + 1) : null;
                    Token prev = (i > 1) ? tokensCopy.get(i - 1) : null;
                    if (!dateSet) {
                        if (next != null && next.getType() == Token.TOKEN_VALUE_DATE) {
                            try {
                                date = format.parse(next.getSequence());
                            } catch (ParseException e) {
                                Log.e(this.getClass().getSimpleName(), "Date parse exception");
                                e.printStackTrace();
                            }
                        } else if (prev != null && prev.getType() == Token.TOKEN_VALUE_DATE) {
                            try {
                                date = format.parse(prev.getSequence());
                            } catch (ParseException e) {
                                Log.e(this.getClass().getSimpleName(), "Date parse exception");
                                e.printStackTrace();
                            }
                        }
                    }
                    if (!prioritySet) {
                        if (next != null && next.getType() == Token.TOKEN_VALUE_PRIORITY) {
                            priority = Integer.parseInt(next.getSequence().replace("p", ""));
                        } else if (prev != null && prev.getType() == Token.TOKEN_VALUE_PRIORITY) {
                            priority = Integer.parseInt(prev.getSequence().replace("p", ""));
                        }
                    }
                    break;
                }
            }
        }
        // TODO: don't add double spaces for operators
        String title = sb.toString().replaceAll("  ", " ").trim();
        return new Section(title, date, priority);
    }

}
