package com.wx.firstweb;

import java.util.HashMap;
import java.util.Map;

public class ShopCar {
    private Map<String, Integer> map = new HashMap();
    private Map<String, Integer> car = new HashMap();

    public ShopCar() {
        this.map.put("苹果", 5);
        this.map.put("香蕉", 10);
        this.map.put("橘子", 15);
    }

    public boolean add(String name, int num) {
        if (!this.map.containsKey(name)) {
            return false;
        } else {
            this.map.put(name, (Integer)this.map.get(name) - num);
            if (this.car.containsKey(name)) {
                Integer n = (Integer)this.car.get(name) + num;
                this.car.put(name, n);
            } else {
                this.car.put(name, num);
            }

            return true;
        }
    }

    public Map<String, Integer> getCarNum() {
        return this.car;
    }

    public Map<String, Integer> getMap() {
        return this.map;
    }

    public Integer getNumber(String name) {
        return (Integer)this.map.get(name);
    }
}
