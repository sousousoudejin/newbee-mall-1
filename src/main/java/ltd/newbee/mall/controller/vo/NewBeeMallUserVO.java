package ltd.newbee.mall.controller.vo;

import lombok.Data;

@Data
public class NewBeeMallUserVO {

    private Long userId;

    private String nickName;

    private String loginName;

    private String introduceSign;

    private String address;

    private int shopCartItemCount;

}
