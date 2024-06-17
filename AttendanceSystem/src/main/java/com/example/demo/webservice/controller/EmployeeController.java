package com.example.demo.webservice.controller;

import com.example.demo.data.employee.Employee;
import com.example.demo.data.exception.BadRequestException;
import com.example.demo.webconfig.RequestHolder;
import com.example.demo.webservice.service.employee.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/AttendanceSystem")
public class EmployeeController {
    private EmployeeService employeeServiceImpl;
    private Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    @Autowired
    public EmployeeController(EmployeeService employeeServiceImpl){
        this.employeeServiceImpl = employeeServiceImpl;
    }
    @GetMapping("/employee")
    public List<Employee> getAllEmployeeInfo(){
        logger.info(RequestHolder.username.get() + " access employee[GET]");
        return employeeServiceImpl.getAllEmployeeInfo();
    }
    @GetMapping("/employee/id")
    public Employee getEmployeeInfoByID(@NonNull @RequestParam(value = "empID") Long empID){
        BadRequestException.checkHasNull(empID);
        logger.info(RequestHolder.username.get() + " access employee/id[GET]");
        return employeeServiceImpl.getEmployeeInfoByEmpID(empID);
    }
    @PostMapping("/employee")
    public Employee addEmployeeInfo(@RequestBody String employeeString){
        Employee employee = Employee.MapConvert(employeeString);
        BadRequestException.checkHasNull(employee);
        logger.info(RequestHolder.username.get() + " access employee[POST]");
        return employeeServiceImpl.addEmployeeInfo(employee);
    }
    @PutMapping("/employee")
    public Employee editEmployeeInfo(@RequestBody String employeeString){
        Employee employee = Employee.MapConvert(employeeString);
        BadRequestException.checkHasNull(employee.getEmpID());
        logger.info(RequestHolder.username.get() + " access employee[PUT]");
        return employeeServiceImpl.editEmployeeInfo(employee);
    }
    @DeleteMapping("/employee")
    public ResponseEntity deleteEmployeeInfo(@RequestParam Long empID){
        BadRequestException.checkHasNull(empID);
        logger.info(RequestHolder.username.get() + " access employee[DELETE]");
        return employeeServiceImpl.deleteEmployeeInfo(empID);
    }
    @GetMapping(value = "/employee/report", produces = {"application/vnd.ms-excel;charset=UTF-8"})
    public ResponseEntity<byte[]> getAttendanceReport()
    {
        logger.info(RequestHolder.username.get() + " access employee/report[GET]");
        return employeeServiceImpl.getAttendanceReport();
    }
}
