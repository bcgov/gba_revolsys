package com.revolsys.record.query;

import java.io.IOException;
import java.sql.PreparedStatement;

import org.jeometry.common.exception.Exceptions;
import org.jeometry.common.io.PathName;

import com.revolsys.collection.map.MapEx;
import com.revolsys.record.schema.RecordDefinitionProxy;
import com.revolsys.record.schema.RecordStore;

public class Join implements QueryValue, TableReferenceProxy {

  private final JoinType joinType;

  private PathName tablePath;

  private TableReference table;

  private QueryValue statement;

  private Condition condition = Condition.ALL;

  private String alias;

  public Join(final JoinType joinType) {
    this.joinType = joinType;
  }

  public Join and(final Condition condition) {
    this.condition = this.condition.and(condition);
    return this;
  }

  @Override
  public void appendDefaultSql(final Query query, final RecordStore recordStore,
    final Appendable sql) {
    try {
      sql.append(' ');
      sql.append(this.joinType.toString());
      sql.append(' ');
      if (this.table != null) {
        if (this.alias == null) {
          this.table.appendFromWithAlias(sql);
        } else {
          this.table.appendFromWithAlias(sql, this.alias);
        }
      }
      if (this.statement != null) {
        this.statement.appendDefaultSelect(query, recordStore, sql);
        if (this.alias != null) {
          sql.append(" ");
          sql.append(this.alias);
        }
      }
      if (!this.condition.isEmpty()) {
        sql.append(" ON ");
        this.condition.appendSql(query, recordStore, sql);
      }
    } catch (final IOException e) {
      throw Exceptions.wrap(e);
    }
  }

  @Override
  public int appendParameters(final int index, final PreparedStatement statement) {
    return this.condition.appendParameters(index, statement);
  }

  private void appendSql(final StringBuilder sql) {
    sql.append(' ');
    sql.append(this.joinType);
    sql.append(' ');
    if (this.table != null) {
      this.table.appendFromWithAlias(sql);
    }
    if (this.statement != null) {
      this.statement.appendSql(null, null, sql);
    }
    if (!this.condition.isEmpty()) {
      sql.append(" ON ");
      sql.append(this.condition);
    }
  }

  @Override
  public Join clone() {
    try {
      final Join join = (Join)super.clone();
      join.condition = this.condition.clone();
      return join;
    } catch (final CloneNotSupportedException e) {
      throw Exceptions.wrap(e);
    }
  }

  @Override
  public Join clone(final TableReference oldTable, final TableReference newTable) {
    final Join join = clone();
    join.condition = this.condition.clone(oldTable, newTable);
    return join;
  }

  public Join condition(final Condition condition) {
    if (condition == null) {
      this.condition = Condition.ALL;
    } else {
      this.condition = condition;
    }
    return this;
  }

  @Override
  public ColumnReference getColumn(final CharSequence name) {
    if (this.table.hasColumn(name)) {
      return new Column(this, name);
    }
    throw new IllegalArgumentException(
      "Column not found: " + this.table.getTableReference().getTablePath() + "." + name);
  }

  public Condition getCondition() {
    return this.condition;
  }

  public TableReference getTable() {
    return this.table;
  }

  @Override
  public String getTableAlias() {
    return this.alias;
  }

  public PathName getTableName() {
    return this.tablePath;
  }

  @Override
  public TableReference getTableReference() {
    return this.table;
  }

  @Override
  public <V> V getValue(final MapEx record) {
    return null;
  }

  public Join on(final QueryValue fromFieldName, final Query query, final String toFieldName) {
    final TableReference toTable = query.getTable();
    return on(fromFieldName, toTable, toFieldName);
  }

  public Join on(final QueryValue from, final TableReference toTable, final String toFieldName) {
    final Condition condition = Q.equal(from, toTable.getColumn(toFieldName));
    return and(condition);
  }

  public Join on(final String fromFieldName, final Object value) {
    final Condition condition = this.table.equal(fromFieldName, value);
    return and(condition);
  }

  public Join on(final String fieldName, final Query query) {
    final TableReference toTable = query.getTable();
    return on(fieldName, toTable);
  }

  public Join on(final String fromFieldName, final Query query, final String toFieldName) {
    final TableReference toTable = query.getTable();
    return on(fromFieldName, toTable, toFieldName);
  }

  public Join on(final String fieldName, final TableReference toTable) {
    return on(fieldName, toTable, fieldName);
  }

  public Join on(final String fromFieldName, final TableReference toTable,
    final String toFieldName) {
    final Condition condition = this.table.equal(fromFieldName, toTable, toFieldName);
    return and(condition);
  }

  public Join or(final Condition condition) {
    this.condition = this.condition.or(condition);
    return this;
  }

  public Join recordDefinition(final RecordDefinitionProxy recordDefinition) {
    this.table = recordDefinition.getRecordDefinition();
    this.tablePath = this.table.getTablePath();
    return this;
  }

  public Join select(final Query query, final String... columnNames) {
    query.select(this, columnNames);
    return this;
  }

  public Join setAlias(final String alias) {
    this.alias = alias;
    return this;
  }

  public Join statement(final QueryValue statement) {
    this.statement = statement;
    return this;
  }

  public Join table(final TableReference table) {
    this.table = table;
    return this;
  }

  public Join tablePath(final PathName tableName) {
    this.tablePath = tableName;
    return this;
  }

  public String toSql() {
    final StringBuilder string = new StringBuilder();
    appendSql(string);
    return string.toString();
  }

  @Override
  public String toString() {
    return toSql();
  }
}
