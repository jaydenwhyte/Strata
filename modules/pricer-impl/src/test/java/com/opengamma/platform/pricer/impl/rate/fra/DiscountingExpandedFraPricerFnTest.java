/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.platform.pricer.impl.rate.fra;

import static com.opengamma.platform.pricer.impl.rate.fra.FraDummyData.FRA;
import static com.opengamma.platform.pricer.impl.rate.fra.FraDummyData.FRA_AFMA;
import static com.opengamma.platform.pricer.impl.rate.fra.FraDummyData.FRA_NONE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.opengamma.basics.currency.Currency;
import com.opengamma.basics.currency.CurrencyAmount;
import com.opengamma.platform.finance.rate.RateObservation;
import com.opengamma.platform.finance.rate.fra.ExpandedFra;
import com.opengamma.platform.finance.rate.fra.Fra;
import com.opengamma.platform.pricer.PricingEnvironment;
import com.opengamma.platform.pricer.rate.RateObservationFn;
import com.opengamma.platform.pricer.sensitivity.IborRateSensitivity;
import com.opengamma.platform.pricer.sensitivity.PointSensitivities;
import com.opengamma.platform.pricer.sensitivity.PointSensitivity;
import com.opengamma.platform.pricer.sensitivity.PointSensitivityBuilder;
import com.opengamma.platform.pricer.sensitivity.ZeroRateSensitivity;

/**
 * Test.
 */
@Test
public class DiscountingExpandedFraPricerFnTest {

  private static final double TOLERANCE = 1E-12;

  /**
   * Test future value for ISDA FRA Discounting method. 
   */
  public void test_futureValue_ISDA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    double forwardRate = 0.02;
    ExpandedFra fraExp = FRA.expand();
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    double fixedRate = FRA.getFixedRate();
    double yearFraction = fraExp.getYearFraction();
    double notional = fraExp.getNotional();
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(forwardRate);
    CurrencyAmount computed = test.futureValue(mockEnv, fraExp);
    double expected = notional * yearFraction * (forwardRate - fixedRate) / (1.0 + yearFraction * forwardRate);
    assertEquals(computed.getAmount(), expected, TOLERANCE);
  }

  /**
   * Test future value for NONE FRA Discounting method. 
   */
  public void test_futureValue_NONE() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    double forwardRate = 0.02;
    ExpandedFra fraExp = FRA_NONE.expand();
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    double fixedRate = FRA_NONE.getFixedRate();
    double yearFraction = fraExp.getYearFraction();
    double notional = fraExp.getNotional();
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(forwardRate);
    CurrencyAmount computed = test.futureValue(mockEnv, fraExp);
    double expected = notional * yearFraction * (forwardRate - fixedRate);
    assertEquals(computed.getAmount(), expected, TOLERANCE);
  }

  /**
   * Test future value for AFMA FRA Discounting method. 
   */
  public void test_futureValue_AFMA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    double forwardRate = 0.02;
    ExpandedFra fraExp = FRA_AFMA.expand();
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    double fixedRate = FRA_AFMA.getFixedRate();
    double yearFraction = fraExp.getYearFraction();
    double notional = fraExp.getNotional();
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(forwardRate);
    CurrencyAmount computed = test.futureValue(mockEnv, fraExp);
    double expected = -notional * (1.0 / (1.0 + yearFraction * forwardRate) - 1.0 / (1.0 + yearFraction * fixedRate));
    assertEquals(computed.getAmount(), expected, TOLERANCE);
  }

  /**
   * Test present value for ISDA FRA Discounting method. 
   */
  public void test_presentValue_ISDA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    double forwardRate = 0.02;
    double discountFactor = 0.98d;
    ExpandedFra fraExp = FRA_NONE.expand();
    Currency currency = FRA_NONE.getCurrency();
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(forwardRate);
    when(mockEnv.discountFactor(currency, fraExp.getPaymentDate())).thenReturn(discountFactor);
    CurrencyAmount pvComputed = test.presentValue(mockEnv, fraExp);
    CurrencyAmount pvExpected = test.futureValue(mockEnv, fraExp).multipliedBy(discountFactor);
    assertEquals(pvComputed.getAmount(), pvExpected.getAmount(), TOLERANCE);
  }

  /**
   * Test present value for NONE FRA Discounting method. 
   */
  public void test_presentValue_NONE() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    double forwardRate = 0.02;
    double discountFactor = 0.98d;
    ExpandedFra fraExp = FRA.expand();
    Currency currency = FRA.getCurrency();
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(forwardRate);
    when(mockEnv.discountFactor(currency, fraExp.getPaymentDate())).thenReturn(discountFactor);
    CurrencyAmount pvComputed = test.presentValue(mockEnv, fraExp);
    CurrencyAmount pvExpected = test.futureValue(mockEnv, fraExp).multipliedBy(discountFactor);
    assertEquals(pvComputed.getAmount(), pvExpected.getAmount(), TOLERANCE);
  }

  /**
   * Test present value for ISDA FRA Discounting method. 
   */
  public void test_presentValue_AFMA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    double forwardRate = 0.02;
    double discountFactor = 0.98d;
    ExpandedFra fraExp = FRA_AFMA.expand();
    Currency currency = FRA_AFMA.getCurrency();
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(forwardRate);
    when(mockEnv.discountFactor(currency, fraExp.getPaymentDate()))
        .thenReturn(discountFactor);
    CurrencyAmount pvComputed = test.presentValue(mockEnv, fraExp);
    CurrencyAmount pvExpected = test.futureValue(mockEnv, fraExp).multipliedBy(discountFactor);
    assertEquals(pvComputed.getAmount(), pvExpected.getAmount(), TOLERANCE);
  }

  //-------------------------------------------------------------------------
  /**
   * Test future value sensitivity for ISDA FRA discounting method. 
   */
  public void test_futureValueSensitivity_ISDA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    ExpandedFra fraExp = FRA.expand();
    double forwardRate = 0.05;
    LocalDate fixingDate = FRA.getStartDate();
    PointSensitivityBuilder sens = IborRateSensitivity.of(FRA.getIndex(), fixingDate, 1d);
    when(mockObs.rateSensitivity(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(sens);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), FRA.getStartDate(), FRA.getEndDate()))
        .thenReturn(forwardRate);
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    PointSensitivities sensitivity = test.futureValueSensitivity(mockEnv, fraExp);
    double eps = 1.e-7;
    double fdSense = futureValueFwdSensitivity(FRA, forwardRate, eps);

    ImmutableList<PointSensitivity> sensitivities = sensitivity.getSensitivities();
    assertEquals(sensitivities.size(), 1);
    PointSensitivity sensitivity0 = sensitivities.get(0);
    assertEquals(sensitivity0.getCurveKey(), FRA.getIndex());
    assertEquals(sensitivity0.getDate(), fixingDate);
    assertEquals(sensitivity0.getSensitivity(), fdSense, FRA.getNotional() * eps);
  }

  /**
   * Test future value sensitivity for NONE FRA discounting method.
   */
  public void test_futureValueSensitivity_NONE() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    ExpandedFra fraExp = FRA_NONE.expand();
    double forwardRate = 0.035;
    LocalDate fixingDate = FRA_NONE.getStartDate();
    PointSensitivityBuilder sens = IborRateSensitivity.of(FRA_NONE.getIndex(), fixingDate, 1d);
    when(mockObs.rateSensitivity(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(sens);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), FRA_NONE.getStartDate(), FRA_NONE.getEndDate()))
        .thenReturn(forwardRate);
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    PointSensitivities sensitivity = test.futureValueSensitivity(mockEnv, fraExp);
    double eps = 1.e-7;
    double fdSense = futureValueFwdSensitivity(FRA_NONE, forwardRate, eps);

    ImmutableList<PointSensitivity> sensitivities = sensitivity.getSensitivities();
    assertEquals(sensitivities.size(), 1);
    PointSensitivity sensitivity0 = sensitivities.get(0);
    assertEquals(sensitivity0.getCurveKey(), FRA_NONE.getIndex());
    assertEquals(sensitivity0.getDate(), fixingDate);
    assertEquals(sensitivity0.getSensitivity(), fdSense, FRA_NONE.getNotional() * eps);
  }

  /**
   * Test future value sensitivity for AFMA FRA discounting method. 
   */
  public void test_futureValueSensitivity_AFMA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    ExpandedFra fraExp = FRA_AFMA.expand();
    double forwardRate = 0.04;
    LocalDate fixingDate = FRA_AFMA.getStartDate();
    PointSensitivityBuilder sens = IborRateSensitivity.of(FRA_AFMA.getIndex(), fixingDate, 1d);
    when(mockObs.rateSensitivity(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(sens);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), FRA_AFMA.getStartDate(), FRA_AFMA.getEndDate()))
        .thenReturn(forwardRate);
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    PointSensitivities sensitivity = test.futureValueSensitivity(mockEnv, fraExp);
    double eps = 1.e-7;
    double fdSense = futureValueFwdSensitivity(FRA_AFMA, forwardRate, eps);

    ImmutableList<PointSensitivity> sensitivities = sensitivity.getSensitivities();
    assertEquals(sensitivities.size(), 1);
    PointSensitivity sensitivity0 = sensitivities.get(0);
    assertEquals(sensitivity0.getCurveKey(), FRA_AFMA.getIndex());
    assertEquals(sensitivity0.getDate(), fixingDate);
    assertEquals(sensitivity0.getSensitivity(), fdSense, FRA_AFMA.getNotional() * eps);
  }

  /**
   * Test present value sensitivity for ISDA  
   */
  public void test_presentValueSensitivity_ISDA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    ExpandedFra fraExp = FRA.expand();
    double forwardRate = 0.05;
    double discountRate = 0.015;
    double paymentTime = 0.3;
    double discountFactor = Math.exp(-discountRate * paymentTime);
    LocalDate fixingDate = FRA.getStartDate();
    PointSensitivityBuilder sens = IborRateSensitivity.of(FRA.getIndex(), fixingDate, 1d);
    when(mockEnv.discountFactor(FRA.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(discountFactor);
    when(mockEnv.discountFactorZeroRateSensitivity(FRA.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(ZeroRateSensitivity.of(
            fraExp.getCurrency(), fraExp.getPaymentDate(), -discountFactor * paymentTime));
    when(mockObs.rateSensitivity(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(sens);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), FRA.getStartDate(), FRA.getEndDate()))
        .thenReturn(forwardRate);
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    PointSensitivities sensitivity = test.presentValueSensitivity(mockEnv, fraExp);
    double eps = 1.e-7;
    double fdDscSense = dscSensitivity(FRA, forwardRate, discountFactor, paymentTime, eps);
    double fdSense = presentValueFwdSensitivity(FRA, forwardRate, discountFactor, paymentTime, eps);

    ImmutableList<PointSensitivity> sensitivities = sensitivity.getSensitivities();
    assertEquals(sensitivities.size(), 2);
    PointSensitivity sensitivity0 = sensitivities.get(0);
    assertEquals(sensitivity0.getCurveKey(), FRA.getIndex());
    assertEquals(sensitivity0.getDate(), fixingDate);
    assertEquals(sensitivity0.getSensitivity(), fdSense, FRA.getNotional() * eps);
    PointSensitivity sensitivity1 = sensitivities.get(1);
    assertEquals(sensitivity1.getCurveKey(), FRA.getCurrency());
    assertEquals(sensitivity1.getDate(), fraExp.getPaymentDate());
    assertEquals(sensitivity1.getSensitivity(), fdDscSense, FRA.getNotional() * eps);
  }

  /**
   * Test present value sensitivity for NONE FRA discounting method. 
   */
  public void test_presentValueSensitivity_NONE() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    ExpandedFra fraExp = FRA_NONE.expand();
    double forwardRate = 0.025;
    double discountRate = 0.01;
    double paymentTime = 0.3;
    double discountFactor = Math.exp(-discountRate * paymentTime);
    LocalDate fixingDate = FRA_NONE.getStartDate();
    PointSensitivityBuilder sens = IborRateSensitivity.of(FRA.getIndex(), fixingDate, 1d);
    when(mockEnv.discountFactor(FRA_NONE.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(discountFactor);
    when(mockEnv.discountFactorZeroRateSensitivity(FRA.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(ZeroRateSensitivity.of(
            fraExp.getCurrency(), fraExp.getPaymentDate(), -discountFactor * paymentTime));
    when(mockObs.rateSensitivity(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(sens);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), FRA_NONE.getStartDate(), FRA_NONE.getEndDate()))
        .thenReturn(forwardRate);
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    PointSensitivities sensitivity = test.presentValueSensitivity(mockEnv, fraExp);
    double eps = 1.e-7;
    double fdDscSense = dscSensitivity(FRA_NONE, forwardRate, discountFactor, paymentTime, eps);
    double fdSense = presentValueFwdSensitivity(FRA_NONE, forwardRate, discountFactor, paymentTime, eps);

    ImmutableList<PointSensitivity> sensitivities = sensitivity.getSensitivities();
    assertEquals(sensitivities.size(), 2);
    PointSensitivity sensitivity0 = sensitivities.get(0);
    assertEquals(sensitivity0.getCurveKey(), FRA_NONE.getIndex());
    assertEquals(sensitivity0.getDate(), fixingDate);
    assertEquals(sensitivity0.getSensitivity(), fdSense, FRA_NONE.getNotional() * eps);
    PointSensitivity sensitivity1 = sensitivities.get(1);
    assertEquals(sensitivity1.getCurveKey(), FRA_NONE.getCurrency());
    assertEquals(sensitivity1.getDate(), fraExp.getPaymentDate());
    assertEquals(sensitivity1.getSensitivity(), fdDscSense, FRA_NONE.getNotional() * eps);
  }

  /**
   * Test present value sensitivity for AFMA FRA discounting method. 
   */
  public void test_presentValueSensitivity_AFMA() {
    RateObservationFn<RateObservation> mockObs = mock(RateObservationFn.class);
    PricingEnvironment mockEnv = mock(PricingEnvironment.class);
    ExpandedFra fraExp = FRA_AFMA.expand();
    double forwardRate = 0.05;
    double discountRate = 0.025;
    double paymentTime = 0.3;
    double discountFactor = Math.exp(-discountRate * paymentTime);
    LocalDate fixingDate = FRA_AFMA.getStartDate();
    PointSensitivityBuilder sens = IborRateSensitivity.of(FRA.getIndex(), fixingDate, 1d);
    when(mockEnv.discountFactor(FRA_AFMA.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(discountFactor);
    when(mockEnv.discountFactorZeroRateSensitivity(FRA.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(ZeroRateSensitivity.of(
            fraExp.getCurrency(), fraExp.getPaymentDate(), -discountFactor * paymentTime));
    when(mockObs.rateSensitivity(mockEnv, fraExp.getFloatingRate(), fraExp.getStartDate(), fraExp.getEndDate()))
        .thenReturn(sens);
    when(mockObs.rate(mockEnv, fraExp.getFloatingRate(), FRA_AFMA.getStartDate(), FRA_AFMA.getEndDate()))
        .thenReturn(forwardRate);
    DiscountingExpandedFraPricerFn test = new DiscountingExpandedFraPricerFn(mockObs);
    PointSensitivities sensitivity = test.presentValueSensitivity(mockEnv, fraExp);
    double eps = 1.e-7;
    double fdDscSense = dscSensitivity(FRA_AFMA, forwardRate, discountFactor, paymentTime, eps);
    double fdSense = presentValueFwdSensitivity(FRA_AFMA, forwardRate, discountFactor, paymentTime, eps);

    ImmutableList<PointSensitivity> sensitivities = sensitivity.getSensitivities();
    assertEquals(sensitivities.size(), 2);
    PointSensitivity sensitivity0 = sensitivities.get(0);
    assertEquals(sensitivity0.getCurveKey(), FRA_AFMA.getIndex());
    assertEquals(sensitivity0.getDate(), fixingDate);
    assertEquals(sensitivity0.getSensitivity(), fdSense, FRA_AFMA.getNotional() * eps);
    PointSensitivity sensitivity1 = sensitivities.get(1);
    assertEquals(sensitivity1.getCurveKey(), FRA_AFMA.getCurrency());
    assertEquals(sensitivity1.getDate(), fraExp.getPaymentDate());
    assertEquals(sensitivity1.getSensitivity(), fdDscSense, FRA_AFMA.getNotional() * eps);
  }

  //-------------------------------------------------------------------------
  private double futureValueFwdSensitivity(Fra fra, double forwardRate, double eps) {

    RateObservationFn<RateObservation> obsFuncNew = mock(RateObservationFn.class);
    PricingEnvironment envNew = mock(PricingEnvironment.class);
    ExpandedFra fraExp = fra.expand();
    when(obsFuncNew.rate(envNew, fraExp.getFloatingRate(), fra.getStartDate(), fra.getEndDate()))
        .thenReturn(forwardRate + eps);
    CurrencyAmount upValue = new DiscountingExpandedFraPricerFn(obsFuncNew).futureValue(envNew, fraExp);
    when(obsFuncNew.rate(envNew, fraExp.getFloatingRate(), fra.getStartDate(), fra.getEndDate()))
        .thenReturn(forwardRate - eps);
    CurrencyAmount downValue = new DiscountingExpandedFraPricerFn(obsFuncNew).futureValue(envNew, fraExp);
    return upValue.minus(downValue).multipliedBy(0.5 / eps).getAmount();
  }

  private double presentValueFwdSensitivity(
      Fra fra, double forwardRate, double discountFactor, double paymentTime, double eps) {

    RateObservationFn<RateObservation> obsFuncNew = mock(RateObservationFn.class);
    PricingEnvironment envNew = mock(PricingEnvironment.class);
    ExpandedFra fraExp = fra.expand();
    when(envNew.discountFactor(fra.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(discountFactor);
    when(envNew.relativeTime(fraExp.getPaymentDate()))
        .thenReturn(paymentTime);
    when(obsFuncNew.rate(envNew, fraExp.getFloatingRate(), fra.getStartDate(), fra.getEndDate()))
        .thenReturn(forwardRate + eps);
    CurrencyAmount upValue = new DiscountingExpandedFraPricerFn(obsFuncNew).presentValue(envNew, fraExp);
    when(obsFuncNew.rate(envNew, fraExp.getFloatingRate(), fra.getStartDate(), fra.getEndDate()))
        .thenReturn(forwardRate - eps);
    CurrencyAmount downValue = new DiscountingExpandedFraPricerFn(obsFuncNew).presentValue(envNew, fraExp);
    return upValue.minus(downValue).multipliedBy(0.5 / eps).getAmount();
  }

  private double dscSensitivity(
      Fra fra, double forwardRate, double discountFactor, double paymentTime, double eps) {

    PricingEnvironment envNew = mock(PricingEnvironment.class);
    RateObservationFn<RateObservation> obsFuncNew = mock(RateObservationFn.class);
    ExpandedFra fraExp = fra.expand();
    when(obsFuncNew.rate(envNew, fraExp.getFloatingRate(), fra.getStartDate(), fra.getEndDate()))
        .thenReturn(forwardRate);
    when(envNew.discountFactor(fra.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(discountFactor * Math.exp(-eps * paymentTime));
    CurrencyAmount upDscValue = new DiscountingExpandedFraPricerFn(obsFuncNew).presentValue(envNew, fraExp);
    when(envNew.discountFactor(fra.getCurrency(), fraExp.getPaymentDate()))
        .thenReturn(discountFactor * Math.exp(eps * paymentTime));
    CurrencyAmount downDscValue = new DiscountingExpandedFraPricerFn(obsFuncNew).presentValue(envNew, fraExp);
    return upDscValue.minus(downDscValue).multipliedBy(0.5 / eps).getAmount();
  }

}