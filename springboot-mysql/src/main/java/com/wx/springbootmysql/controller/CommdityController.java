package com.wx.springbootmysql.controller;

import com.wx.springbootmysql.entiy.Commdity;
import com.wx.springbootmysql.service.ServiceCommdity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commodity")
public class CommdityController {
    @Autowired
    private ServiceCommdity serviceCommdity;
    @GetMapping("list")
    public Object list(){
        return serviceCommdity.list();
    }
    @GetMapping("get/{cid}")
    public Object get(@PathVariable String cid){
        return serviceCommdity.getById(cid);
    }
    @GetMapping("{cid}/{cname}/{price}/{number}")
    public String add(@PathVariable String cid, @PathVariable String cname, @PathVariable int price, @PathVariable int number){
        Commdity commdity=new Commdity(cid,cname,price,number);
        boolean succ=serviceCommdity.save(commdity);
        if(succ){
            return "添加成功";
        }else return "添加失败";
    }
    @GetMapping("delete/{cid}")
    public String delete(@PathVariable String cid){
        boolean s=serviceCommdity.removeById(cid);
        if(s){
            return "删除成功";
        }else return "删除失败";
    }
    @GetMapping("update/{cid}/{cname}/{price}/{number}")
    public String update(@PathVariable String cid,@PathVariable String cname,@PathVariable int price,@PathVariable int number){
        Commdity commdity=serviceCommdity.getById(cid);
        commdity.setCname(cname);
        commdity.setPrice(price);
        commdity.setNumber(number);
        boolean s=serviceCommdity.updateById(commdity);
        if(s){
            return "修改成功";
        }else return "修改失败";
    }


}
