package com.revolsys.swing.component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class BasicTransferable implements Transferable {

  private static final DataFlavor[] HTML_FLAVORS = {
    DataFlavor.fragmentHtmlFlavor, DataFlavor.selectionHtmlFlavor, DataFlavor.allHtmlFlavor,
  };

  private final String plainText;

  private final String htmlText;

  public BasicTransferable(final String plainText, final String htmlText) {
    this.plainText = plainText;
    this.htmlText = htmlText;
  }

  @Override
  public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
    if (this.htmlText != null) {
      for (final DataFlavor htmlFlavor : HTML_FLAVORS) {
        if (flavor.equals(htmlFlavor)) {
          return this.htmlText;
        }
      }
    }
    if (this.plainText != null) {
      if (flavor.equals(DataFlavor.stringFlavor)) {
        return this.plainText;
      }
      if (flavor.equals(DataFlavor.plainTextFlavor)) {
        return new java.io.StringReader(this.plainText);
      }
    }
    throw new UnsupportedFlavorException(flavor);
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    if (this.htmlText != null && this.plainText != null) {
      return new DataFlavor[] {
        DataFlavor.fragmentHtmlFlavor, DataFlavor.stringFlavor, DataFlavor.plainTextFlavor
      };
    } else if (this.htmlText != null) {
      return new DataFlavor[] {
        DataFlavor.fragmentHtmlFlavor
      };
    } else if (this.plainText != null) {
      return new DataFlavor[] {
        DataFlavor.stringFlavor, DataFlavor.plainTextFlavor
      };
    }
    return new DataFlavor[0];
  }

  @Override
  public boolean isDataFlavorSupported(final DataFlavor flavor) {
    if (this.htmlText != null) {
      for (final DataFlavor htmlFlavor : HTML_FLAVORS) {
        if (flavor.equals(htmlFlavor)) {
          return true;
        }
      }
    }
    if (this.plainText != null) {
      return flavor.equals(DataFlavor.stringFlavor) || flavor.equals(DataFlavor.plainTextFlavor);
    }
    return false;
  }
}
