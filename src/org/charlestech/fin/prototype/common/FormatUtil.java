package org.charlestech.fin.prototype.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 14-2-8.
 */
public class FormatUtil {
    public static Timestamp formatReleaseTime(String releaseTime) {
        //TODO: centralize the format pattern
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = formatter.parse(releaseTime);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return new Timestamp(date.getTime());
    }

    public static Double formatAmount(String amount) {
        if (amount.matches("\\d+\\.\\d+") || amount.matches("\\d+"))
            return Double.parseDouble(amount);
        else
            return Double.parseDouble("0.0");
    }
}
