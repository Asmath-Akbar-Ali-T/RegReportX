package com.cts.regreportx.component;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class DynamicRiskEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();

    public BigDecimal evaluateFormula(String formula, Map<String, BigDecimal> context) {
        try {
            // Build a clean SpEL expression string by replacing variables with actual numbers
            String spelFormula = formula;

            // Pre-process exact SQL-like strings from the DB into their actual values
            spelFormula = spelFormula.replace("SUM(loanAmount) FROM Loan", context.getOrDefault("Total_Loans", BigDecimal.ZERO).toPlainString());
            spelFormula = spelFormula.replace("SUM(amount) FROM Deposit", context.getOrDefault("Total_Deposits", BigDecimal.ZERO).toPlainString());
            spelFormula = spelFormula.replace("SUM(credit) - SUM(debit) FROM GeneralLedger", context.getOrDefault("Net_GL_Balance", BigDecimal.ZERO).toPlainString());
            spelFormula = spelFormula.replace("SUM(loanAmount * RiskWeight) FROM Loan", context.getOrDefault("RWA", BigDecimal.ZERO).toPlainString());
            spelFormula = spelFormula.replace("SUM(notional) FROM TreasuryTrade", context.getOrDefault("Treasury_Exposure", BigDecimal.ZERO).toPlainString());

            // IRS and derived report string formats
            spelFormula = spelFormula.replace("SUM(loanAmount)", context.getOrDefault("Total_Loans", BigDecimal.ZERO).toPlainString());
            spelFormula = spelFormula.replace("SUM(treasuryTradeNotional)", context.getOrDefault("Treasury_Exposure", BigDecimal.ZERO).toPlainString());
            spelFormula = spelFormula.replace("SUM(depositAmount)", context.getOrDefault("Total_Deposits", BigDecimal.ZERO).toPlainString());

            // Handle Absolute value syntax e.g. |Net_GL_Balance|
            if (spelFormula.contains("|Net_GL_Balance|")) {
                BigDecimal val = context.getOrDefault("Net_GL_Balance", BigDecimal.ZERO).abs();
                spelFormula = spelFormula.replace("|Net_GL_Balance|", val.toPlainString());
            }

            // Replace MAX function syntax
            if (spelFormula.contains("MAX(Customer_Total_Load)")) {
                BigDecimal val = context.getOrDefault("MAX(Customer_Total_Load)", BigDecimal.ZERO);
                spelFormula = spelFormula.replace("MAX(Customer_Total_Load)", val.toPlainString());
            }

            // Replace context variables
            for (Map.Entry<String, BigDecimal> entry : context.entrySet()) {
                if (spelFormula.contains(entry.getKey())) {
                    spelFormula = spelFormula.replace(entry.getKey(), entry.getValue().toPlainString());
                }
            }
            
            // To ensure big decimal conversion in divisions (SpEL truncates ints), suffix numbers with .0
            // Although variable replacement drops them as plain string decimals, hardcoded like * 100 needs parsing to double
            // SpEL allows floating point evaluation and converting back to BigDecimal dynamically.

            StandardEvaluationContext evalContext = new StandardEvaluationContext();
            Expression expression = parser.parseExpression(spelFormula);
            
            Number result = expression.getValue(evalContext, Number.class);
            if (result != null) {
                return new BigDecimal(result.toString()).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Failed to parse dynamic formula: " + formula + " -> Error: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
