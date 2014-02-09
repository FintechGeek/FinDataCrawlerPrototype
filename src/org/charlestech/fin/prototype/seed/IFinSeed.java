package org.charlestech.fin.prototype.seed;

import java.util.List;

/**
 * Created by Administrator on 14-2-7.
 */
public interface IFinSeed {
    public List<String> getColumnByDesc(String description);

    public String getColumnByDescAndCategory(String desc, String category);

    public String getDescByColumn(String columnName);

    public String getCategoryByColumn(String columnName);

    public String getSubCategoryByColumn(String columnName);

    public String getSource();
}
