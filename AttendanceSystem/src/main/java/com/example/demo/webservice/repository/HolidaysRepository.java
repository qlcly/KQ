package com.example.demo.webservice.repository;

import com.example.demo.data.holidays.Holidays;
import com.example.demo.data.holidays.HolidaysDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HolidaysRepository {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public HolidaysRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    //查询所有员工假期信息
    public List<Holidays> getAllHolidaysInfo(){
        String sql = "SELECT * FROM `holidays` ORDER BY empId";
        List<Holidays> holidaysList = jdbcTemplate.query(sql, new HolidaysDO());
        return holidaysList;
    }
    //查询某一员工假期信息
    public Holidays getHolidaysInfoByID(long empID){
        String sql = "SELECT * FROM `holidays` WHERE empId=? ";
        Holidays holidays = jdbcTemplate.queryForObject(sql, new HolidaysDO(), empID);
        return holidays;
    }
    //修改员工假期信息
    public Holidays editRestHolidaysInfo(long empID,Holidays holidays){
        String sql = "UPDATE `holidays` set yearHoliday=? , privateHoliday=?, sickHoliday=? WHERE empId=?";
        jdbcTemplate.update(sql, holidays.getYearHolidays(), holidays.getPrivateHolidays(), holidays.getSickHolidays(), empID);
        return getHolidaysInfoByID(empID);
    }
    //添加员工信息
    public Holidays addHolidaysInfo(Holidays holidays){
        String sql = "INSERT INTO holidays (empId,yearHoliday,privateHoliday,sickHoliday) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, holidays.getEmpID(), holidays.getYearHolidays(),holidays.getPrivateHolidays(),holidays.getSickHolidays());
        String sql2 = "SELECT * FROM `holidays` WHERE empId=? ";
        Holidays holidays2 = jdbcTemplate.queryForObject(sql2, new HolidaysDO(), holidays.getEmpID());
        return holidays2;
    }
    //删除某一员工信息
    public ResponseEntity deleteHolidaysInfo(long empID){
        String sql = "DELETE FROM `holidays` WHERE empId=?";
        jdbcTemplate.update(sql, empID);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
    //查询某一员工信息以map返回
    public Map<String, Object> queryHolidaysInfoByID(long empID){
        String sql = "select * from `holidays` where empId = ?";
        Map<String, Object> holidaysMap = jdbcTemplate.queryForMap(sql, empID);
        return holidaysMap;
    }

}
