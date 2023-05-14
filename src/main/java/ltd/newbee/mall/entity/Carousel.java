package ltd.newbee.mall.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Carousel {

    private Integer carouselId;

    private String carouselUrl;

    private String redirectUrl;

    private Integer carouselRank;

    private Integer isDeleted;

    private Date createTime;

    private Integer createUser;

    private Date updateTime;

    private Integer updateUser;

}
