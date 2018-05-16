package com.cpf.controller;

import com.cpf.mysql.manager.DO.RuleDO;
import com.cpf.mysql.manager.RuleManager;
import com.cpf.holder.RuleHolder;
import com.cpf.service.CallbackResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * @author jieping
 * @create 2018-04-11
 **/
@RestController
@RequestMapping("/config")
public class RuleController {
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private RuleHolder ruleHolder;
    /**
     * 添加或者修改实时监控规则
     * @param ruleDO
     * @return
     */
    @RequestMapping(value = "/rule", method = RequestMethod.POST)
    ResponseEntity<Object> rule(@RequestBody RuleDO ruleDO){
        ruleDO.setModifyTime(new Date());
        CallbackResult<RuleDO>  result = ruleManager.save(ruleDO);
        if(result.getSuccess()){
            //更新内存中的监控规则
            ruleHolder.refresh();
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    /**
     * 获取所有监控规则
     * @return
     */
    @RequestMapping(value = "/rules", method = RequestMethod.GET)
    ResponseEntity<Object> rules(){
        return new ResponseEntity<>(ruleManager.all(), HttpStatus.OK);
    }

    /**
     * 删除指定监控规则
     * @param id
     * @return
     */
    @RequestMapping(value = "/rule", method = RequestMethod.DELETE)
    ResponseEntity<Object> rule(@RequestParam Long id){
        return new ResponseEntity<>(ruleManager.delete(id), HttpStatus.OK);
    }

}
