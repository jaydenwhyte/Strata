/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.platform.finance.swap;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableValidator;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.basics.currency.Currency;
import com.opengamma.basics.currency.CurrencyAmount;
import com.opengamma.basics.value.ValueSchedule;
import com.opengamma.collect.ArgChecker;
import com.opengamma.collect.Messages;

/**
 * Defines the schedule of notional amounts.
 * <p>
 * Interest rate swaps are based on a notional amount of money.
 * The notional can vary during the lifetime of the swap, but only at payment period boundaries.
 * It is not permitted to vary at an intermediate accrual (compounding) period boundary.
 * <p>
 * In most cases, the notional amount is not exchanged, with only the net difference being exchanged.
 * However, in certain cases, initial, final or intermediate amounts are exchanged.
 * In this case, the notional can be referred to as the <i>principal</i>.
 */
@BeanDefinition
public final class NotionalSchedule
    implements ImmutableBean, Serializable {

  /**
   * The currency of the swap leg associated with the notional.
   * <p>
   * This is the currency of the swap leg and the currency that interest calculation is made in.
   * <p>
   * The amounts of the notional are usually expressed in terms of this currency,
   * however they can be converted from amounts in a different currency.
   * See the optional {@code fxReset} property.
   */
  @PropertyDefinition(validate = "notNull")
  private final Currency currency;
  /**
   * The FX reset definition, optional.
   * <p>
   * This property is used when the defined amount of the notional is specified in
   * a currency other than the currency of the swap leg. When this occurs, the notional
   * amount has to be converted using an FX rate to the swap leg currency. This conversion
   * occurs at each payment period boundary and usually corresponds to an actual
   * exchange of money between the counterparties.
   */
  @PropertyDefinition(get = "optional")
  private final FxResetCalculation fxReset;
  /**
   * The notional amount.
   * <p>
   * This defines the notional as an initial amount and a list of adjustments.
   * The notional expressed here is intended to always be positive.
   * <p>
   * The notional is only allowed to change at payment period boundaries.
   * As such, the {@code ValueSchedule} steps are defined relative to the payment schedule.
   */
  @PropertyDefinition(validate = "notNull")
  private final ValueSchedule amount;
  /**
   * The flag indicating whether to exchange the initial notional.
   * <p>
   * Setting this to true indicates that the notional is transferred at the start of the trade.
   */
  @PropertyDefinition
  private final boolean initialExchange;
  /**
   * The flag indicating whether to exchange the final notional.
   * <p>
   * Setting this to true indicates that the notional is transferred at the end of the trade.
   */
  @PropertyDefinition
  private final boolean finalExchange;
  /**
   * The flag indicating whether to exchange the differences in the notional during the lifetime of the swap.
   * <p>
   * Setting this to true indicates that the notional is transferred when it changes during the trade.
   * This should typically be set to true in the case of an FX reset swap, or one with a varying notional.
   */
  @PropertyDefinition
  private final boolean intermediateExchange;

  //-------------------------------------------------------------------------
  /**
   * Obtains a {@code NotionalSchedule} with a single amount that does not change over time.
   * 
   * @param notional  the single notional that does not change over time
   * @return the notional amount
   */
  public static NotionalSchedule of(CurrencyAmount notional) {
    ArgChecker.notNull(notional, "notional");
    return NotionalSchedule.builder()
        .currency(notional.getCurrency())
        .amount(ValueSchedule.of(notional.getAmount()))
        .build();
  }

  /**
   * Obtains a {@code NotionalSchedule} with a single amount that does not change over time.
   * 
   * @param currency  the currency of the notional and swap payments
   * @param amount  the single notional amount that does not change over time
   * @return the notional amount
   */
  public static NotionalSchedule of(Currency currency, double amount) {
    ArgChecker.notNull(currency, "currency");
    return NotionalSchedule.builder()
        .currency(currency)
        .amount(ValueSchedule.of(amount))
        .build();
  }

  /**
   * Obtains a {@code NotionalSchedule} with a notional amount that can change over time.
   * 
   * @param currency  the currency of the notional and swap payments
   * @param amountSchedule  the schedule describing how the notional changes over time
   * @return the notional amount
   */
  public static NotionalSchedule of(Currency currency, ValueSchedule amountSchedule) {
    ArgChecker.notNull(currency, "currency");
    ArgChecker.notNull(amountSchedule, "amountSchedule");
    return NotionalSchedule.builder()
        .currency(currency)
        .amount(amountSchedule)
        .build();
  }

  //-------------------------------------------------------------------------
  @ImmutableValidator
  private void validate() {
    if (fxReset != null) {
      if (fxReset.getReferenceCurrency().equals(currency)) {
        throw new IllegalArgumentException(
            Messages.format("Currency {} must not equal FxResetCalculation reference currency {}",
                currency, fxReset.getReferenceCurrency()));
      }
      if (!fxReset.getIndex().getCurrencyPair().contains(currency)) {
        throw new IllegalArgumentException(
            Messages.format("Currency {} must be one of those in the FxResetCalculation index {}",
                currency, fxReset.getIndex()));
      }
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code NotionalSchedule}.
   * @return the meta-bean, not null
   */
  public static NotionalSchedule.Meta meta() {
    return NotionalSchedule.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(NotionalSchedule.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static NotionalSchedule.Builder builder() {
    return new NotionalSchedule.Builder();
  }

  private NotionalSchedule(
      Currency currency,
      FxResetCalculation fxReset,
      ValueSchedule amount,
      boolean initialExchange,
      boolean finalExchange,
      boolean intermediateExchange) {
    JodaBeanUtils.notNull(currency, "currency");
    JodaBeanUtils.notNull(amount, "amount");
    this.currency = currency;
    this.fxReset = fxReset;
    this.amount = amount;
    this.initialExchange = initialExchange;
    this.finalExchange = finalExchange;
    this.intermediateExchange = intermediateExchange;
    validate();
  }

  @Override
  public NotionalSchedule.Meta metaBean() {
    return NotionalSchedule.Meta.INSTANCE;
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
   * Gets the currency of the swap leg associated with the notional.
   * <p>
   * This is the currency of the swap leg and the currency that interest calculation is made in.
   * <p>
   * The amounts of the notional are usually expressed in terms of this currency,
   * however they can be converted from amounts in a different currency.
   * See the optional {@code fxReset} property.
   * @return the value of the property, not null
   */
  public Currency getCurrency() {
    return currency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the FX reset definition, optional.
   * <p>
   * This property is used when the defined amount of the notional is specified in
   * a currency other than the currency of the swap leg. When this occurs, the notional
   * amount has to be converted using an FX rate to the swap leg currency. This conversion
   * occurs at each payment period boundary and usually corresponds to an actual
   * exchange of money between the counterparties.
   * @return the optional value of the property, not null
   */
  public Optional<FxResetCalculation> getFxReset() {
    return Optional.ofNullable(fxReset);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the notional amount.
   * <p>
   * This defines the notional as an initial amount and a list of adjustments.
   * The notional expressed here is intended to always be positive.
   * <p>
   * The notional is only allowed to change at payment period boundaries.
   * As such, the {@code ValueSchedule} steps are defined relative to the payment schedule.
   * @return the value of the property, not null
   */
  public ValueSchedule getAmount() {
    return amount;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag indicating whether to exchange the initial notional.
   * <p>
   * Setting this to true indicates that the notional is transferred at the start of the trade.
   * @return the value of the property
   */
  public boolean isInitialExchange() {
    return initialExchange;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag indicating whether to exchange the final notional.
   * <p>
   * Setting this to true indicates that the notional is transferred at the end of the trade.
   * @return the value of the property
   */
  public boolean isFinalExchange() {
    return finalExchange;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag indicating whether to exchange the differences in the notional during the lifetime of the swap.
   * <p>
   * Setting this to true indicates that the notional is transferred when it changes during the trade.
   * This should typically be set to true in the case of an FX reset swap, or one with a varying notional.
   * @return the value of the property
   */
  public boolean isIntermediateExchange() {
    return intermediateExchange;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      NotionalSchedule other = (NotionalSchedule) obj;
      return JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(fxReset, other.fxReset) &&
          JodaBeanUtils.equal(getAmount(), other.getAmount()) &&
          (isInitialExchange() == other.isInitialExchange()) &&
          (isFinalExchange() == other.isFinalExchange()) &&
          (isIntermediateExchange() == other.isIntermediateExchange());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash = hash * 31 + JodaBeanUtils.hashCode(fxReset);
    hash = hash * 31 + JodaBeanUtils.hashCode(getAmount());
    hash = hash * 31 + JodaBeanUtils.hashCode(isInitialExchange());
    hash = hash * 31 + JodaBeanUtils.hashCode(isFinalExchange());
    hash = hash * 31 + JodaBeanUtils.hashCode(isIntermediateExchange());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(224);
    buf.append("NotionalSchedule{");
    buf.append("currency").append('=').append(getCurrency()).append(',').append(' ');
    buf.append("fxReset").append('=').append(fxReset).append(',').append(' ');
    buf.append("amount").append('=').append(getAmount()).append(',').append(' ');
    buf.append("initialExchange").append('=').append(isInitialExchange()).append(',').append(' ');
    buf.append("finalExchange").append('=').append(isFinalExchange()).append(',').append(' ');
    buf.append("intermediateExchange").append('=').append(JodaBeanUtils.toString(isIntermediateExchange()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code NotionalSchedule}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> currency = DirectMetaProperty.ofImmutable(
        this, "currency", NotionalSchedule.class, Currency.class);
    /**
     * The meta-property for the {@code fxReset} property.
     */
    private final MetaProperty<FxResetCalculation> fxReset = DirectMetaProperty.ofImmutable(
        this, "fxReset", NotionalSchedule.class, FxResetCalculation.class);
    /**
     * The meta-property for the {@code amount} property.
     */
    private final MetaProperty<ValueSchedule> amount = DirectMetaProperty.ofImmutable(
        this, "amount", NotionalSchedule.class, ValueSchedule.class);
    /**
     * The meta-property for the {@code initialExchange} property.
     */
    private final MetaProperty<Boolean> initialExchange = DirectMetaProperty.ofImmutable(
        this, "initialExchange", NotionalSchedule.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code finalExchange} property.
     */
    private final MetaProperty<Boolean> finalExchange = DirectMetaProperty.ofImmutable(
        this, "finalExchange", NotionalSchedule.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code intermediateExchange} property.
     */
    private final MetaProperty<Boolean> intermediateExchange = DirectMetaProperty.ofImmutable(
        this, "intermediateExchange", NotionalSchedule.class, Boolean.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "currency",
        "fxReset",
        "amount",
        "initialExchange",
        "finalExchange",
        "intermediateExchange");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return currency;
        case -449555555:  // fxReset
          return fxReset;
        case -1413853096:  // amount
          return amount;
        case -511982201:  // initialExchange
          return initialExchange;
        case -1048781383:  // finalExchange
          return finalExchange;
        case -2147112388:  // intermediateExchange
          return intermediateExchange;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public NotionalSchedule.Builder builder() {
      return new NotionalSchedule.Builder();
    }

    @Override
    public Class<? extends NotionalSchedule> beanType() {
      return NotionalSchedule.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Currency> currency() {
      return currency;
    }

    /**
     * The meta-property for the {@code fxReset} property.
     * @return the meta-property, not null
     */
    public MetaProperty<FxResetCalculation> fxReset() {
      return fxReset;
    }

    /**
     * The meta-property for the {@code amount} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ValueSchedule> amount() {
      return amount;
    }

    /**
     * The meta-property for the {@code initialExchange} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Boolean> initialExchange() {
      return initialExchange;
    }

    /**
     * The meta-property for the {@code finalExchange} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Boolean> finalExchange() {
      return finalExchange;
    }

    /**
     * The meta-property for the {@code intermediateExchange} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Boolean> intermediateExchange() {
      return intermediateExchange;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return ((NotionalSchedule) bean).getCurrency();
        case -449555555:  // fxReset
          return ((NotionalSchedule) bean).fxReset;
        case -1413853096:  // amount
          return ((NotionalSchedule) bean).getAmount();
        case -511982201:  // initialExchange
          return ((NotionalSchedule) bean).isInitialExchange();
        case -1048781383:  // finalExchange
          return ((NotionalSchedule) bean).isFinalExchange();
        case -2147112388:  // intermediateExchange
          return ((NotionalSchedule) bean).isIntermediateExchange();
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
   * The bean-builder for {@code NotionalSchedule}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<NotionalSchedule> {

    private Currency currency;
    private FxResetCalculation fxReset;
    private ValueSchedule amount;
    private boolean initialExchange;
    private boolean finalExchange;
    private boolean intermediateExchange;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(NotionalSchedule beanToCopy) {
      this.currency = beanToCopy.getCurrency();
      this.fxReset = beanToCopy.fxReset;
      this.amount = beanToCopy.getAmount();
      this.initialExchange = beanToCopy.isInitialExchange();
      this.finalExchange = beanToCopy.isFinalExchange();
      this.intermediateExchange = beanToCopy.isIntermediateExchange();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return currency;
        case -449555555:  // fxReset
          return fxReset;
        case -1413853096:  // amount
          return amount;
        case -511982201:  // initialExchange
          return initialExchange;
        case -1048781383:  // finalExchange
          return finalExchange;
        case -2147112388:  // intermediateExchange
          return intermediateExchange;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          this.currency = (Currency) newValue;
          break;
        case -449555555:  // fxReset
          this.fxReset = (FxResetCalculation) newValue;
          break;
        case -1413853096:  // amount
          this.amount = (ValueSchedule) newValue;
          break;
        case -511982201:  // initialExchange
          this.initialExchange = (Boolean) newValue;
          break;
        case -1048781383:  // finalExchange
          this.finalExchange = (Boolean) newValue;
          break;
        case -2147112388:  // intermediateExchange
          this.intermediateExchange = (Boolean) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public NotionalSchedule build() {
      return new NotionalSchedule(
          currency,
          fxReset,
          amount,
          initialExchange,
          finalExchange,
          intermediateExchange);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code currency} property in the builder.
     * @param currency  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder currency(Currency currency) {
      JodaBeanUtils.notNull(currency, "currency");
      this.currency = currency;
      return this;
    }

    /**
     * Sets the {@code fxReset} property in the builder.
     * @param fxReset  the new value
     * @return this, for chaining, not null
     */
    public Builder fxReset(FxResetCalculation fxReset) {
      this.fxReset = fxReset;
      return this;
    }

    /**
     * Sets the {@code amount} property in the builder.
     * @param amount  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder amount(ValueSchedule amount) {
      JodaBeanUtils.notNull(amount, "amount");
      this.amount = amount;
      return this;
    }

    /**
     * Sets the {@code initialExchange} property in the builder.
     * @param initialExchange  the new value
     * @return this, for chaining, not null
     */
    public Builder initialExchange(boolean initialExchange) {
      this.initialExchange = initialExchange;
      return this;
    }

    /**
     * Sets the {@code finalExchange} property in the builder.
     * @param finalExchange  the new value
     * @return this, for chaining, not null
     */
    public Builder finalExchange(boolean finalExchange) {
      this.finalExchange = finalExchange;
      return this;
    }

    /**
     * Sets the {@code intermediateExchange} property in the builder.
     * @param intermediateExchange  the new value
     * @return this, for chaining, not null
     */
    public Builder intermediateExchange(boolean intermediateExchange) {
      this.intermediateExchange = intermediateExchange;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(224);
      buf.append("NotionalSchedule.Builder{");
      buf.append("currency").append('=').append(JodaBeanUtils.toString(currency)).append(',').append(' ');
      buf.append("fxReset").append('=').append(JodaBeanUtils.toString(fxReset)).append(',').append(' ');
      buf.append("amount").append('=').append(JodaBeanUtils.toString(amount)).append(',').append(' ');
      buf.append("initialExchange").append('=').append(JodaBeanUtils.toString(initialExchange)).append(',').append(' ');
      buf.append("finalExchange").append('=').append(JodaBeanUtils.toString(finalExchange)).append(',').append(' ');
      buf.append("intermediateExchange").append('=').append(JodaBeanUtils.toString(intermediateExchange));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
