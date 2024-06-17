package com.example.demo.data.leave;

public class LeaveBO extends Leave{
    public LeaveBO(){
        super();
    }
    public static Leave convert(Leave leave){
        Leave leaveInfo = new LeaveBO();
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
}
