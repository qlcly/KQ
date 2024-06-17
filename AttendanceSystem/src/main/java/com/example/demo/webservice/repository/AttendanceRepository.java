package com.example.demo.webservice.repository;

import com.example.demo.data.attendance.Attendance;
import com.example.demo.data.attendance.AttendanceDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

@Repository
public class AttendanceRepository {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public AttendanceRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    //获取所有打卡记录
    public List<Attendance> getAllAttendance(){
        String sql = "SELECT * FROM `attendance` ORDER BY empId";
        List<Attendance> attendanceList = jdbcTemplate.query(sql, new AttendanceDO());
        return attendanceList;
    }
    //根据empID与attendanceID查询打卡记录，查询规则同approval
    public List<Attendance> getAttendanceByID(Long empID, Long attendanceID){
        String sql = "SELECT * FROM `attendance` WHERE empId=ifnull(?,empId) AND attendanceId=ifnull(?,attendanceId)";
        List<Attendance> attendanceList = jdbcTemplate.query(sql, new AttendanceDO(), empID,attendanceID);
        return attendanceList;
    }
//    导入打卡记录
    public ResponseEntity importAttendance(File file){
        String path = file.getPath();
        try {
            //读取指定的文件
            BufferedReader buf = new BufferedReader(new FileReader(file));
            String str=null;//定义一个字符串类型变量str
            String[] values = new String[3];
            String sql = "insert into attendance(attendanceId, empId, clockTime) values(?, ?, ?) ";
            while ((str = buf.readLine()) != null) {//readLine()方法, 用于读取一行,只要读取内容不为空就一直执行
                values = str.split(",");
                jdbcTemplate.update(sql,values[0], values[1], values[2]);
            }
        } catch (Exception e) {//当try代码块有异常时转到catch代码块
            e.printStackTrace();//printStackTrace()方法是打印异常信息在程序中出错的位置及原因
        }
        return new ResponseEntity( "success", HttpStatus.OK);
    }

    //删除打卡记录
    public ResponseEntity deleteAttendance(long attendanceID){
        String sql = "DELETE FROM `attendance` WHERE attendanceId=?";
        jdbcTemplate.update(sql, attendanceID);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    public void deleteAttendanceByEmpID(long empID){
        String sql = "DELETE FROM `attendance` WHERE empID=?";
        jdbcTemplate.update(sql, empID);
    }
}
