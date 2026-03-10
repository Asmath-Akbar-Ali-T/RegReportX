package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "treasury")
public class TreasuryTrade {

    @Id
    @Column(name = "\"TradeID\"")
    private String tradeId;

    @Column(name = "\"Instrument\"")
    private String instrument;

    @Column(name = "\"Counterparty\"")
    private String counterparty;

    @Column(name = "\"Notional\"")
    private BigDecimal notional;

    @Column(name = "\"Currency\"")
    private String currency;

    @Column(name = "\"TradeDate\"")
    private LocalDate tradeDate;

    @Column(name = "\"MaturityDate\"")
    private LocalDate maturityDate;

    public TreasuryTrade() {
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public BigDecimal getNotional() {
        return notional;
    }

    public void setNotional(BigDecimal notional) {
        this.notional = notional;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }
}
