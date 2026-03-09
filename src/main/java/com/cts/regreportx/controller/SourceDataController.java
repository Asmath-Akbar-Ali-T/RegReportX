package com.cts.regreportx.controller;

import com.cts.regreportx.model.Deposit;
import com.cts.regreportx.model.GeneralLedger;
import com.cts.regreportx.model.Loan;
import com.cts.regreportx.model.TreasuryTrade;
import com.cts.regreportx.service.SourceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SourceDataController {

    private final SourceDataService sourceDataService;

    @Autowired
    public SourceDataController(SourceDataService sourceDataService) {
        this.sourceDataService = sourceDataService;
    }

    @GetMapping("/loans")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(sourceDataService.getAllLoans());
    }

    @GetMapping("/deposits")
    public ResponseEntity<List<Deposit>> getAllDeposits() {
        return ResponseEntity.ok(sourceDataService.getAllDeposits());
    }

    @GetMapping("/treasury")
    public ResponseEntity<List<TreasuryTrade>> getAllTreasuryTrades() {
        return ResponseEntity.ok(sourceDataService.getAllTreasuryTrades());
    }

    @GetMapping("/gl")
    public ResponseEntity<List<GeneralLedger>> getAllGeneralLedgers() {
        return ResponseEntity.ok(sourceDataService.getAllGeneralLedgers());
    }
}
