package com.example.demo.data.attendance;

public class AttendanceVO extends Attendance
{
    public AttendanceVO()
    {
        super();
    }
    public static Attendance convert(Attendance attendance){
        Attendance attendanceInfo = new AttendanceVO();
        attendanceInfo.setEmpID(attendance.getEmpID());
        attendanceInfo.setAttendanceID(attendance.getAttendanceID());
        attendanceInfo.setClockTime(attendance.getClockTime());
        return attendanceInfo;
    }
    public static Object[] AttendanceList(Attendance attendance){
        Object[] attendanceList = {attendance.getAttendanceID(),
                attendance.getClockTime(), attendance.getEmpID()};
        return attendanceList;
    }
}
