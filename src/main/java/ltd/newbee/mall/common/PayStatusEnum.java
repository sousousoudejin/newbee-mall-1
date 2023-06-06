package ltd.newbee.mall.common;

import lombok.Data;


public enum PayStatusEnum {

    DEFAULT(-1, "支付失败"),
    PAY_ING(0, "支付中"),
    PAY_SUCCESS(1, "支付成功");

    private int payStatus;

    private String name;

    PayStatusEnum(int payStatus, String name) {
        this.payStatus = payStatus;
        this.name = name;
    }

    public static PayStatusEnum getPayStatusEnumByStatus(int payStatus) {
        for (PayStatusEnum value : PayStatusEnum.values()) {
            if (payStatus == value.getPayStatus())
                return value;
        }
        return DEFAULT;
    }

    public int getPayStatus() {
        return this.payStatus;
    }

    public String getName() {
        return this.name;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public void setName(String name) {
        this.name = name;
    }
}
