package com.example.demo.webservice.controller;

import com.example.demo.data.attendance.Attendance;
import com.example.demo.data.exception.BadRequestException;
import com.example.demo.webconfig.RequestHolder;
import com.example.demo.webservice.service.attendance.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/AttendanceSystem")
public class AttendanceController {
    private AttendanceService attendanceServiceImpl;
    private Logger logger = LoggerFactory.getLogger(AttendanceController.class);
    @Autowired
    public AttendanceController(AttendanceService attendanceServiceImpl){
        this.attendanceServiceImpl = attendanceServiceImpl;
    }
    @GetMapping("/attendance")
    public List<Attendance> getAllAttendance(){
        logger.info(RequestHolder.username.get() + " access attendance[GET]");
        return attendanceServiceImpl.getAllAttendance();
    }
    @GetMapping("/attendance/id")
    public List<Attendance> getAttendanceByID(@RequestParam(value = "empID", required = false) Long empID,
                                              @RequestParam(value = "attendanceID", required = false) Long attendanceID){
        BadRequestException.checkAllNull(empID, attendanceID);
        logger.info(RequestHolder.username.get() + " access attendance/id[GET]");
        return attendanceServiceImpl.getAttendanceByID(empID, attendanceID);
    }

    @PostMapping("/attendance")
    @ResponseBody
    public ResponseEntity importAttendance(@RequestParam("file") MultipartFile file) throws IOException
    {
        logger.info(RequestHolder.username.get() + " access attendance[POST]");
        File path = new File(new File("").getAbsolutePath()+ "/receivedFiles/attendance/");
        if (!path.exists())
        {
            path.mkdirs();
        }
        File f = new File(new File("").getAbsolutePath()+ "/receivedFiles/attendance/" + file.getOriginalFilename());
        file.transferTo(f);
        return attendanceServiceImpl.importAttendance(f);
    }
    @DeleteMapping("/attendance")
    public ResponseEntity<String> deleteAttendance(@NonNull @RequestParam Long attendanceID){
        logger.info(RequestHolder.username.get() + " access attendance[DELETE]");
        return attendanceServiceImpl.deleteAttendance(attendanceID);
    }
}
