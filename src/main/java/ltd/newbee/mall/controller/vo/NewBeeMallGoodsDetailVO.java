package ltd.newbee.mall.controller.vo;

import lombok.Data;

@Data
public class NewBeeMallGoodsDetailVO {

    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoverImg;

    private String[] goodsCarouselList;

    private Integer sellingPrice;

    private Integer originalPrice;

    private String goodsDetailContent;

}
