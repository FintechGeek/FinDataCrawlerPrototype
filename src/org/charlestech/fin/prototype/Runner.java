package org.charlestech.fin.prototype;

import org.charlestech.fin.prototype.common.CsvParser;
import org.charlestech.fin.prototype.common.FormatUtil;
import org.charlestech.fin.prototype.crawler.StatementCrawler;
import org.charlestech.fin.prototype.finance.FinancialStatement;
import org.charlestech.fin.prototype.seed.FinSeedNtesImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-2-7.
 */
public class Runner {

    private static List<String> stockIds;

    public static void main(String[] args) throws Exception {
        _step_1();
        _step_2();
        _step_3();

    }

    private static void _step_1() {
        CsvParser parser = new CsvParser("sample/test.csv", "UTF-8");
        stockIds = new ArrayList<String>();
        while (parser.hasNext()) {
            stockIds.add(parser.next().get(0));
        }
        parser.close();
    }

    private static void _step_2() {
        for (String stockId : stockIds) {
            StatementCrawler.crawlStatementNtes(stockId);
        }
    }

    private static void _step_3() {
        for (String stockId : stockIds) {
            CsvParser balanceParser = new CsvParser("download/balance" + stockId + ".csv", "UTF-8");
            CsvParser incomeParser = new CsvParser("download/income" + stockId + ".csv", "UTF-8");
            CsvParser cashParser = new CsvParser("download/cash" + stockId + ".csv", "UTF-8");
            List<FinancialStatement> financialStatements = new ArrayList<FinancialStatement>();
            int recordCount = 0;
            int rowSize = 0;
            while (balanceParser.hasNext() && incomeParser.hasNext() && cashParser.hasNext()) {
                List<String> balanceRow = balanceParser.next();
                List<String> incomeRow = incomeParser.next();
                List<String> cashRow = cashParser.next();
                int balanceRowSize = balanceRow.size();
                int incomeRowSize = incomeRow.size();
                int cashRowSize = cashRow.size();

                rowSize = (balanceRowSize > incomeRowSize ? incomeRowSize : balanceRowSize);
                rowSize = (rowSize > cashRowSize ? cashRowSize : rowSize);
                if (2 == balanceParser.getLineNumber() &&
                        2 == incomeParser.getLineNumber() &&
                        2 == cashParser.getLineNumber()) {
                    recordCount = rowSize - 2;
                    for (int i = 1; i <= recordCount; i++) {
                        Timestamp releaseTime = FormatUtil.formatReleaseTime(balanceRow.get(i));
                        financialStatements.add(new FinancialStatement(stockId, releaseTime, "NTES", "ASHARE"));
                    }
                } else {
                    if (rowSize == (recordCount + 2)) {
                        if (!balanceRow.get(0).isEmpty()) {
                            String column = FinSeedNtesImpl.getFinSeedInstance()
                                    .getColumnByDescAndCategory(balanceRow.get(0), "BALANCE");
                            for (int i = 0; i < recordCount; i++) {
                                System.out.println("!!!!!!!!!!!!!!!" + i + "++++++++++++" + column);
                                financialStatements.get(i).getDataMap().put(column, FormatUtil.formatAmount(balanceRow.get(i + 1)));
                            }
                        }

                        if (!incomeRow.get(0).isEmpty()) {
                            String column = FinSeedNtesImpl.getFinSeedInstance()
                                    .getColumnByDescAndCategory(incomeRow.get(0), "INCOME");
                            for (int i = 0; i < recordCount; i++) {
                                financialStatements.get(i).getDataMap().put(column, FormatUtil.formatAmount(incomeRow.get(i + 1)));
                            }
                        }

                        if (!cashRow.get(0).isEmpty()) {
                            String column = FinSeedNtesImpl.getFinSeedInstance()
                                    .getColumnByDescAndCategory(cashRow.get(0), "CASH");
                            for (int i = 0; i < recordCount; i++) {
                                financialStatements.get(i).getDataMap().put(column, FormatUtil.formatAmount(cashRow.get(i + 1)));
                            }
                        }
                    } else {
                        System.out.println("Step 3 Error:" + stockId + " !!!!!!!!!!!");
                        break;
                    }
                }
            }

            for (FinancialStatement financialStatement : financialStatements) {
                financialStatement.insertStatement();
            }
            balanceParser.close();
            incomeParser.close();
            cashParser.close();
        }
    }
}
