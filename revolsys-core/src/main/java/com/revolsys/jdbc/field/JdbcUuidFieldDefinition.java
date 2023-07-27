package com.revolsys.jdbc.field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jeometry.common.data.type.DataTypes;

import com.revolsys.record.query.ColumnIndexes;
import com.revolsys.record.schema.RecordDefinition;

public class JdbcUuidFieldDefinition extends JdbcFieldDefinition {
  public JdbcUuidFieldDefinition(final String dbName, final String name, final int sqlType,
    final boolean required, final String description, final Map<String, Object> properties) {

    super(dbName, name, DataTypes.UUID, sqlType, 32, 0, required, description, properties);
  }

  @Override
  public JdbcUuidFieldDefinition clone() {
    final JdbcUuidFieldDefinition clone = new JdbcUuidFieldDefinition(getDbName(), getName(),
      getSqlType(), isRequired(), getDescription(), getProperties());
    postClone(clone);
    return clone;
  }

  @Override
  public Object getValueFromResultSet(final RecordDefinition recordDefinition,
    final ResultSet resultSet, final ColumnIndexes indexes, final boolean internStrings)
    throws SQLException {
    final Object value = resultSet.getObject(indexes.incrementAndGet());
    if (resultSet.wasNull()) {
      return null;
    } else {
      return value;
    }
  }

  @Override
  public int setPreparedStatementValue(final PreparedStatement statement, final int parameterIndex,
    final Object value) throws SQLException {
    if (value == null) {
      statement.setNull(parameterIndex, getSqlType());
    } else {
      statement.setObject(parameterIndex, value);
    }
    return parameterIndex + 1;
  }
}
