package com.cpf.controller;

import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.influx.manager.MonitorManager;
import com.cpf.ml.MlEngine;
import com.cpf.monitor.MonitorEngine;
import com.cpf.mysql.manager.AssetManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jieping
 * @create 2018-04-21
 **/
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private AssetManager assetManager;
    private ExecutorService executorService = Executors.newFixedThreadPool(30);
    @Autowired
    private MlEngine mlEngine;
    @Autowired
    private MonitorEngine monitorEngine;

    /**
     * 接受监控数据
     * @param monitorDO
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Object> monitor(@RequestBody MonitorDO monitorDO){
        //异步执行
        executorService.submit(()->monitorEngine.monitor(monitorDO));
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    /**
     * 获取指定主机的在指定时间段内的所有性能数据平均值
     * @param host 主机名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping(value = "/allAVG", method = RequestMethod.GET)
    ResponseEntity<Object> allAVG(String host,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date endTime){
        return new ResponseEntity<>(monitorManager.queryAllAVGByTime(host,startTime,endTime),HttpStatus.OK);
    }

    /**
     * 获取监控图表所需信息
     * @param host 主机名称
     * @param table 表名
     * @param col 列名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping(value = "/chartdata", method = RequestMethod.GET)
    ResponseEntity<Object> chartData(String host, String table, String col,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date endTime){
        return new ResponseEntity<>(monitorManager.queryChartDataByTime(host,table,col,startTime,endTime),HttpStatus.OK);
    }

    /**
     * 根据ip地址模糊分页查询资产信息
     * @return
     */
    @RequestMapping(value = "/assets", method = RequestMethod.GET)
    ResponseEntity<Object> assets(@PageableDefault() Pageable pageable,String ipaddr){
        return new ResponseEntity<>(assetManager.findByIP(pageable,ipaddr),HttpStatus.OK);
    }

}
