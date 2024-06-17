package com.example.demo.webservice.service.holidays;

import com.example.demo.data.holidays.Holidays;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface HolidaysService {
    List<Holidays> getAllHolidaysInfo();
    Holidays getHolidaysInfoByEmpID(long empID);
    Holidays editRestHolidaysInfo(Holidays holidaysVO);
    Holidays addHolidaysInfo(Holidays holidaysVO);
    ResponseEntity deleteHolidaysInfo(long empID);

}
