/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.collect.named;

import com.google.common.collect.ImmutableMap;

/**
 * A lookup for named instances.
 * <p>
 * This interface is used to lookup objects that can be {@linkplain Named identified by a unique name}.
 * 
 * @param <T>  the named type
 */
public interface NamedLookup<T extends Named> {

  /**
   * Looks up an instance by name, returning null if not found.
   * <p>
   * The name contains enough information to be able to recreate the instance.
   * The lookup should return null if the name is not known.
   * The lookup should be case sensitive.
   * An exception should only be thrown if an error occurs during lookup.
   * <p>
   * The default implementation uses {@link #lookupAll()}.
   * 
   * @param name  the name to lookup
   * @return the named object, null if not found
   */
  public default T lookup(String name) {
    return lookupAll().get(name);
  }

  /**
   * Returns the map of known instances by name.
   * <p>
   * This method returns all known instances.
   * It is permitted for an implementation to return an empty map, however this will
   * reduce the usefulness of the matching method on {@link ExtendedEnum}.
   * The map may include instances keyed under an alternate name.
   * 
   * @return the map of enum instance by name
   */
  public abstract ImmutableMap<String, T> lookupAll();

}