/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.revolsys.gis.esri.gdb.file.capi.swig;

public class FieldDef {
  protected static long getCPtr(final FieldDef obj) {
    return obj == null ? 0 : obj.swigCPtr;
  }

  protected boolean swigCMemOwn;

  private long swigCPtr;

  public FieldDef() {
    this(EsriFileGdbJNI.new_FieldDef(), true);
  }

  protected FieldDef(final long cPtr, final boolean cMemoryOwn) {
    this.swigCMemOwn = cMemoryOwn;
    this.swigCPtr = cPtr;
  }

  public synchronized void delete() {
    if (this.swigCPtr != 0) {
      if (this.swigCMemOwn) {
        this.swigCMemOwn = false;
        EsriFileGdbJNI.delete_FieldDef(this.swigCPtr);
      }
      this.swigCPtr = 0;
    }
  }

  @Override
  protected void finalize() {
    delete();
  }

  public String getAlias() {
    return EsriFileGdbJNI.FieldDef_getAlias(this.swigCPtr, this);
  }

  public int getLength() {
    return EsriFileGdbJNI.FieldDef_getLength(this.swigCPtr, this);
  }

  public String getName() {
    return EsriFileGdbJNI.FieldDef_getName(this.swigCPtr, this);
  }

  public FieldType getType() {
    return FieldType.swigToEnum(EsriFileGdbJNI.FieldDef_getType(this.swigCPtr,
      this));
  }

  public boolean isNullable() {
    return EsriFileGdbJNI.FieldDef_isNullable(this.swigCPtr, this);
  }

  public int SetAlias(final String alias) {
    return EsriFileGdbJNI.FieldDef_SetAlias(this.swigCPtr, this, alias);
  }

  public int SetIsNullable(final boolean isNullable) {
    return EsriFileGdbJNI.FieldDef_SetIsNullable(this.swigCPtr, this,
      isNullable);
  }

  public int SetLength(final int length) {
    return EsriFileGdbJNI.FieldDef_SetLength(this.swigCPtr, this, length);
  }

  public int SetName(final String name) {
    return EsriFileGdbJNI.FieldDef_SetName(this.swigCPtr, this, name);
  }

  public int SetType(final FieldType type) {
    return EsriFileGdbJNI.FieldDef_SetType(this.swigCPtr, this,
      type.swigValue());
  }

}