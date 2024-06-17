package com.example.demo.data.approval;

import com.alibaba.fastjson.JSON;
import com.example.demo.data.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class Approval {
    private Long empID;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date time;
    private Long appID;
    private Integer type;
    private Boolean approval;
    private String comment;
    public static final int REFUSE = 0;
    public static final int UNDETERMINED = 1;
    public static final int DEPARTMENT_MANAGER_APPROVAL = 2;
    public static final int GENERAL_MANAGER_APPROVAL = 3;
    private List<Integer> availableType = Arrays.asList(REFUSE, UNDETERMINED,
            DEPARTMENT_MANAGER_APPROVAL, GENERAL_MANAGER_APPROVAL);
    protected Approval()
    {

    }
    public Long getEmpID(){
        return empID;
    }
    public Long getAppID() {
        return appID;
    }

    public Boolean getApproval() {
        return approval;
    }

    public String getComment() {
        return comment;
    }

    public Date getTime() {
        return time;
    }

    public Integer getType() {
        return type;
    }

    public void setAppID(Long appID)
    {
        this.appID = appID;
    }

    public void setEmpID(Long empID)
    {
        this.empID = empID;
    }

    public void setApproval(Boolean approval)
    {
        this.approval = approval;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public void setType(Integer type)
    {
        if (availableType.contains(type)) {
            this.type = type;
        }
        else{
            throw new BadRequestException();
        }
    }
    public static Approval MapConvert(String approvalString) {
        Map<String, Object> map =  (Map<String, Object>)JSON.parse(approvalString);
        Approval approval = JSON.parseObject(map.get("Approval").toString(), ApprovalVO.class);
        return approval;
    }
}
