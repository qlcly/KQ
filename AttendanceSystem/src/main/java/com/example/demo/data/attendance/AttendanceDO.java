package com.example.demo.data.attendance;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendanceDO extends Attendance implements RowMapper<Attendance>
{
    public AttendanceDO()
    {
        super();
    }
    public static Attendance convert(Attendance attendance){
        Attendance attendanceInfo = new AttendanceDO();
        attendanceInfo.setEmpID(attendance.getEmpID());
        attendanceInfo.setAttendanceID(attendance.getAttendanceID());
        attendanceInfo.setClockTime(attendance.getClockTime());
        return attendanceInfo;
    }
    @Override
    public Attendance mapRow(ResultSet rs, int i) throws SQLException {
        Attendance attendanceDo = new AttendanceDO();

        attendanceDo.setAttendanceID(rs.getLong("attendanceId"));
        attendanceDo.setEmpID(rs.getLong("empId"));
        attendanceDo.setClockTime(rs.getDate("clockTime"));


        return attendanceDo;
    }
}
