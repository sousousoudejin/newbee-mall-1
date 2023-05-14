package ltd.newbee.mall.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -3991667753835427072L;

    private int resultCode;

    private String message;

    private T data;
}
