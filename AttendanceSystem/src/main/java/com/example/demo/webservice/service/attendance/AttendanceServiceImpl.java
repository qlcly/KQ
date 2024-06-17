package com.example.demo.webservice.service.attendance;

import com.example.demo.data.attendance.Attendance;
import com.example.demo.webservice.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private AttendanceRepository attendanceRepository;
    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository){
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.getAllAttendance();
    }

    @Override
    public List<Attendance> getAttendanceByID(Long empID, Long attendanceID) {
        return attendanceRepository.getAttendanceByID(empID, attendanceID);
    }

    @Override
    public ResponseEntity importAttendance(File file) {
        return attendanceRepository.importAttendance(file);
    }

    @Override
    public ResponseEntity deleteAttendance(long attendanceID) {
        return attendanceRepository.deleteAttendance(attendanceID);
    }
}
