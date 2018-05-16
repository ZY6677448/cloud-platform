package com.cpf.controller;

import com.cpf.VO.AlarmVO;
import com.cpf.mysql.manager.AlarmManager;
import com.cpf.mysql.manager.DO.AlarmDO;
import com.cpf.service.CallbackResult;
import com.cpf.utils.VODOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jieping
 * @create 2018-04-28 15:25
 * @desc 报警
 **/
@RestController
public class AlarmController {
    @Autowired
    private AlarmManager alarmManager;

    /**
     * 获取所有预警信息
     * @return
     */
    @RequestMapping(value = "/alarms", method = RequestMethod.GET)
    ResponseEntity<Object> alarms(){
        //获取所有未过期的报警数据
        boolean expire = false;
        CallbackResult<List<AlarmDO>> result = alarmManager.get(expire);
        if(result.getSuccess()){
            CallbackResult<List<AlarmVO>> voResult = new CallbackResult<>(VODOConverter.alarmDOS2VOS(result.getResult()),true);
            return new ResponseEntity<>(voResult, HttpStatus.OK);

        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
