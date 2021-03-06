package site.binghai.lib.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PayBizEnum {
    EXP_TAKE(0,"代取快递"),
    EXP_SEND(1,"代寄快递"),
    EXP_CHARGE(2,"寄件费用"),
    COMMON_PAY(3,"通用支付单"),
    VIP_CHARGE(4,"VIP充值"),
    ;

    private int code;
    private String name;
    private static Map<Integer, PayBizEnum> maps;

    static {
        maps = new HashMap<>();
        for (PayBizEnum f : PayBizEnum.values()) {
            maps.put(f.code, f);
        }
    }

    public static PayBizEnum valueOf(int code) {
        return maps.get(code);
    }

    PayBizEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
