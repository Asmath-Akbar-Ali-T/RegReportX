package com.cts.regreportx.controller;

import com.cts.regreportx.dto.DepositDto;
import com.cts.regreportx.dto.GeneralLedgerDto;
import com.cts.regreportx.dto.LoanDto;
import com.cts.regreportx.dto.TreasuryTradeDto;
import com.cts.regreportx.model.Deposit;
import com.cts.regreportx.model.GeneralLedger;
import com.cts.regreportx.model.Loan;
import com.cts.regreportx.model.TreasuryTrade;
import com.cts.regreportx.service.SourceDataService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SourceDataController {

    private final SourceDataService sourceDataService;
    private final ModelMapper modelMapper;

    @Autowired
    public SourceDataController(SourceDataService sourceDataService, ModelMapper modelMapper) {
        this.sourceDataService = sourceDataService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(sourceDataService.getAllLoans().stream()
                .map(l -> modelMapper.map(l, LoanDto.class)).collect(Collectors.toList()));
    }

    @GetMapping("/deposits")
    public ResponseEntity<List<DepositDto>> getAllDeposits() {
        return ResponseEntity.ok(sourceDataService.getAllDeposits().stream()
                .map(d -> modelMapper.map(d, DepositDto.class)).collect(Collectors.toList()));
    }

    @GetMapping("/treasury")
    public ResponseEntity<List<TreasuryTradeDto>> getAllTreasuryTrades() {
        return ResponseEntity.ok(sourceDataService.getAllTreasuryTrades().stream()
                .map(t -> modelMapper.map(t, TreasuryTradeDto.class)).collect(Collectors.toList()));
    }

    @GetMapping("/gl")
    public ResponseEntity<List<GeneralLedgerDto>> getAllGeneralLedgers() {
        return ResponseEntity.ok(sourceDataService.getAllGeneralLedgers().stream()
                .map(g -> modelMapper.map(g, GeneralLedgerDto.class)).collect(Collectors.toList()));
    }
}
