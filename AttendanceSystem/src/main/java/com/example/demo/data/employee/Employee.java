package com.example.demo.data.employee;

import com.alibaba.fastjson.JSON;
import com.example.demo.data.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 * @author 刘永禄
 * @since 1.0.0
 * @version 1.1.0
 * 修改数据库，新增员工生日、性别等信息
 * @version 1.2.0
 * 删除副总经理相关部分
 */
public class Employee {
    private Long empID;
    private String empName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date getJobDay;
    private String gender;
    private Long deptID;
    private String deptName;
    private Integer auth;
    public static final int STAFF = 0;
    public static final int DEPARTMENT_MANAGER = 1;
    public static final int GENERAL_MANAGER = 2;
    private final List<Integer> availableAuth = Arrays.asList(STAFF, DEPARTMENT_MANAGER, GENERAL_MANAGER);
    public Long getEmpID(){
        return empID;
    }
    public String getEmpName() {
        return empName;
    }
    public Date getBirthday() {
        return birthday;
    }
    public Date getGetJobDay() {
        return getJobDay;
    }
    public String getGender() {
        return gender;
    }
    public Long getDeptID() {
        return deptID;
    }
    public String getDeptName() {
        return deptName;
    }
    public Integer getAuth() {
        return auth;
    }
    public void setEmpID(Long empID) {
        this.empID = empID;
    }
    public void setEmpName(String empName) {
        this.empName = empName;
    }
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
    public void setGetJobDay(Date getJobDay) {
        this.getJobDay = getJobDay;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setDeptID(Long deptID) {
        this.deptID = deptID;
    }
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    public void setAuth(Integer auth) {
        if(availableAuth.contains(auth)) {
            this.auth = auth;
        }
        else{
            throw new BadRequestException();
        }
    }
    public static Employee MapConvert(String employeeString){
        Map<String, Object> map = ( Map<String, Object>)JSON.parse(employeeString);
        Employee employee = JSON.parseObject(map.get("Employee").toString(), Employee.class);
        return employee;
    }
}
