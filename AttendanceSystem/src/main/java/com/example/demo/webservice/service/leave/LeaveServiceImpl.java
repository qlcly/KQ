package com.example.demo.webservice.service.leave;

import com.example.demo.data.employee.Employee;
import com.example.demo.data.exception.BadRequestException;
import com.example.demo.data.exception.LackOfHolidaysException;
import com.example.demo.data.holidays.Holidays;
import com.example.demo.data.leave.Leave;
import com.example.demo.data.leave.LeaveBO;
import com.example.demo.data.user.User;
import com.example.demo.webservice.repository.EmployeeRepository;
import com.example.demo.webservice.repository.HolidaysRepository;
import com.example.demo.webservice.repository.LeaveRepository;
import com.example.demo.webservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.*;

@Service
public class LeaveServiceImpl implements LeaveService{
    private LeaveRepository leaveRepository;
    private HolidaysRepository holidaysRepository;
    private EmployeeRepository employeeRepository;
    private final long SECOND_CONVERT = 60 * 60 * 24 * 1000;
    @Autowired
    public LeaveServiceImpl(LeaveRepository leaveRepository, HolidaysRepository holidaysRepository,
                            EmployeeRepository employeeRepository){
        this.leaveRepository = leaveRepository;
        this.holidaysRepository = holidaysRepository;
        this.employeeRepository = employeeRepository;
    }
    @Override
    public List<Leave> getAllLeaveInfo(){
        String userID = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Employee employeeInfo = employeeRepository.getEmployeeInfoByEmpID(Long.parseLong(userID));
        switch (employeeInfo.getAuth()){
            case Employee.STAFF:
                return leaveRepository.getLeaveInfoByEmpID(employeeInfo.getEmpID());
            case Employee.DEPARTMENT_MANAGER:
                return leaveRepository.getLeaveInfoByDeptID(employeeInfo.getDeptID());
            case Employee.GENERAL_MANAGER:
                return leaveRepository.getAllLeaveInfo();
            default:
                break;
        }
        return leaveRepository.getAllLeaveInfo();
    }
    @Override
    public List<Leave> getLeaveInfoByEmpID(Long empID, Long appID){
        return leaveRepository.getLeaveInfo(empID, appID);
    }
    @Override
    public Leave addLeaveInfo(Leave leaveVO){
        checkLeaveValid(leaveVO);
        return leaveRepository.addLeaveInfo(leaveVO);
    }
    @Override
    public Leave editLeaveInfo(Leave leaveVO){
        long appID = leaveVO.getAppID();
        Map<String, Object> leaveMap = leaveRepository.QueryLeaveInfoByAppID(leaveVO.getAppID());
        Leave leaveBO = new LeaveBO();
        BOFilled(leaveBO, leaveMap);
        BOUpdate(leaveBO, leaveVO);
        checkLeaveValid(leaveBO);
        Leave leaveInfo = leaveRepository.editLeaveInfo(appID, leaveBO);
        return leaveInfo;
    }
    @Override
    public ResponseEntity deleteLeaveInfo(long appID){
        return leaveRepository.deleteLeaveInfo(appID);
    }


    //获取已有请假信息
    public void BOFilled(Leave leaveBO, Map<String, Object> leaveInfo){
        for(Map.Entry<String, Object> entry: leaveInfo.entrySet()){
            switch (entry.getKey()) {
                case "empId":
                    leaveBO.setEmpID((Long)entry.getValue());
                    break;
                case "appId":
                    leaveBO.setAppID((Long)entry.getValue());
                    break;
                case "empName" :
                    leaveBO.setEmpName((String)entry.getValue());
                    break;
                case "empDeptId" :
                    leaveBO.setEmpDeptID((Long)entry.getValue());
                    break;
                case "empDeptName" :
                    leaveBO.setEmpDeptName((String)entry.getValue());
                    break;
                case "reason" :
                    leaveBO.setReason((String) entry.getValue());
                    break;
                case "leaveType" :
                    leaveBO.setType((Integer)entry.getValue());
                    break;
                case "state" :
                    leaveBO.setState((Integer)entry.getValue());
                    break;
                case "startTime" :
                    leaveBO.setStartTime((Date)entry.getValue());
                    break;
                case "endTime" :
                    leaveBO.setEndTime((Date) entry.getValue());
                    break;
            }
        }

    }
    //用从前端接受的VO更新请假信息
    public void BOUpdate(Leave leaveBO, Leave leaveVO){

        if(leaveVO.getEmpName() != null){
            leaveBO.setEmpName(leaveVO.getEmpName());
        }
        if(leaveVO.getEmpDeptID() != null){
            leaveBO.setEmpDeptID(leaveVO.getEmpDeptID());
        }
        if(leaveVO.getEmpDeptName() != null){
            leaveBO.setEmpDeptName(leaveVO.getEmpDeptName());
        }
        if(leaveVO.getReason() != null){
            leaveBO.setReason(leaveVO.getReason());
        }
        if(leaveVO.getType() != null){
            leaveBO.setType(leaveVO.getType());
        }
        if(leaveVO.getState() != null){
            leaveBO.setState(leaveVO.getState());
        }
        if(leaveVO.getStartTime() != null){
            leaveBO.setStartTime(leaveVO.getStartTime());
        }
        if(leaveVO.getEndTime() != null){
            leaveBO.setEndTime(leaveVO.getEndTime());
        }
    }

    void checkLeaveValid(Leave leaveInfo){
        int days = (int)((leaveInfo.getEndTime().getTime() - leaveInfo.getStartTime().getTime()) / SECOND_CONVERT + 1);

        if (days < 0) {
            throw new BadRequestException();
        }
        int leaveType = leaveInfo.getType();
        int restHolidays = 0;
        switch (leaveType){
            case Leave.YEAR_LEAVE:
                restHolidays = holidaysRepository.getHolidaysInfoByID(leaveInfo.getEmpID()).getYearHolidays();
                break;
            case Leave.SICK_LEAVE:
                restHolidays = holidaysRepository.getHolidaysInfoByID(leaveInfo.getEmpID()).getSickHolidays();
                break;
            default:
                break;
        }
        if(leaveType == Leave.YEAR_LEAVE || leaveType == Leave.SICK_LEAVE) {
            if (days > restHolidays) {
                throw new LackOfHolidaysException();
            }
        }
    }
}
