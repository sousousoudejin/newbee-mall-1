package ltd.newbee.mall.controller.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewBeeMallIndexCategoryVO {

    private Long categoryId;

    private Byte categoryLevel;

    private String categoryName;

    private List<SecondLevelCategoryVO> secondLevelCategoryVOS;

}
