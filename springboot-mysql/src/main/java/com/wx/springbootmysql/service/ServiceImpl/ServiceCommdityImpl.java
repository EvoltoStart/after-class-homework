package com.wx.springbootmysql.service.ServiceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.springbootmysql.entiy.Commdity;
import com.wx.springbootmysql.mapper.CommdityMapper;
import com.wx.springbootmysql.service.ServiceCommdity;
import org.springframework.stereotype.Service;

@Service
public class ServiceCommdityImpl extends ServiceImpl<CommdityMapper,Commdity> implements ServiceCommdity {
}
