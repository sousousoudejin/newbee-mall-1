package ltd.newbee.mall.controller.vo;

import lombok.Data;
import ltd.newbee.mall.entity.GoodsCategory;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchPageCategoryVO implements Serializable {

    private static final long serialVersionUID = 7092965141626874640L;

    private String firstLevelCategoryName;

    private List<GoodsCategory> secondLevelCategoryList;

    private String secondLevelCategoryName;

    private List<GoodsCategory> thirdLevelCategoryList;

    private String currentCategoryName;
}
