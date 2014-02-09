package org.charlestech.fin.prototype.seed;

import org.charlestech.fin.prototype.common.ConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-2-7.
 */
public abstract class FinSeedImpl implements IFinSeed {

    private List<FinMetaSeed> finMetaSeedList;

    private String seedTable;

    public FinSeedImpl() {
        super();
    }

    FinSeedImpl(String seedTable) {
        this.seedTable = seedTable;
        ConnectionPool pool = ConnectionPool.getPoolInstance();
        String sql = "select seed_id, table_name, column_name, category, sub_category, description from " + seedTable;
        finMetaSeedList = new ArrayList<FinMetaSeed>();
        try {
            Connection conn = pool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                FinMetaSeed finMetaSeed = new FinMetaSeed();
                String tableName = rs.getString("table_name");
                String columnName = rs.getString("column_name");
                String category = rs.getString("category");
                String subCategory = rs.getString("sub_category");
                String description = rs.getString("description");
                finMetaSeed.setTableName(tableName);
                finMetaSeed.setColumnName(columnName);
                finMetaSeed.setCategory(category);
                finMetaSeed.setSubCategory(subCategory);
                finMetaSeed.setDescription(description);
                finMetaSeedList.add(finMetaSeed);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public List<String> getColumnByDesc(String description) {
        List<String> columns = new ArrayList<String>();

        if (null == this.getFinMetaSeedList() || null == description) {
            return columns;
        }

        for (FinMetaSeed finMetaSeed : this.getFinMetaSeedList()) {
            if (description.equalsIgnoreCase(finMetaSeed.getDescription())) {
                columns.add(finMetaSeed.getColumnName());
            }
        }

        return columns;
    }

    @Override
    public String getColumnByDescAndCategory(String description, String category) {
        String column = null;

        if (null == this.getFinMetaSeedList() || null == description || null == category) {
            return column;
        }

        for (FinMetaSeed finMetaSeed : this.getFinMetaSeedList()) {
            if (description.equalsIgnoreCase(finMetaSeed.getDescription())) {
                column = finMetaSeed.getColumnName();
                break;
            }
        }

        return column;
    }

    @Override
    public String getCategoryByColumn(String columnName) {
        String category = null;

        if (null == this.getFinMetaSeedList() || null == columnName) {
            return category;
        }

        for (FinMetaSeed finMetaSeed : this.getFinMetaSeedList()) {
            if (columnName.equalsIgnoreCase(finMetaSeed.getColumnName())) {
                category = finMetaSeed.getCategory();
                break;
            }
        }

        return category;
    }

    @Override
    public String getDescByColumn(String columnName) {
        String description = null;

        if (null == this.getFinMetaSeedList() || null == columnName) {
            return description;
        }

        for (FinMetaSeed finMetaSeed : this.getFinMetaSeedList()) {
            if (columnName.equalsIgnoreCase(finMetaSeed.getColumnName())) {
                description = finMetaSeed.getDescription();
                break;
            }
        }

        return description;
    }

    @Override
    public String getSubCategoryByColumn(String columnName) {
        String subCategory = null;

        if (null == this.getFinMetaSeedList() || null == columnName) {
            return subCategory;
        }

        for (FinMetaSeed finMetaSeed : this.getFinMetaSeedList()) {
            if (columnName.equalsIgnoreCase(finMetaSeed.getColumnName())) {
                subCategory = finMetaSeed.getSubCategory();
                break;
            }
        }

        return subCategory;
    }

    @Override
    public abstract String getSource();

    public List<FinMetaSeed> getFinMetaSeedList() {
        return finMetaSeedList;
    }

    public void setFinMetaSeedList(List<FinMetaSeed> finMetaSeedList) {
        this.finMetaSeedList = finMetaSeedList;
    }

    public String getSeedTable() {
        return seedTable;
    }

    public void setSeedTable(String seedTable) {
        this.seedTable = seedTable;
    }
}
