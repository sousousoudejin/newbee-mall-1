package ltd.newbee.mall.controller.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NewBeeMallOrderListVO {

    private Long orderId;

    private String orderNo;

    private Integer totalPrice;

    private Byte payType;

    private Byte orderStatus;

    private String orderStatusString;

    private String userAddress;

    private Date createTime;

    private List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS;

}
