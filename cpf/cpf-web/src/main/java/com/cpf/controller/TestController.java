package com.cpf.controller;

import com.cpf.influx.dao.CpuDAO;
import com.cpf.influx.holder.ModelHolder;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.influx.manager.MonitorManager;
import com.cpf.ml.MlEngine;
import com.cpf.monitor.MonitorEngine;
import com.cpf.monitor.RuleHolder;
import com.cpf.mysql.dao.AggreModelDAO;
import com.cpf.task.TrainTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by jieping on 2018-04-19
 */
@RestController
public class TestController {
    @Autowired
    private RuleHolder ruleHolder;
    @Autowired
    private CpuDAO cpuDAO;
    @Autowired
    private ModelHolder modelHolder;
    @Autowired
    private TrainTask trainTask;
    @Autowired
    private MlEngine mlEngine;
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private MonitorEngine monitorEngine;
    @Autowired
    private AggreModelDAO aggreModelDAO;
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<Object> test() throws Exception {
//        List<MonitorDO> monitorDOList = monitorManager.queryDataByTime("win_cpu",null,null,null,2L).getResult();
//        mlEngine.predict(monitorDOList.get(0));
//        List<AggreModelPO> aggreModelPOS = aggreModelDAO.findAll();
//        AggreModelPO aggreModelPO = aggreModelDAO.findByModelsIsContaining(aggreModelPOS.get(0).getModels().get(0));

        List<MonitorDO> monitorDOList = monitorManager.queryDataByTime("win_cpu",null,null,null,1L).getResult();
        monitorEngine.monitor(monitorDOList.get(0));
        return new ResponseEntity<>(monitorDOList.get(0),HttpStatus.OK);
    }
}
