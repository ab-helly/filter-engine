package me.abhelly.filterengine.filter.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

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

    public Parser() {
    }

    public ArrayList<Item> evaluate(LinkedList<Token> tokens, ArrayList<Item> items) {
        LinkedList<Token> orderedTokens = parse(tokens);
        return calculate(items, orderedTokens);
    }

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
}
