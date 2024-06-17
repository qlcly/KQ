package com.example.demo.webservice.service.holidays;

import com.example.demo.data.holidays.Holidays;
import com.example.demo.data.holidays.HolidaysBO;
import com.example.demo.data.holidays.HolidaysDO;
import com.example.demo.webservice.repository.HolidaysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HolidaysServiceImpl implements HolidaysService{
    private HolidaysRepository holidaysRepository;
    @Autowired
    public HolidaysServiceImpl(HolidaysRepository holidaysRepository){
        this.holidaysRepository = holidaysRepository;
    }
    @Override
    public List<Holidays> getAllHolidaysInfo(){
        return holidaysRepository.getAllHolidaysInfo();
    }
    @Override
    public Holidays getHolidaysInfoByEmpID(long empID){
        return holidaysRepository.getHolidaysInfoByID(empID);
    }
    @Override
    public Holidays editRestHolidaysInfo(Holidays holidaysVO){
        long empID = holidaysVO.getEmpID();
        Map<String, Object> holidaysMap = holidaysRepository.queryHolidaysInfoByID(empID);
        Holidays holidaysBO = new HolidaysBO();
        //填补
        BOFilled(holidaysBO, holidaysMap);
        //更新
        BOUpdate(holidaysBO, holidaysVO);
        return holidaysRepository.editRestHolidaysInfo(empID,holidaysBO);
    }
    @Override
    public Holidays addHolidaysInfo(Holidays holidaysVO){
        Holidays holidays = HolidaysBO.convert(holidaysVO);
        return holidaysRepository.addHolidaysInfo(holidays);
    }
    @Override
    public ResponseEntity deleteHolidaysInfo(long empID){//删除就不返回数据
        return holidaysRepository.deleteHolidaysInfo(empID);
    }

    public void BOFilled(Holidays holidaysBO, Map<String, Object> holidaysInfo){
        for(Map.Entry<String, Object> entry: holidaysInfo.entrySet()){
            switch (entry.getKey()){
                case "empId":
                    holidaysBO.setEmpID((Long)entry.getValue());
                    break;
                case "yearHoliday":
                    holidaysBO.setYearHolidays((Integer) entry.getValue());
                    break;
                case "privateHoliday":
                    holidaysBO.setPrivateHolidays((Integer) entry.getValue());
                    break;
                case "sickHoliday":
                    holidaysBO.setSickHolidays((Integer) entry.getValue());
            }
        }
    }
    public void BOUpdate(Holidays holidaysBO, Holidays holidaysVO){
        if(holidaysVO.getYearHolidays() != null){
            holidaysBO.setYearHolidays(holidaysVO.getYearHolidays());
        }
        if(holidaysVO.getPrivateHolidays() != null){
            holidaysBO.setPrivateHolidays(holidaysVO.getPrivateHolidays());
        }
        if(holidaysVO.getSickHolidays() != null){
            holidaysBO.setSickHolidays(holidaysVO.getSickHolidays());
        }
    }

    public boolean sickHolidaysEnough(long empID, int days){
        Holidays holidaysInfo = holidaysRepository.getHolidaysInfoByID(empID);
        return days <= holidaysInfo.getSickHolidays();
    }

}
