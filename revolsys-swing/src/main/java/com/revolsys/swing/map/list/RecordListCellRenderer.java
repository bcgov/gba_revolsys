package com.revolsys.swing.map.list;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.jeometry.common.awt.WebColors;
import org.jeometry.common.data.type.DataType;
import org.jeometry.common.data.type.DataTypes;

import com.revolsys.record.Record;
import com.revolsys.swing.field.TextField;

public class RecordListCellRenderer extends DefaultListCellRenderer {
  private static final long serialVersionUID = 1L;

  private final String fieldName;

  private Color stripeColor = null;

  private TextField highlightValue = null;

  // this.list.setHighlighters(HighlighterFactory.createSimpleStriping(Color.LIGHT_GRAY));
  // this.list.addHighlighter(new ColorHighlighter(this, WebColors.Blue,
  // WebColors.White));

  public RecordListCellRenderer(final String fieldName) {
    this(fieldName, true, null);
  }

  public RecordListCellRenderer(final String fieldName, final boolean isStriped,
    final TextField highlightValue) {
    this.fieldName = fieldName;
    if (isStriped) {
      this.stripeColor = Color.LIGHT_GRAY;
    }
    this.highlightValue = highlightValue;
  }

  public RecordListCellRenderer(final String fieldName, final TextField highlightValue) {
    this(fieldName, true, highlightValue);
  }

  @Override
  public Component getListCellRendererComponent(final JList<?> list, final Object cellValue,
    final int index, final boolean isSelected, final boolean cellHasFocus) {

    super.getListCellRendererComponent(list, cellValue, index, isSelected, cellHasFocus);
    if (cellValue instanceof Record) {
      final Record object = (Record)cellValue;
      final Object value = object.getValue(this.fieldName);
      final String text = DataTypes.toString(value);
      setText(text);

      if (!isSelected) {
        // Exact match highlight — replaces ColorHighlighter(HighlightPredicate,
        // Blue, White)
        if (this.highlightValue != null && DataType.equal(this.highlightValue.getText(), text)) {
          setBackground(WebColors.Blue);
          setForeground(WebColors.White);
        } else if (this.stripeColor != null) {
          // Alternating stripe — replaces
          // HighlighterFactory.createSimpleStriping(LIGHT_GRAY)
          setBackground(index % 2 == 0 ? Color.WHITE : this.stripeColor);
          setForeground(Color.BLACK);
        }
      }
    }
    return this;
  }
}
