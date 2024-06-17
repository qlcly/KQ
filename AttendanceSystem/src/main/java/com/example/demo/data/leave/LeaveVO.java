package com.example.demo.data.leave;

public class LeaveVO extends Leave{
    public LeaveVO(){
        super();
    }
    public static Leave convert(Leave leave){
        Leave leaveInfo = new LeaveVO();
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
    public static Object[] LeaveList(Leave leave){
        Object[] leaveList = {leave.getState(), leave.getType(), leave.getReason(),
                leave.getEmpDeptName(), leave.getEmpName(),
                leave.getAppID(), leave.getEmpID(), leave.getEmpDeptID(),
                leave.getStartTime(), leave.getEndTime()};
        return leaveList;
    }
}
