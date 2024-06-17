package com.example.demo.webservice.service.approval;

import com.example.demo.data.approval.Approval;
import com.example.demo.data.approval.ApprovalBO;
import com.example.demo.data.approval.ApprovalDO;
import com.example.demo.data.employee.Employee;
import com.example.demo.data.holidays.Holidays;
import com.example.demo.data.leave.Leave;
import com.example.demo.webservice.repository.ApprovalRepository;
import com.example.demo.webservice.repository.EmployeeRepository;
import com.example.demo.webservice.repository.HolidaysRepository;
import com.example.demo.webservice.repository.LeaveRepository;
import com.example.demo.data.exception.LackOfHolidaysException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalServiceImpl implements ApprovalService{
    private ApprovalRepository approvalRepository;
    private LeaveRepository leaveRepository;
    private EmployeeRepository employeeRepository;
    private HolidaysRepository holidaysRepository;
    private final int SECOND_CONVERT = 60 * 60 * 24 * 1000;
    private final int SHORT_HOLIDAYS = 3;

    @Autowired
    public ApprovalServiceImpl(ApprovalRepository approvalRepository, LeaveRepository leaveRepository,
                               HolidaysRepository holidaysRepository, EmployeeRepository employeeRepository){
        this.approvalRepository = approvalRepository;
        this.leaveRepository = leaveRepository;
        this.holidaysRepository = holidaysRepository;
        this.employeeRepository = employeeRepository;
    }
    @Override
    public List<Approval> getAllApproval() {
        return approvalRepository.getAllApproval();
    }

    @Override
    public List<Approval> getApprovalByID(Long empID, Long appID) {
        return approvalRepository.getApprovalByID(empID, appID);
    }

    @Override
    public Approval addApproval(Approval approval) {
        int approvalType = getApprovalType(approval);
        approval.setType(approvalType);
        Approval approvalResult = approvalRepository.addApproval(approval);
        updateLeaveState(approvalResult);
        updateHolidayInfo(approvalResult);
        return approvalResult;
    }

    @Override
    public Approval editApproval(Approval approval) {
        long appID = approval.getAppID();
        long empID = approval.getEmpID();
        Map<String, Object> approvalMap = approvalRepository.queryByAppID(appID, empID);
        Approval approvalBO = new ApprovalBO();
        BOFilled(approvalBO, approvalMap);
        int beforeApprovalType = approvalBO.getType();
        BOUpdate(approvalBO, approval);
        Approval approvalResult = approvalRepository.editApproval(appID,approvalBO);
        updateLeaveState(approvalResult);
        updateGiveBackHoliday(approvalResult, beforeApprovalType);
        return approvalResult;
    }

    @Override
    public ResponseEntity deleteApproval(long appID, long empID) {
        deleteGiveBackHolidays(appID, empID);
        ResponseEntity result = approvalRepository.deleteApproval(appID, empID);
        try {
            Approval approvalInfo = approvalRepository.QueryLatestApprovalByAppID(appID);
            updateLeaveState(approvalInfo);
        } catch (Exception e){
            Approval approvalInfo = new ApprovalBO();
            approvalInfo.setAppID(appID);
            approvalInfo.setType(Approval.UNDETERMINED);
            updateLeaveState(approvalInfo);
        }
        return result;
    }

    public void BOFilled(Approval approvalBO, Map<String, Object> approvalMap){
        for(Map.Entry<String, Object> entry: approvalMap.entrySet()){
            switch (entry.getKey()){
                case "empId":
                    approvalBO.setEmpID((Long)entry.getValue());
                    break;
                case "approvalType":
                    approvalBO.setType((Integer) entry.getValue());
                    break;
                case "approval":
                    approvalBO.setApproval((int) entry.getValue() != 0);
                    break;
                case "comment":
                    approvalBO.setComment((String) entry.getValue());
                    break;
                case "time":
                    approvalBO.setTime((Date) entry.getValue());
                    break;
            }
        }
    }
    public void BOUpdate(Approval approvalBO, Approval approvalVO){
        if(approvalVO.getComment() != null){
            approvalBO.setComment(approvalVO.getComment());
        }
        if(approvalVO.getApproval() != null){
            approvalBO.setApproval(approvalVO.getApproval());
        }
        if(approvalVO.getEmpID() != null){
            approvalBO.setEmpID(approvalVO.getEmpID());
        }
        if(approvalVO.getTime() != null){
            approvalBO.setTime(approvalVO.getTime());
        }
        approvalBO.setType(getApprovalType(approvalVO));
    }

    public int getApprovalType(Approval approvalInfo){
        long empID = approvalInfo.getEmpID();
        Employee employeeInfo = employeeRepository.getEmployeeInfoByEmpID(empID);
        if(approvalInfo.getApproval()){
            switch (employeeInfo.getAuth()){
                case Employee.DEPARTMENT_MANAGER:
                    return Approval.DEPARTMENT_MANAGER_APPROVAL;
                case Employee.GENERAL_MANAGER:
                    return Approval.GENERAL_MANAGER_APPROVAL;
                default:
                    return Approval.UNDETERMINED;
            }
        }
        else {
            return Approval.REFUSE;
        }
    }

    public void updateLeaveState(Approval approval){
        Leave leaveInfo = leaveRepository.getLeaveInfoByAppID(approval.getAppID());
        leaveInfo.setState(approval.getType());
        leaveRepository.editLeaveInfo(approval.getAppID(), leaveInfo);
    }

    public void updateHolidayInfo(Approval approval){
        Leave leaveInfo = leaveRepository.getLeaveInfoByAppID(approval.getAppID());
        int approvalResult = approval.getType();
        int days = (int)(leaveInfo.getEndTime().getTime() - leaveInfo.getStartTime().getTime()) / SECOND_CONVERT + 1;
        Holidays holidaysInfo = holidaysRepository.getHolidaysInfoByID(leaveInfo.getEmpID());
        if(approvalResult == Approval.REFUSE){
            return;
        }
        if(days <= SHORT_HOLIDAYS && approvalResult == Approval.DEPARTMENT_MANAGER_APPROVAL){
            judgeHolidayRest(leaveInfo, days, holidaysInfo);
            Leave approvalInfo = leaveRepository.getLeaveInfoByAppID(approval.getAppID());
            approvalInfo.setState(Leave.GENERAL_MANAGER_APPROVAL);
            leaveRepository.editLeaveInfo(approval.getAppID(), approvalInfo);
        }
        else if(days > SHORT_HOLIDAYS && approvalResult == Approval.GENERAL_MANAGER_APPROVAL){
            judgeHolidayRest(leaveInfo, days, holidaysInfo);
        }
    }

    private void judgeHolidayRest(Leave leaveInfo, int days, Holidays holidaysInfo) {
        switch (leaveInfo.getType()){
            case Leave.YEAR_LEAVE:
                int restYearHolidays = holidaysInfo.getYearHolidays() - days;
                if(restYearHolidays >=0){
                    holidaysInfo.setYearHolidays(restYearHolidays);
                    holidaysRepository.editRestHolidaysInfo(holidaysInfo.getEmpID(), holidaysInfo);
                }
                else {
                    LackOfHolidaysException.throwHolidaysLackEx();
                }
                break;
            case Leave.PRIVATE_LEAVE:
                holidaysInfo.setPrivateHolidays(days + holidaysInfo.getPrivateHolidays());
                holidaysRepository.editRestHolidaysInfo(holidaysInfo.getEmpID(), holidaysInfo);
                break;
            case Leave.SICK_LEAVE:
                int restSickHolidays = holidaysInfo.getSickHolidays() - days;
                if(restSickHolidays >= 0){
                    holidaysInfo.setSickHolidays(restSickHolidays);
                    holidaysRepository.editRestHolidaysInfo(holidaysInfo.getEmpID(), holidaysInfo);
                }
                else {
                    LackOfHolidaysException.throwHolidaysLackEx();
                }
                break;
            default:
                break;
        }
    }

    public void updateGiveBackHoliday(Approval approvalResult, int beforeApprovalType){
        Leave leaveInfo = leaveRepository.getLeaveInfoByAppID(approvalResult.getAppID());
        int days = (int)(leaveInfo.getEndTime().getTime() - leaveInfo.getStartTime().getTime()) / SECOND_CONVERT + 1;

        int nowApprovalType = approvalResult.getType();
        if(nowApprovalType == Approval.REFUSE && ((beforeApprovalType == Approval.GENERAL_MANAGER_APPROVAL && days > SHORT_HOLIDAYS)
                || (beforeApprovalType == Approval.DEPARTMENT_MANAGER_APPROVAL && days <= SHORT_HOLIDAYS))){
            giveBackHolidays(leaveInfo, days);
        }
    }

    public void deleteGiveBackHolidays(long appID, long empID){
        Approval approvalInfo = approvalRepository.QueryApprovalByAppID(appID, empID);
        Leave leaveInfo = leaveRepository.getLeaveInfoByAppID(approvalInfo.getAppID());
        int days = (int)(leaveInfo.getEndTime().getTime() - leaveInfo.getStartTime().getTime()) / SECOND_CONVERT + 1;

        if((approvalInfo.getType() == Approval.GENERAL_MANAGER_APPROVAL && days > SHORT_HOLIDAYS)
                || (approvalInfo.getType() == Approval.DEPARTMENT_MANAGER_APPROVAL && days <= SHORT_HOLIDAYS)){
            giveBackHolidays(leaveInfo, days);
        }
    }

    private void giveBackHolidays(Leave leaveInfo, int days) {
        Holidays holidaysInfo = holidaysRepository.getHolidaysInfoByID(leaveInfo.getEmpID());
        switch (leaveInfo.getType()){
            case Leave.YEAR_LEAVE:
                holidaysInfo.setYearHolidays(holidaysInfo.getYearHolidays() + days);
                holidaysRepository.editRestHolidaysInfo(holidaysInfo.getEmpID(), holidaysInfo);
                break;
            case Leave.PRIVATE_LEAVE:
                holidaysInfo.setPrivateHolidays(holidaysInfo.getPrivateHolidays() - days);
                holidaysRepository.editRestHolidaysInfo(holidaysInfo.getEmpID(), holidaysInfo);
                break;
            case Leave.SICK_LEAVE:
                holidaysInfo.setSickHolidays(holidaysInfo.getSickHolidays() + days);
                holidaysRepository.editRestHolidaysInfo(holidaysInfo.getEmpID(), holidaysInfo);
                break;
            default:
                break;
        }
    }

}
