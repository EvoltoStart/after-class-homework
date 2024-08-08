package car;

import java.util.HashMap;
import java.util.Map;

public class ShopCar {
    private Map<String,Integer> map;
    private Map<String,Integer> car;

    public ShopCar() {

        this.map =new HashMap<>();
        this.car=new HashMap<>();
        this.map.put("苹果", 5);
        this.map.put("香蕉", 10);
        this.map.put("橘子", 15);

    }
    public boolean add(String name,int num) {
        if(!map.containsKey(name)) {
            return false;
        }else
        {
            map.put(name,map.get(name)-num);
            if(car.containsKey(name)){
                Integer n=car.get(name)+num;
                car.put(name,n);
            }else car.put(name,num);

            return true;
        }
    }
    public Map<String, Integer> getCarNum(){
        return this.car;
    }
    public Map<String,Integer> getMap() {
        return this.map;
    }
    public Integer getNumber(String name) {
        return this.map.get(name);
    }
}
