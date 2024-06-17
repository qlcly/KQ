package com.example.demo.data.attendance;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.util.Map;



public class Attendance
{
    private Long attendanceID;
    private Long empID;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date clockTime;
    protected Attendance()
    {

    }

    public Date getClockTime()
    {
        return clockTime;
    }

    public Long getAttendanceID()
    {
        return attendanceID;
    }

    public Long getEmpID()
    {
        return empID;
    }

    public void setAttendanceID(Long attendanceID)
    {
        this.attendanceID = attendanceID;
    }

    public void setClockTime(Date clockTime)
    {
        this.clockTime = clockTime;
    }

    public void setEmpID(Long empID)
    {
        this.empID = empID;
    }
    public Attendance MapConvert(Map<String, Object> attendanceMap){
        Attendance attendance = JSON.parseObject(JSON.toJSONString(attendanceMap), Attendance.class);
        return attendance;
    }
}
