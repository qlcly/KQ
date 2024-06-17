package com.example.demo.data.holidays;

import com.alibaba.fastjson.JSON;

import java.util.Map;

public class Holidays {
    private Long empID;
    private Integer yearHolidays;
    private Integer privateHolidays;
    private Integer sickHolidays;
    public Long getEmpID(){
        return empID;
    }
    public Integer getYearHolidays(){
        return yearHolidays;
    }
    public Integer getPrivateHolidays(){
        return privateHolidays;
    }
    public Integer getSickHolidays() {
        return sickHolidays;
    }
    public void setEmpID(Long empID){
        this.empID = empID;
    }
    public void setYearHolidays(Integer yearHolidays) {
        this.yearHolidays = yearHolidays;
    }
    public void setPrivateHolidays(Integer privateHolidays) {
        this.privateHolidays = privateHolidays;
    }
    public void setSickHolidays(Integer sickHolidays) {
        this.sickHolidays = sickHolidays;
    }

    @Override
    public String toString(){
        StringBuffer str = new StringBuffer();
        str.append("empID " + empID + " ");
        str.append("yearHolidays " + yearHolidays + " ");
        str.append("privateHolidays " + privateHolidays + " ");
        str.append("sickHolidays " + sickHolidays + " ");
        return str.toString();
    }
    public static Holidays MapConvert(String holidaysString){
        Map<String, Object> map = (Map<String, Object>)JSON.parse(holidaysString);
        Holidays holidays = JSON.parseObject(map.get("Holidays").toString(), Holidays.class);
        return holidays;
    }
}
