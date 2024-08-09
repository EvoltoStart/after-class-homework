package com.wx.springbootmysql.entiy;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName("Commdity")
public class Commdity {
    @TableId
    private String cid;
    private String cname;
    private int price;
    private int number;
    private String ssid;

    public Commdity(String cid, String cname, int price, int number) {
        this.cid = cid;
        this.cname = cname;
        this.price = price;
        this.number = number;
    }
    public Commdity(){}
}
