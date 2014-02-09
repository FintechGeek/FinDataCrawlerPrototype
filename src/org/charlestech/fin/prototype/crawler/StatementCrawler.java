package org.charlestech.fin.prototype.crawler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 14-2-6.
 */
public class StatementCrawler {
    public static void crawlStatementNtes(String stockId) {
        try {
            URL balanceUrl = new URL("http://quotes.money.163.com/service/zcfzb_" + stockId + ".html");
            downloadStatement("balance", stockId, balanceUrl);
            URL incomeUrl = new URL("http://quotes.money.163.com/service/lrb_" + stockId + ".html");
            downloadStatement("income", stockId, incomeUrl);
            URL cashUrl = new URL("http://quotes.money.163.com/service/xjllb_" + stockId + ".html");
            downloadStatement("cash", stockId, cashUrl);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

    }

    private static void downloadStatement(String type, String stockId, URL url) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), "GBK"));
            File csvFile = new File("download/" + type + stockId + ".csv");
            if (!csvFile.exists()) {
                csvFile.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, false));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                writer.newLine();
                writer.write(new String(inputLine.getBytes("UTF-8")));
            }
            reader.close();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StatementCrawler.crawlStatementNtes("000998");
    }
}
