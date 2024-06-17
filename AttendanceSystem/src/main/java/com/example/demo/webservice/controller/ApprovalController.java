package com.example.demo.webservice.controller;

import com.example.demo.data.approval.Approval;
import com.example.demo.data.approval.ApprovalVO;
import com.example.demo.data.exception.BadRequestException;
import com.example.demo.webconfig.RequestHolder;
import com.example.demo.webservice.service.approval.ApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/AttendanceSystem")
public class ApprovalController {
    private ApprovalService approvalServiceImpl;
    private Logger logger = LoggerFactory.getLogger(ApprovalController.class);
    @Autowired
    public ApprovalController(ApprovalService approvalServiceImpl){
        this.approvalServiceImpl = approvalServiceImpl;
    }
    //获得所有审批意见
    @GetMapping("/approval")
    public List<Approval> getAllApproval(){
        logger.info(RequestHolder.username.get() + " access approval[GET]");
        return approvalServiceImpl.getAllApproval();
    }
    //获得某领导审批意见
    @GetMapping("/approval/id")
    public List<Approval> getApprovalByEmpID(@RequestParam(value = "empID", required = false) Long empID,
                                          @RequestParam(value = "appID", required = false) Long appID){
        BadRequestException.checkAllNull(empID, appID);
        logger.info(RequestHolder.username.get() + " access approval/id[GET]");
        return approvalServiceImpl.getApprovalByID(empID, appID);
    }
    //添加审批意见
    @PostMapping("/approval")
    public Approval addApproval(@RequestBody String approvalString)
    {
        Approval approvalInfo = Approval.MapConvert(approvalString);
        BadRequestException.checkHasNull(ApprovalVO.ApprovalList(approvalInfo));
        logger.info(RequestHolder.username.get() + " access approval[POST]");
        return approvalServiceImpl.addApproval(approvalInfo);
    }
    //修改某条审批意见
    @PutMapping("/approval")
    public Approval editApproval(@RequestBody String approvalString)
    {
        Approval approvalInfo = Approval.MapConvert(approvalString);
        BadRequestException.checkHasNull(approvalInfo.getAppID());
        logger.info(RequestHolder.username.get() + " access approval[PUT]");
        return approvalServiceImpl.editApproval(approvalInfo);
    }
    //删除某条审批意见
    @DeleteMapping("/approval")
    public ResponseEntity deleteApproval(@NonNull @RequestParam(value = "appID") Long appID,
                                         @NonNull @RequestParam(value = "empID") Long empID){
        logger.info(RequestHolder.username.get() + " access approval[DELETE]");
        return approvalServiceImpl.deleteApproval(appID,empID);
    }
}
