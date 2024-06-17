package com.example.demo.webservice.controller;

import com.example.demo.data.exception.BadRequestException;
import com.example.demo.data.holidays.Holidays;
import com.example.demo.data.holidays.HolidaysVO;
import com.example.demo.webconfig.RequestHolder;
import com.example.demo.webservice.service.holidays.HolidaysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/AttendanceSystem")
public class HolidaysController {
    private HolidaysService holidaysService;
    private Logger logger = LoggerFactory.getLogger(HolidaysController.class);
    @Autowired
    public HolidaysController(HolidaysService holidaysService) {
        this.holidaysService = holidaysService;
    }
    //获取所有员工假期信息
    @GetMapping("/holidays")
    public List<Holidays> getAllHolidaysInfo(){
        logger.info(RequestHolder.username.get() + " access holidays[GET]");
        return holidaysService.getAllHolidaysInfo();
    }
    //获取某一员工假期信息
    @GetMapping("/holidays/id")
    public Holidays getHolidaysInfo(@NonNull @RequestParam(value = "empID") long empID){
        BadRequestException.checkHasNull(empID);
        logger.info(RequestHolder.username.get() + " access holidays/id[GET]");
        return holidaysService.getHolidaysInfoByEmpID(empID);
    }
    //更新某员工假期信息
    @PutMapping("/holidays")
    public Holidays editRestHolidaysInfo(@RequestBody String holidaysString){
        Holidays holidaysInfo = Holidays.MapConvert(holidaysString);
        BadRequestException.checkHasNull(holidaysInfo.getEmpID());
        logger.info(RequestHolder.username.get() + " access holidays[PUT]");
        return holidaysService.editRestHolidaysInfo(HolidaysVO.convert(holidaysInfo));
    }
    //不可独立添加，添加员工信息时自动添加
//    //添加某员工假期信息
//    @PostMapping(value = "/holidays")
//    public Holidays addHolidaysInfo(@RequestBody String holidaysString){
//        Holidays holidaysInfo = Holidays.MapConvert(holidaysString);
//        BadRequestException.checkHasNull(HolidaysVO.HolidaysList(holidaysInfo));
//        return holidaysService.addHolidaysInfo(HolidaysVO.convert(holidaysInfo));
//    }
    //删除同理
//    //删除某一员工假期信息
//    @DeleteMapping(value = "/holidays")
//    public ResponseEntity deleteHolidaysInfo(@NonNull @RequestParam long empID){
//        return holidaysService.deleteHolidaysInfo(empID);
//    }


}
