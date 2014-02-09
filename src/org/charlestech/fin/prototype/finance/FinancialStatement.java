package org.charlestech.fin.prototype.finance;

import org.charlestech.fin.prototype.common.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 14-2-6.
 */
public class FinancialStatement {
    private String stockId;
    private Timestamp releaseTime;
    private String source;
    private String category;
    private Map<String, Double> dataMap;

    public FinancialStatement() {
        super();
    }

    public FinancialStatement(String stockId, Timestamp releaseTime,
                              String source, String category) {
        super();
        this.stockId = stockId;
        this.releaseTime = releaseTime;
        this.source = source;
        this.category = category;
        this.dataMap  = new HashMap<String, Double>();
    }

    public int insertStatement() {
        int num = 0;
        if (!isExisting() && null != dataMap) {
            StringBuffer sbColumn = new StringBuffer();
            StringBuffer sbValue = new StringBuffer();
            sbColumn.append("insert into financial_statement ( stock_id, release_time, source, category");
            sbValue.append("values( ?, ?, ?, ?");
            Set<Map.Entry<String, Double>> entrySet = this.dataMap.entrySet();
            for (Map.Entry<String, Double> entry : entrySet) {
                sbColumn.append(", " + entry.getKey());
                sbValue.append(", " + entry.getValue().toString());
            }
            sbColumn.append(")");
            sbValue.append(")");
            String sql = (sbColumn.append(sbValue)).toString();
            try {
                Connection conn = ConnectionPool.getPoolInstance().getConnection();
                conn.setAutoCommit(false);
                PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.setString(1, this.stockId);
                pStmt.setTimestamp(2, this.releaseTime);
                pStmt.setString(3, this.source);
                pStmt.setString(4, this.category);
                num = pStmt.executeUpdate();
                conn.commit();
                ConnectionPool.getPoolInstance().releaseConnection(conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }


        }
        return num;
    }

    private boolean isExisting() {
        boolean existed = false;
        if (null != this.stockId && null != this.releaseTime && null != this.source) {
            String sql = "SELECT fs_id FROM financial_statement WHERE stock_id=? AND release_time=? AND source=?";
            try {
                Connection conn = ConnectionPool.getPoolInstance().getConnection();
                PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.setString(1, this.stockId);
                pStmt.setTimestamp(2, this.releaseTime);
                pStmt.setString(3, this.source);
                ResultSet rs = pStmt.executeQuery();
                if (rs.next()) {
                    existed = true;
                }
                ConnectionPool.getPoolInstance().releaseConnection(conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return existed;
    }


    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Timestamp releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Double> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Double> dataMap) {
        this.dataMap = dataMap;
    }
}
