package com.example.demo.data.employee;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDO extends Employee implements RowMapper<Employee> {
    public EmployeeDO(){
        super();
    }
    @Override
    public Employee mapRow(ResultSet rs, int i) throws SQLException {
        Employee employeeDO = new EmployeeDO();
        employeeDO.setEmpID(rs.getLong("empId"));
        employeeDO.setEmpName(rs.getString("empName"));
        employeeDO.setBirthday(rs.getDate("birthday"));
        employeeDO.setGetJobDay(rs.getDate("getJobDay"));
        employeeDO.setGender(rs.getString("gender"));
        employeeDO.setDeptID(rs.getLong("deptId"));
        employeeDO.setDeptName(rs.getString("deptName"));
        employeeDO.setAuth(rs.getInt("auth"));
        return employeeDO;
    }
}
