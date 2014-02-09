package org.charlestech.fin.prototype.seed;

/**
 * Created by Administrator on 14-2-7.
 */
public class FinMetaSeed {
    private String tableName;
    private String columnName;
    private String category;
    private String subCategory;
    private String description;

    public FinMetaSeed() {
        super();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Table Name: " + this.getTableName() + ",\t");
        sb.append("Column Name: " + this.getColumnName() + ",\t");
        sb.append("Category: " + this.getCategory() + ",\t");
        sb.append("Sub Category: " + this.getSubCategory() + ",\t");
        sb.append("Description: " + this.getDescription());
        return sb.toString();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
