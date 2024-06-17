package com.example.demo.webservice.repository;

import com.example.demo.data.leave.Leave;
import com.example.demo.data.leave.LeaveDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LeaveRepository {// need to change with code

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public LeaveRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    //查询所有请假信息
    public List<Leave> getAllLeaveInfo(){
        String sql = "select * from `leave` order by appId";
        List<Leave> leaveList = jdbcTemplate.query(sql, new LeaveDO());
        return leaveList;
    }

    //根据empID查询个人请假信息
    public List<Leave> getLeaveInfo(Long empId, Long appID){
        String sql = "select * from `leave` where empId=ifnull(?,empId) and appId=ifnull(?,appId)";
        List<Leave> leaveList = jdbcTemplate.query(sql, new LeaveDO(), empId, appID);
        return leaveList;
    }
    public List<Leave> getLeaveInfoByEmpID(long empID){
        String sql = "SELECT * FROM `leave` WHERE empId=?";
        List<Leave> leaveInfo = jdbcTemplate.query(sql, new LeaveDO(), empID);
        return leaveInfo;
    }
    public List<Leave> getLeaveInfoByDeptID(long deptID){
        String sql = "SELECT * FROM `leave` WHERE empDeptId=?";
        List<Leave> leaveInfo = jdbcTemplate.query(sql, new LeaveDO(), deptID);
        return leaveInfo;
    }
    public Leave getLeaveInfoByAppID(long appID){
        String sql = "SELECT * FROM `leave` WHERE appId=?";
        Leave leaveInfo = jdbcTemplate.queryForObject(sql, new LeaveDO(), appID);
        return leaveInfo;
    }
    //新增请假信息
    public Leave addLeaveInfo(Leave leave){
        String sql = "INSERT INTO `leave` (appId,empId,empName,empDeptId,empDeptName,reason,leaveType,state,startTime,endTime) VALUES(?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        jdbcTemplate.update(sql, leave.getAppID(),leave.getEmpID(),leave.getEmpName(),leave.getEmpDeptID(),leave.getEmpDeptName(),
                leave.getReason(),leave.getType(),leave.getState(),leave.getStartTime(),leave.getEndTime());
        return getLeaveInfoByAppID(leave.getAppID());
    }

    //修改某条请假信息
    public Leave editLeaveInfo(long appID, Leave leave){
        String sql = "UPDATE `leave` set empName =?,empDeptId =? ,empDeptName = ?,reason = ?,leaveType = ?,state = ?, startTime=?, endTime = ? WHERE appId=?";
        jdbcTemplate.update(sql, leave.getEmpName(), leave.getEmpDeptID(),leave.getEmpDeptName(),
                leave.getReason(),leave.getType(),leave.getState(),leave.getStartTime(),leave.getEndTime(), appID);
        return getLeaveInfoByAppID(appID);
    }
    //删除请假信息
    public ResponseEntity deleteLeaveInfo(long appID){
        String sql = "DELETE FROM `leave` WHERE appId=?";
        jdbcTemplate.update(sql, appID);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    //删除某员工的请假信息
    public void deleteLeaveInfoByEmpID(long empID){
        String sql = "DELETE FROM `leave` WHERE empID=?";
        jdbcTemplate.update(sql, empID);
    }


    //查询某条请假信息,以map返回
    public Map<String, Object> QueryLeaveInfoByAppID(long appID){
        String sql = "SELECT * FROM `leave` WHERE appId=?";
        Map<String, Object> leaveMap = jdbcTemplate.queryForMap(sql, appID);
        return leaveMap;
    }
    //更新请假申请状态
    public void updateLeaveState(int state, long appID){
        String sql = "update `leave` set state = ? where appId = ?";
        jdbcTemplate.update(sql,state, appID);
    }
}
