import java.util.HashMap;
import java.util.Map;

public class ShopCar {
    private Map<String,Integer> map;

    public ShopCar() {

        this.map =new HashMap<>();
        this.map.put("苹果", 5);
        this.map.put("香蕉", 10);
        this.map.put("橙子", 15);

    }
    public boolean add(String name,int num) {
        if(!map.containsKey(name)) {
            return false;
        }else
        {
            map.put(name,map.get(name)-num);
            return true;
        }
    }
    public Map<String,Integer> getMap() {
        return this.map;
    }
    public Integer getNumber(String name) {
        return this.map.get(name);
    }
}
