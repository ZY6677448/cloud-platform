package com.cpf.controller;

import com.alibaba.fastjson.JSON;
import com.cpf.holder.ModelHolder;
import com.cpf.holder.RuleHolder;
import com.cpf.influx.manager.MonitorManager;
import com.cpf.ml.MlEngine;
import com.cpf.monitor.MonitorEngine;
import com.cpf.mysql.dao.AggreModelDAO;
import com.cpf.mysql.dao.AssetDAO;
import com.cpf.task.TrainTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.influxdb.DefaultInfluxDBTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jieping on 2018-04-19
 */
@RestController
public class TestController {
    @Autowired
    private RuleHolder ruleHolder;
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
    @Autowired
    private DefaultInfluxDBTemplate template;
    @Autowired
    private AssetDAO assetDAO;
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<Object> test() {
////        List<MonitorDO> monitorDOList = monitorManager.queryDataByTime("win_cpu",null,null,null,2L).getResult();
////        mlEngine.predict(monitorDOList.get(0));
////        List<AggreModelPO> aggreModelPOS = aggreModelDAO.findAll();
////        AggreModelPO aggreModelPO = aggreModelDAO.findByModelsIsContaining(aggreModelPOS.get(0).getModels().get(0));
//
//        List<MonitorDO> monitorDOList = monitorManager.queryDataByTime("win_swap",null,null,null,10000L).getResult();
//        String unit = TimeIntervalEnum.generateInterval(TimeIntervalEnum.HOUR,1L);
//
//        MonitorDO derivativeDo = monitorManager.queryChangeRateByTime(monitorDOList.get(0),unit).getResult();
//        System.out.println(JSON.toJSONString(derivativeDo));
//        Map<String,String> derivativeMap = Maps.newHashMap();
//        derivativeMap.put("derivative_System_Calls_persec","-1.13303671875E7");
//        derivativeMap.put("derivative_Processor_Queue_Length","0.0");
//        derivativeMap.put("derivative_Context_Switches_persec","-8391714.2578125");
//        derivativeMap.put("derivative_System_Up_Time","-9.288360039550781E8");
//        Random random = new Random();
//        monitorDOList.forEach(monitorDO -> {
//            monitorDO.setType("win_swap_train");
//            monitorDO.getData().putAll(derivativeMap);
//            monitorDO.getData().put("danger",String.valueOf(random.nextBoolean()));
//            monitorDO.getData().put("time",TimeStampUtil.javaTime2Influx(System.currentTimeMillis()).toString());
//
//        });
//        List<Point> pointList= monitorDOList.stream().map(monitorDO -> monitorManager.convert2Point(monitorDO)).collect(Collectors.toList());
//        template.write(pointList);
//        trainTask.train();
//        List<MonitorDO> monitorDOList = monitorManager.queryDataByTime("win_cpu_train",null,null,null,1L).getResult();
//        MonitorDO monitorDO = monitorDOList.get(0);
//        monitorDO.setType("win_cpu");
//        mlEngine.predict(monitorDO);
//        trainTask.train();
//        List<MonitorDO> samples = monitorManager.queryDataByTime("win_cpu",null,null,null,1L).getResult();
//        monitorEngine.monitor(samples.get(0));
//        List<AssetPO> assetPOList = assetDAO.findAll();
//        AssetPO assetPO = assetPOList.get(0);
//        for(int i = 0; i< 100;i++){
//            //深度复制
//            AssetPO temp = JSON.parseObject(JSON.toJSONString(assetPO),new TypeReference<AssetPO>(){});
//            temp.setId(String.valueOf(i));
//            assetDAO.save(temp);
//        }
        Pageable pageable = new PageRequest(0,10);
        String ipaddr = "192.168.207.82" + "%";
        System.out.println(JSON.toJSONString(assetDAO.findByIpaddrLike(ipaddr,pageable)));
        System.out.println(assetDAO.findByIpaddrLike("%",null).getNumberOfElements());
        return new ResponseEntity<>(true,HttpStatus.OK);
    }
}
