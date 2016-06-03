/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.data.scenario;

import static com.opengamma.strata.collect.Guavate.toImmutableList;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.function.ObjIntFunction;

/**
 * A market data box containing a single value which is used in all scenarios.
 * <p>
 * A market data box containing a single value can therefore be used with any number of scenarios.
 * 
 * @param <T>  the type of data held in the box
 */
@BeanDefinition
public final class SingleMarketDataBox<T>
    implements ImmutableBean, MarketDataBox<T>, Serializable {

  /**
   * The market data value used in all scenarios.
   */
  @PropertyDefinition(validate = "notNull")
  private final T value;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance containing a single market data value.
   * 
   * @param <T> the type of the value
   * @param value  the market data value
   * @return a market data box containing a single market data value
   */
  public static <T> SingleMarketDataBox<T> of(T value) {
    return new SingleMarketDataBox<>(value);
  }

  //-------------------------------------------------------------------------
  @Override
  public T getSingleValue() {
    return value;
  }

  @Override
  public ScenarioArray<T> getScenarioValue() {
    throw new IllegalStateException("This box does not contain a scenario value");
  }

  @Override
  public T getValue(int scenarioIndex) {
    ArgChecker.notNegative(scenarioIndex, "scenarioIndex");
    return value;
  }

  @Override
  public boolean isSingleValue() {
    return true;
  }

  @Override
  public int getScenarioCount() {
    return 1;
  }

  @Override
  public Class<?> getMarketDataType() {
    return value.getClass();
  }

  //-------------------------------------------------------------------------
  @Override
  public <R> MarketDataBox<R> map(Function<T, R> fn) {
    return MarketDataBox.ofSingleValue(fn.apply(value));
  }

  @Override
  public <R> MarketDataBox<R> mapWithIndex(int scenarioCount, ObjIntFunction<T, R> fn) {
    List<R> perturbedValues = IntStream.range(0, scenarioCount)
        .mapToObj(idx -> fn.apply(value, idx))
        .collect(toImmutableList());
    return MarketDataBox.ofScenarioValues(perturbedValues);
  }

  @Override
  public <U, R> MarketDataBox<R> combineWith(MarketDataBox<U> other, BiFunction<T, U, R> fn) {
    return other.isSingleValue() ?
        combineWithSingle(other, fn) :
        combineWithMultiple(other, fn);
  }

  private <U, R> MarketDataBox<R> combineWithMultiple(MarketDataBox<U> other, BiFunction<T, U, R> fn) {
    ScenarioArray<U> otherValue = other.getScenarioValue();
    int scenarioCount = otherValue.getScenarioCount();

    List<R> values = IntStream.range(0, scenarioCount)
        .mapToObj(i -> fn.apply(value, other.getValue(i)))
        .collect(toImmutableList());
    return MarketDataBox.ofScenarioValues(values);
  }

  private <U, R> MarketDataBox<R> combineWithSingle(MarketDataBox<U> other, BiFunction<T, U, R> fn) {
    U otherValue = other.getSingleValue();
    return MarketDataBox.ofSingleValue(fn.apply(value, otherValue));
  }

  @Override
  public Stream<T> stream() {
    return Stream.of(value);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SingleMarketDataBox}.
   * @return the meta-bean, not null
   */
  @SuppressWarnings("rawtypes")
  public static SingleMarketDataBox.Meta meta() {
    return SingleMarketDataBox.Meta.INSTANCE;
  }

  /**
   * The meta-bean for {@code SingleMarketDataBox}.
   * @param <R>  the bean's generic type
   * @param cls  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R> SingleMarketDataBox.Meta<R> metaSingleMarketDataBox(Class<R> cls) {
    return SingleMarketDataBox.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SingleMarketDataBox.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @param <T>  the type
   * @return the builder, not null
   */
  public static <T> SingleMarketDataBox.Builder<T> builder() {
    return new SingleMarketDataBox.Builder<T>();
  }

  private SingleMarketDataBox(
      T value) {
    JodaBeanUtils.notNull(value, "value");
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public SingleMarketDataBox.Meta<T> metaBean() {
    return SingleMarketDataBox.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the market data value used in all scenarios.
   * @return the value of the property, not null
   */
  public T getValue() {
    return value;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder<T> toBuilder() {
    return new Builder<T>(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SingleMarketDataBox<?> other = (SingleMarketDataBox<?>) obj;
      return JodaBeanUtils.equal(value, other.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(value);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("SingleMarketDataBox{");
    buf.append("value").append('=').append(JodaBeanUtils.toString(value));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SingleMarketDataBox}.
   * @param <T>  the type
   */
  public static final class Meta<T> extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code value} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<T> value = (DirectMetaProperty) DirectMetaProperty.ofImmutable(
        this, "value", SingleMarketDataBox.class, Object.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "value");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          return value;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public SingleMarketDataBox.Builder<T> builder() {
      return new SingleMarketDataBox.Builder<T>();
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends SingleMarketDataBox<T>> beanType() {
      return (Class) SingleMarketDataBox.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code value} property.
     * @return the meta-property, not null
     */
    public MetaProperty<T> value() {
      return value;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          return ((SingleMarketDataBox<?>) bean).getValue();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code SingleMarketDataBox}.
   * @param <T>  the type
   */
  public static final class Builder<T> extends DirectFieldsBeanBuilder<SingleMarketDataBox<T>> {

    private T value;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(SingleMarketDataBox<T> beanToCopy) {
      this.value = beanToCopy.getValue();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          return value;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder<T> set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          this.value = (T) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder<T> set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder<T> setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder<T> setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder<T> setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public SingleMarketDataBox<T> build() {
      return new SingleMarketDataBox<T>(
          value);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the market data value used in all scenarios.
     * @param value  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<T> value(T value) {
      JodaBeanUtils.notNull(value, "value");
      this.value = value;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("SingleMarketDataBox.Builder{");
      buf.append("value").append('=').append(JodaBeanUtils.toString(value));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
