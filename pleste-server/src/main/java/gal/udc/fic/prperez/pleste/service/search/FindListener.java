package gal.udc.fic.prperez.pleste.service.search;

import org.apache.commons.lang3.StringUtils;

import gal.udc.fic.prperez.pleste.service.FindBaseListener;
import gal.udc.fic.prperez.pleste.service.FindParser;
import gal.udc.fic.prperez.pleste.service.exceptions.ParseSearchException;
import org.antlr.v4.runtime.tree.ErrorNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FindListener extends FindBaseListener {
	private final Node root;
	private final Stack<Node> stack = new Stack<Node>();

	public FindListener(Node root) {
		this.root = root;
		stack.push(root);
	}

	private void appendNode(Node node) {
		Node top = stack.peek();

		if(top.getChild() == null) {
			top.setChild(node);
		} else {
			((BinaryNode) top).setSecondChild(node);
		}
	}

	@Override
	public void enterTestString(FindParser.TestStringContext ctx) {
		try {
			String property = ctx.getChild(0).getText().trim();
			boolean trueMatch = ctx.getChild(1).getText().trim().equals("~");
			String value = StringUtils.strip(ctx.getChild(2).getText().trim(), "\"");

			TestString node = new TestString(property, value, trueMatch);
			appendNode(node);
		} catch (IndexOutOfBoundsException e) {
			throw new ParseSearchException(e.getMessage());
		}
	}

	@Override
	public void enterTestNum(FindParser.TestNumContext ctx) {
		try {
			String property = ctx.getChild(0).getText().trim();
			String trueMatch = ctx.getChild(1).getText().trim();
			String value = ctx.getChild(2).getText().trim();
			TestNumber node = new TestNumber(property, trueMatch, value);
			appendNode(node);
		} catch (IndexOutOfBoundsException e) {
			throw new ParseSearchException(e.getMessage());
		}
	}

	@Override
	public void enterTestIn(FindParser.TestInContext ctx) {
		try {
			String property = ctx.getChild(0).getText().trim();
			boolean trueMatch = ctx.getChild(1).getText().trim().equals("@");
			List<String> values = new ArrayList<>();
			for(int i = 3; i < ctx.getChildCount(); i += 2) {
				if(ctx.getChild(i).getText().trim().equals("]")) {
					break;
				} else {
					values.add(StringUtils.strip(ctx.getChild(i).getText().trim(), "\""));
				}
			}
			TestIn node = new TestIn(property, values, trueMatch);
			appendNode(node);
		} catch (IndexOutOfBoundsException e) {
			throw new ParseSearchException(e.getMessage());
		}
	}

	@Override
	public void enterExpOr(FindParser.ExpOrContext ctx) {
		Node current = new OrNode();
		appendNode(current);
		stack.push(current);
	}

	@Override
	public void exitExpOr(FindParser.ExpOrContext ctx) {
		stack.pop();
	}

	@Override
	public void enterExpAnd(FindParser.ExpAndContext ctx) {
		Node current = new AndNode();
		appendNode(current);
		stack.push(current);
	}

	@Override
	public void exitExpAnd(FindParser.ExpAndContext ctx) {
		stack.pop();
	}

	@Override
	public void visitErrorNode(ErrorNode node) {
		throw new ParseSearchException("Unknown " + node.toString() + " at character " + node.getSymbol().getStartIndex());
	}
}
