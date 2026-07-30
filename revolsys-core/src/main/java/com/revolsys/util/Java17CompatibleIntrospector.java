package com.revolsys.util;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Drop-in replacement for {@link Introspector#getBeanInfo(Class)} that reproduces
 * the behavior Java 17 had before JDK-8071693 was fixed: default methods declared
 * on an interface (and inherited by a class without being overridden) are NOT
 * treated as bean property getters/setters.
 *
 * On current JDKs (17.0.14+, 21.0.6+ backport lines, and mainline from JDK 24
 * onward), Introspector.getBeanInfo(clazz).getPropertyDescriptors() will include
 * properties backed purely by unoverridden default interface methods. This class
 * strips those back out so code written against pre-fix Java 17 semantics keeps
 * behaving the same way.
 *
 * Usage is a drop-in swap:
 *
 *   final BeanInfo beanInfo = Java17CompatibleIntrospector.getBeanInfo(clazz);
 *   for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
 *       ...
 *   }
 *
 * Note: this only filters PropertyDescriptors (and IndexedPropertyDescriptors).
 * getMethodDescriptors() / getEventSetDescriptors() are passed through unfiltered
 * from the delegate; the same MethodInfo change can affect those too, but most
 * callers only care about properties. Ask if you need those filtered as well.
 */
public final class Java17CompatibleIntrospector {

  private static void copyFeatureState(final PropertyDescriptor from, final PropertyDescriptor to) {
    to.setDisplayName(from.getDisplayName());
    to.setShortDescription(from.getShortDescription());
    to.setExpert(from.isExpert());
    to.setHidden(from.isHidden());
    to.setPreferred(from.isPreferred());
    final Enumeration<String> keys = from.attributeNames();
    while (keys.hasMoreElements()) {
      final String key = keys.nextElement();
      to.setValue(key, from.getValue(key));
    }
  }

  private static PropertyDescriptor[] filter(final PropertyDescriptor[] descriptors) {
    final List<PropertyDescriptor> result = new ArrayList<>(descriptors.length);
    for (final PropertyDescriptor pd : descriptors) {
      final PropertyDescriptor cleaned = stripDefaultAccessors(pd);
      if (cleaned != null) {
        result.add(cleaned);
      }
    }
    return result.toArray(new PropertyDescriptor[0]);
  }

  public static BeanInfo getBeanInfo(final Class<?> clazz) throws IntrospectionException {
    final BeanInfo delegate = Introspector.getBeanInfo(clazz);
    final PropertyDescriptor[] filtered = filter(delegate.getPropertyDescriptors());

    return new SimpleBeanInfo() {
      @Override
      public BeanInfo[] getAdditionalBeanInfo() {
        return delegate.getAdditionalBeanInfo();
      }

      @Override
      public BeanDescriptor getBeanDescriptor() {
        return delegate.getBeanDescriptor();
      }

      @Override
      public int getDefaultEventIndex() {
        return delegate.getDefaultEventIndex();
      }

      @Override
      public int getDefaultPropertyIndex() {
        return delegate.getDefaultPropertyIndex();
      }

      @Override
      public EventSetDescriptor[] getEventSetDescriptors() {
        return delegate.getEventSetDescriptors();
      }

      @Override
      public MethodDescriptor[] getMethodDescriptors() {
        return delegate.getMethodDescriptors();
      }

      @Override
      public PropertyDescriptor[] getPropertyDescriptors() {
        return filtered;
      }
    };
  }

  private static Method nullIfDefault(final Method method) {
    return method != null && method.isDefault() ? null : method;
  }

  private static PropertyDescriptor stripDefaultAccessors(final IndexedPropertyDescriptor ipd) {
    final Method originalRead = ipd.getReadMethod();
    final Method originalWrite = ipd.getWriteMethod();
    final Method originalIndexedRead = ipd.getIndexedReadMethod();
    final Method originalIndexedWrite = ipd.getIndexedWriteMethod();

    final Method read = nullIfDefault(originalRead);
    final Method write = nullIfDefault(originalWrite);
    final Method indexedRead = nullIfDefault(originalIndexedRead);
    final Method indexedWrite = nullIfDefault(originalIndexedWrite);

    if (read == originalRead && write == originalWrite && indexedRead == originalIndexedRead
      && indexedWrite == originalIndexedWrite) {
      // Nothing was a default method: return the original untouched rather
      // than re-validating a combination Introspector already accepted.
      return ipd;
    }
    if (read == null && write == null && indexedRead == null && indexedWrite == null) {
      return null;
    }

    try {
      final IndexedPropertyDescriptor copy = new IndexedPropertyDescriptor(ipd.getName(), read,
        write, indexedRead, indexedWrite);
      copyFeatureState(ipd, copy);
      copy.setConstrained(ipd.isConstrained());
      copy.setBound(ipd.isBound());
      return copy;
    } catch (final IntrospectionException e) {
      throw new IllegalStateException(
        "Failed to rebuild IndexedPropertyDescriptor for '" + ipd.getName() + "'", e);
    }
  }

  /**
   * Returns a copy of {@code pd} with any accessor that resolves to an
   * unoverridden interface default method removed. Returns {@code null} if
   * the property existed only because of such accessors (i.e. pre-fix Java 17
   * would never have discovered this property at all).
   */
  private static PropertyDescriptor stripDefaultAccessors(final PropertyDescriptor pd) {
    if (pd instanceof IndexedPropertyDescriptor) {
      return stripDefaultAccessors((IndexedPropertyDescriptor)pd);
    }

    final Method originalRead = pd.getReadMethod();
    final Method originalWrite = pd.getWriteMethod();
    final Method read = nullIfDefault(originalRead);
    final Method write = nullIfDefault(originalWrite);

    if (read == originalRead && write == originalWrite) {
      // Neither accessor was a default method: nothing to remove, so return
      // the original descriptor untouched. This avoids re-running the public
      // constructor's read/write type-consistency check on pairs that
      // Introspector itself assembled more leniently (e.g. via generics or
      // covariant accessors) - rebuilding unchanged pairs is exactly what was
      // blowing up on properties like "menuRecord".
      return pd;
    }
    if (read == null && write == null) {
      // Property existed only because of default-method accessors.
      return null;
    }

    // From here on we are only ever dropping an accessor, never adding one, so
    // the resulting pair can't be any less consistent than what Introspector
    // already had - it's always safe to rebuild.
    try {
      final PropertyDescriptor copy = new PropertyDescriptor(pd.getName(), read, write);
      copyFeatureState(pd, copy);
      copy.setConstrained(pd.isConstrained());
      copy.setBound(pd.isBound());
      return copy;
    } catch (final IntrospectionException e) {
      throw new IllegalStateException(
        "Failed to rebuild PropertyDescriptor for '" + pd.getName() + "'", e);
    }
  }

  private Java17CompatibleIntrospector() {
  }
}
