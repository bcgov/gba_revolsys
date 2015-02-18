package com.revolsys.swing.map.layer.dataobject.component;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

import com.revolsys.converter.string.StringConverterRegistry;
import com.revolsys.gis.data.model.Attribute;
import com.revolsys.swing.map.layer.dataobject.AbstractDataObjectLayer;

public class AttributeTitleStringConveter extends ObjectToStringConverter
  implements ListCellRenderer {
  private final AbstractDataObjectLayer layer;

  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  public AttributeTitleStringConveter(final AbstractDataObjectLayer layer) {
    this.layer = layer;
  }

  @Override
  public Component getListCellRendererComponent(final JList list,
    final Object value, final int index, final boolean isSelected,
    final boolean cellHasFocus) {
    final String title = getPreferredStringForItem(value);
    return this.renderer.getListCellRendererComponent(list, title, index,
      isSelected, cellHasFocus);
  }

  @Override
  public String getPreferredStringForItem(final Object item) {
    if (item instanceof Attribute) {
      final Attribute attribute = (Attribute)item;
      return layer.getFieldTitle(attribute.getName());
    } else if (item instanceof String) {
      final String attributeName = (String)item;
      return layer.getFieldTitle(attributeName);
    }
    return StringConverterRegistry.toString(item);
  }
}