package com.revolsys.record.query;

import java.io.IOException;

import org.jeometry.common.data.type.DataType;
import org.jeometry.common.exception.Exceptions;

import com.revolsys.record.schema.RecordStore;

public class LeftUnaryCondition extends AbstractUnaryQueryValue implements Condition {

  private final String operator;

  public LeftUnaryCondition(final String operator, final QueryValue value) {
    super(value);
    this.operator = operator;
  }

  @Override
  public void appendDefaultSql(final Query query, final RecordStore recordStore,
    final Appendable buffer) {
    try {
      buffer.append(this.operator);
      buffer.append(" ");
      super.appendDefaultSql(query, recordStore, buffer);
    } catch (final IOException e) {
      throw Exceptions.wrap(e);
    }
  }

  @Override
  public LeftUnaryCondition clone() {
    return (LeftUnaryCondition)super.clone();
  }

  @Override
  public LeftUnaryCondition clone(final TableReference oldTable, final TableReference newTable) {
    return (LeftUnaryCondition)super.clone(oldTable, newTable);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof LeftUnaryCondition) {
      final LeftUnaryCondition condition = (LeftUnaryCondition)obj;
      if (DataType.equal(condition.getOperator(), getOperator())) {
        return super.equals(condition);
      }
    }
    return false;
  }

  public String getOperator() {
    return this.operator;
  }

  @Override
  public String toString() {
    return this.operator + " " + super.toString();
  }
}
