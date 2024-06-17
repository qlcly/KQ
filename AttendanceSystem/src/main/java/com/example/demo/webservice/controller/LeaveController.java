package com.example.demo.webservice.controller;

import com.example.demo.data.exception.BadRequestException;
import com.example.demo.data.leave.Leave;
import com.example.demo.data.leave.LeaveVO;
import com.example.demo.webconfig.RequestHolder;
import com.example.demo.webservice.service.leave.LeaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/AttendanceSystem")
public class LeaveController {
    private LeaveService leaveService;
    @Autowired
    public LeaveController(LeaveService leaveService){
        this.leaveService = leaveService;
    }
    private Logger logger = LoggerFactory.getLogger(LeaveController.class);
    //获取所有员工请假信息
    @GetMapping("/leave")
    public List<Leave> getAllLeaveInfo(){
        logger.info(RequestHolder.username.get() + " access leave[GET]");
        return leaveService.getAllLeaveInfo();
    }

    //获取某员工请假信息
    @GetMapping("/leave/id")
    public List<Leave> getLeaveInfoByEmpID(@RequestParam(value = "empID",required = false) Long empID,
                                           @RequestParam(value = "appID", required = false) Long appID){
        BadRequestException.checkAllNull(empID,appID);
        logger.info(RequestHolder.username.get() + " access leave/id[GET]");
        return leaveService.getLeaveInfoByEmpID(empID,appID);
    }

    // 添加员工请假申请
    @PostMapping("/leave")
    public Leave addLeaveInfo(@RequestBody String leaveString){
        Leave leaveVO = Leave.MapConvert(leaveString);
        BadRequestException.checkHasNull(LeaveVO.LeaveList(leaveVO));
        logger.info(RequestHolder.username.get() + " access leave[POST]");
        return leaveService.addLeaveInfo(LeaveVO.convert(leaveVO));
    }

    //    修改员工请假申请
    @PutMapping("/leave")
    public Leave editLeaveInfo(@RequestBody String leaveString) {
        Leave leaveVO = Leave.MapConvert(leaveString);
        BadRequestException.checkAllNull(leaveVO.getAppID(), leaveVO.getEmpID());
        logger.info(RequestHolder.username.get() + " access leave[PUT]");
        return leaveService.editLeaveInfo(LeaveVO.convert(leaveVO));
    }

    //删除某员工请假申请
    @DeleteMapping("/leave")
    public ResponseEntity deleteLeaveInfo(@RequestParam(value = "appID") Long appID){
        logger.info(RequestHolder.username.get() + " access leave[DELETE]");
        return leaveService.deleteLeaveInfo(appID);
    }

}

