package com.store.store9m.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_result")
public class OrderResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "o_id", nullable = false)
    private String orderId;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "payment_type_charge_fee", nullable = false)
    private BigDecimal paymentTypeChargeFee;

    @Column(name = "rtn_code", nullable = false)
    private int rtnCode;

    @Column(name = "rtn_msg", nullable = false)
    private String rtnMsg;

    @Column(name = "simulate_paid", nullable = false)
    private int simulatePaid;

    @Column(name = "trade_no", nullable = false)
    private String tradeNo;

    @Column(name = "check_mac_value", nullable = false)
    private String checkMacValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public BigDecimal getPaymentTypeChargeFee() {
		return paymentTypeChargeFee;
	}

	public void setPaymentTypeChargeFee(BigDecimal paymentTypeChargeFee) {
		this.paymentTypeChargeFee = paymentTypeChargeFee;
	}

	public int getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(int rtnCode) {
		this.rtnCode = rtnCode;
	}

	public String getRtnMsg() {
		return rtnMsg;
	}

	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}

	public int getSimulatePaid() {
		return simulatePaid;
	}

	public void setSimulatePaid(int simulatePaid) {
		this.simulatePaid = simulatePaid;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getCheckMacValue() {
		return checkMacValue;
	}

	public void setCheckMacValue(String checkMacValue) {
		this.checkMacValue = checkMacValue;
	}

    
}

