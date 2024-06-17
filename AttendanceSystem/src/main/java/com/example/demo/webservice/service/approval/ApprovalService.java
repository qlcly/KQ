package com.example.demo.webservice.service.approval;

import com.example.demo.data.approval.Approval;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApprovalService {
    List<Approval> getAllApproval();
    List<Approval> getApprovalByID(Long empID, Long appID);
    Approval addApproval(Approval approval);
    Approval editApproval(Approval approval);
    ResponseEntity deleteApproval(long appID, long empID);
    int getApprovalType(Approval approval);
}
