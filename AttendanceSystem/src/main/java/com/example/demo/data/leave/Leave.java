package com.example.demo.data.leave;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Leave {
    private Long appID;
    private Long empID;
    private String empName;
    private Long empDeptID;
    private String empDeptName;
    private String reason;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    private Integer type;
    private Integer state;
    public static final int YEAR_LEAVE = 1;
    public static final int PRIVATE_LEAVE = 2;
    public static final int SICK_LEAVE = 3;
    public static final int BUSINESS_LEAVE = 4;
    public static final int OTHERS = 5;     //孕假等不扣新的事假
    private final List<Integer> availableType = Arrays.asList(
            YEAR_LEAVE, PRIVATE_LEAVE, SICK_LEAVE, BUSINESS_LEAVE, OTHERS);
    public static final int REFUSE = 0;
    public static final int UNDETERMINED = 1;
    public static final int DEPARTMENT_MANAGER_APPROVAL = 2;
    public static final int GENERAL_MANAGER_APPROVAL = 3;
    private final List<Integer> availableState = Arrays.asList(REFUSE, UNDETERMINED,
            DEPARTMENT_MANAGER_APPROVAL, GENERAL_MANAGER_APPROVAL);
    public Long getAppID() {
        return appID;
    }
    public Long getEmpID() {
        return empID;
    }
    public String getEmpName(){
        return empName;
    }
    public Long getEmpDeptID() {
        return empDeptID;
    }
    public String getEmpDeptName(){
        return empDeptName;
    }
    public String getReason(){
        return reason;
    }
    public Integer getType() {
        return type;
    }
    public Integer getState() {
        return state;
    }
    public Date getStartTime() {
        return startTime;
    }
    public Date getEndTime(){
        return endTime;
    }
    public void setAppID(Long appID) {
        this.appID = appID;
    }
    public void setEmpID(Long empID) {
        this.empID = empID;
    }
    public void setEmpName(String empName) {
        this.empName = empName;
    }
    public void setEmpDeptID(Long empDeptID) {
        this.empDeptID = empDeptID;
    }
    public void setEmpDeptName(String empDeptName) {
        this.empDeptName = empDeptName;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public void setStartTime(Date startTime){
        this.startTime = startTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setType(Integer type) {
        if(availableType.contains(type)){
            this.type = type;
        }
    }
    public void setState(Integer state) {
        if(availableState.contains(state)){
            this.state = state;
        }
    }
    public static Leave MapConvert(String leaveString){
        Map<String, Object> map = (Map<String, Object>)JSON.parse(leaveString);
        Leave leave = JSON.parseObject(map.get("Leave").toString(), Leave.class);
        return leave;
    }

}
