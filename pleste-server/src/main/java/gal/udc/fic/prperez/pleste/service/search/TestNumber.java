package gal.udc.fic.prperez.pleste.service.search;

import gal.udc.fic.prperez.pleste.service.JSONDatetime;
import gal.udc.fic.prperez.pleste.service.dao.component.*;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import gal.udc.fic.prperez.pleste.service.exceptions.ParseSearchException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class TestNumber extends Node {
	private enum Operator {
		EQUAL("="),
		NOT_EQUAL("!="),
		GREAT(">"),
		LESS("<"),
		GREAT_EQUAL(">="),
		LESS_EQUAL("<=");

		private final String operator;

		Operator(String s) {
			this.operator = s;
		}

		public String getOperator() {
			return operator;
		}

		public static Operator getOperator(String s) throws IllegalArgumentException {
			for (Operator op : Operator.values()) {
				if (s.equals(op.getOperator())) {
					return op;
				}
			}
			throw new IllegalArgumentException();
		}

		public boolean match(BigDecimal a, BigDecimal b) {
			return switch (this) {
				case EQUAL -> a.equals(b);
				case NOT_EQUAL -> ! a.equals(b);
				case GREAT -> a.compareTo(b) > 0;
				case LESS -> a.compareTo(b) < 0;
				case GREAT_EQUAL -> a.compareTo(b) >= 0;
				case LESS_EQUAL -> a.compareTo(b) <= 0;
			};
		}

		public boolean match(JSONDatetime a, JSONDatetime b) {
			OffsetDateTime a2, b2;
			a2 = a.getDatetime();
			b2 = b.getDatetime();
			
			return switch (this) {
				case EQUAL -> a2.isEqual(b2);
				case NOT_EQUAL -> ! a2.isEqual(b2);
				case GREAT -> a2.isAfter(b2);
				case LESS -> a2.isBefore(b2);
				case GREAT_EQUAL -> a2.isAfter(b2) || a2.isEqual(b2);
				case LESS_EQUAL -> a2.isBefore(b2) || a2.isEqual(b2);
			};
		}
	}

	private final Properties property;
	private final Operator operator;
	private final BigDecimal valueNumber;
	private final JSONDatetime valueDate;

	public TestNumber(String property, String operator, String value) {
		try {
			this.property = Properties.getProperty(property);
		} catch (IllegalArgumentException e) {
			throw new ParseSearchException("Unknown property: " + property);
		}
		try {
			this.operator = Operator.getOperator(operator);
		} catch (IllegalArgumentException e) {
			throw new ParseSearchException("Unknown operator: " + operator);
		}

		BigDecimal valueNumber = null;
		JSONDatetime valueDate = null;
		try {
			valueNumber = new BigDecimal(value);
		} catch (NumberFormatException e) {
			try {
				valueDate = JSONDatetime.parse(value);
			} catch (DateTimeParseException e2) {
				throw new ParseSearchException("Invalid value: " + value);
			}
		} finally {
			this.valueNumber = valueNumber;
			this.valueDate = valueDate;
		}
	}

	@Override
	public String toString() {
		return property + " " + operator.getOperator() + " ";
	}

	@Override
	public boolean matches(Object n) {
		if(n instanceof Template) {
			throw new ParseSearchException("Tried to use " + this.property + " with a template");
		} else if (n instanceof Component c) {
			if (this.property == Properties.COMPONENT_FIELD_VALUE) {
				return c.getFields().stream().anyMatch(f ->
						switch (f.getTemplateField().getType()) {
					case LINK -> switch (this.operator) {
						case EQUAL -> ((LinkField) f).getContent().getId().equals((long) valueNumber.intValue());
						case NOT_EQUAL -> ! ((LinkField) f).getContent().getId().equals((long) valueNumber.intValue());
						default -> false;
					};
					case NUMBER -> this.operator.match(((NumberField)f).getContent(), valueNumber);
					case DATETIME -> this.operator.match(((DatetimeField)f).getContent(), valueDate);
					default -> false;
				});
			} else {
				throw new ParseSearchException("Tried to use " + this.property + " with a component");
			}
		} else if (n instanceof User) {
			throw new ParseSearchException("Tried to use " + this.property + " with a user");
		} else {
			return false;
		}
	}
}
