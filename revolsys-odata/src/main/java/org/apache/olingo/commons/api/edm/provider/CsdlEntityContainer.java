/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.api.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * The type Csdl entity container.
 */
public class CsdlEntityContainer implements CsdlAbstractEdmItem, CsdlNamed, CsdlAnnotatable {

  private String name;

  private FullQualifiedName extendsContainer;

  private List<CsdlEntitySet> entitySets = new ArrayList<>();

  private List<CsdlActionImport> actionImports = new ArrayList<>();

  private List<CsdlFunctionImport> functionImports = new ArrayList<>();

  private List<CsdlSingleton> singletons = new ArrayList<>();

  private List<CsdlAnnotation> annotations = new ArrayList<>();

  /**
   * Gets the first action import with given name.
   *
   * @param name name.
   * @return action import.
   */
  public CsdlActionImport getActionImport(final String name) {
    return getOneByName(name, getActionImports());
  }

  /**
   * Gets action imports.
   *
   * @return the action imports
   */
  public List<CsdlActionImport> getActionImports() {
    return this.actionImports;
  }

  /**
   * Gets all action imports with given name.
   *
   * @param name name.
   * @return action imports.
   */
  public List<CsdlActionImport> getActionImports(final String name) {
    final List<CsdlActionImport> actionImports = getActionImports();
    return getAllByName(name, actionImports);
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return this.annotations;
  }

  /**
   * Gets entity set.
   *
   * @param name the name
   * @return the entity set
   */
  public CsdlEntitySet getEntitySet(final String name) {
    return getOneByName(name, getEntitySets());
  }

  /**
   * Gets entity sets.
   *
   * @return the entity sets
   */
  public List<CsdlEntitySet> getEntitySets() {
    return this.entitySets;
  }

  /**
   * Gets extends container.
   *
   * @return the extends container
   */
  public String getExtendsContainer() {
    if (this.extendsContainer != null) {
      return this.extendsContainer.getFullQualifiedNameAsString();
    }
    return null;
  }

  /**
   * Gets extends container fQN.
   *
   * @return the extends container fQN
   */
  public FullQualifiedName getExtendsContainerFQN() {
    return this.extendsContainer;
  }

  /**
   * Gets the first function import with given name.
   *
   * @param name name.
   * @return function import.
   */
  public CsdlFunctionImport getFunctionImport(final String name) {
    return getOneByName(name, getFunctionImports());
  }

  /**
   * Gets function imports.
   *
   * @return the function imports
   */
  public List<CsdlFunctionImport> getFunctionImports() {
    return this.functionImports;
  }

  /**
   * Gets all function imports with given name.
   *
   * @param name name.
   * @return function imports.
   */
  public List<CsdlFunctionImport> getFunctionImports(final String name) {
    return getAllByName(name, getFunctionImports());
  }

  // Annotations
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Gets singleton.
   *
   * @param name the name
   * @return the singleton
   */
  public CsdlSingleton getSingleton(final String name) {
    return getOneByName(name, getSingletons());
  }

  /**
   * Gets singletons.
   *
   * @return the singletons
   */
  public List<CsdlSingleton> getSingletons() {
    return this.singletons;
  }

  /**
   * Sets action imports.
   *
   * @param actionImports the action imports
   * @return the action imports
   */
  public CsdlEntityContainer setActionImports(final List<CsdlActionImport> actionImports) {
    this.actionImports = actionImports;
    return this;
  }

  public CsdlEntityContainer setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  /**
   * Sets entity sets.
   *
   * @param entitySets the entity sets
   * @return the entity sets
   */
  public CsdlEntityContainer setEntitySets(final List<CsdlEntitySet> entitySets) {
    this.entitySets = entitySets;
    return this;
  }

  /**
   * Sets extends container.
   *
   * @param extendsContainer the extends container
   * @return the extends container
   */
  public CsdlEntityContainer setExtendsContainer(final String extendsContainer) {
    this.extendsContainer = new FullQualifiedName(extendsContainer);
    return this;
  }

  /**
   * Sets function imports.
   *
   * @param functionImports the function imports
   * @return the function imports
   */
  public CsdlEntityContainer setFunctionImports(final List<CsdlFunctionImport> functionImports) {
    this.functionImports = functionImports;
    return this;
  }

  /**
   * Sets name.
   *
   * @param name the name
   * @return the name
   */
  public CsdlEntityContainer setName(final String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets singletons.
   *
   * @param singletons the singletons
   * @return the singletons
   */
  public CsdlEntityContainer setSingletons(final List<CsdlSingleton> singletons) {
    this.singletons = singletons;
    return this;
  }

}
