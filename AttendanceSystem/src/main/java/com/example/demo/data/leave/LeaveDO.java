package com.example.demo.data.leave;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaveDO extends Leave implements RowMapper<Leave>{
    public LeaveDO(){
        super();
    }
    public static Leave convert(Leave leave){
        Leave leaveInfo = new LeaveDO();
        leaveInfo.setState(leave.getState());
        leaveInfo.setType(leave.getType());
        leaveInfo.setReason(leave.getReason());
        leaveInfo.setEmpDeptName(leave.getEmpDeptName());
        leaveInfo.setEmpName(leave.getEmpName());
        leaveInfo.setAppID(leave.getAppID());
        leaveInfo.setEmpID(leave.getEmpID());
        leaveInfo.setEmpDeptID(leave.getEmpDeptID());
        leaveInfo.setStartTime(leave.getStartTime());
        leaveInfo.setEndTime(leave.getEndTime());
        return leaveInfo;
    }
    @Override
    public Leave mapRow(ResultSet rs,int i) throws SQLException {
        Leave leaveDo = new LeaveDO();

        leaveDo.setAppID(rs.getLong("appId"));
        leaveDo.setEmpID(rs.getLong("empId"));
        leaveDo.setEmpName(rs.getString("empName"));
        leaveDo.setEmpDeptID(rs.getLong("empDeptId"));
        leaveDo.setEmpDeptName(rs.getString("empDeptName"));
        leaveDo.setReason(rs.getString("reason"));
        leaveDo.setType(rs.getInt("leaveType"));// does it need to change into integer
        leaveDo.setState(rs.getInt("state"));
        leaveDo.setStartTime(rs.getDate("startTime"));
        leaveDo.setEndTime(rs.getDate("endTime"));
        return leaveDo;
    }
}
