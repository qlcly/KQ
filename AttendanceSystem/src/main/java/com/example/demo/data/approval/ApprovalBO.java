package com.example.demo.data.approval;



public class ApprovalBO extends Approval
{
    public ApprovalBO()
    {
        super();
    }
    public static Approval convert(Approval approval){
        Approval approvalInfo = new ApprovalBO();
        approvalInfo.setAppID(approval.getAppID());
        approvalInfo.setEmpID(approval.getEmpID());
        approvalInfo.setApproval(approval.getApproval());
        approvalInfo.setComment(approval.getComment());
        approvalInfo.setType(approval.getType());
        approvalInfo.setTime(approval.getTime());
        return approvalInfo;
    }
}
