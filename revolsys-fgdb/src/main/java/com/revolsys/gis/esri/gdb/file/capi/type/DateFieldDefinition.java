package com.revolsys.gis.esri.gdb.file.capi.type;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.jeometry.common.data.type.DataTypes;
import org.jeometry.common.date.Dates;
import org.jeometry.common.logging.Logs;

import com.revolsys.esri.filegdb.jni.Row;
import com.revolsys.record.Record;
import com.revolsys.record.io.format.esri.gdb.xml.model.Field;
import com.revolsys.util.Booleans;

public class DateFieldDefinition extends AbstractFileGdbFieldDefinition {
  /** Synchronize access to C++ date methods across all instances. */
  private static final Object LOCK = new Object();

  public static final Instant MAX_DATE = Instant
    .from(ZonedDateTime.of(2038, 1, 19, 0, 0, 0, 0, ZoneOffset.UTC));

  public static final Instant MIN_DATE = Instant
    .from(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));

  public DateFieldDefinition(final int fieldNumber, final Field field) {
    super(fieldNumber, field.getName(), DataTypes.INSTANT,
      Booleans.getBoolean(field.getRequired()) || !field.isIsNullable());
  }

  @Override
  public int getMaxStringLength() {
    return 10;
  }

  @Override
  public Object getValue(final Row row) {
    synchronized (row) {
      if (row.isNull(this.fieldNumber)) {
        return null;
      } else {
        long time;
        synchronized (LOCK) {
          time = row.getDate(this.fieldNumber) * 1000;
        }
        return new Date(time);
      }
    }
  }

  @Override
  public void setValue(final Record record, final Row row, Object value) {
    if (value == null) {
      setNull(row);
    } else {
      if (value instanceof String) {
        try {
          value = Dates.getDate("yyyy-MM-dd", (String)value);
        } catch (final Exception e) {
          throw new IllegalArgumentException("Data must be in the format YYYY-MM-DD " + value);
        }
      }

      Instant date = Dates.getInstant(value);

      if (date.isBefore(MIN_DATE)) {
        Logs.error(this, getName() + "=" + date + " is before " + MIN_DATE
          + " which is not supported by ESRI File Geodatabases\n" + record);
        if (isRequired()) {
          date = MIN_DATE;
        } else {
          row.setNull(this.fieldNumber);
          return;
        }
      } else if (date.isAfter(MAX_DATE)) {
        Logs.error(this, getName() + "=" + date + " is after " + MAX_DATE
          + " which is not supported by ESRI File Geodatabases\n" + record);
        if (isRequired()) {
          date = MAX_DATE;
        } else {
          row.setNull(this.fieldNumber);
          return;
        }
      }

      final long time = date.getEpochSecond();
      synchronized (LOCK) {
        synchronized (row) {
          row.setDate(this.fieldNumber, time);
        }
      }
    }
  }
}
