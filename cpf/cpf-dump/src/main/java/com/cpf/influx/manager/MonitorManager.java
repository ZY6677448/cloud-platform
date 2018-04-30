package com.cpf.influx.manager;

import com.cpf.constants.CpfDumpConstants;
import com.cpf.constants.RuleTypeEnum;
import com.cpf.exception.BusinessException;
import com.cpf.influx.dao.MonitorDAO;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.utils.MonitorUtil;
import com.cpf.utils.TimeStampUtil;
import com.cpf.utils.ValidationUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jieping
 * @create 2018-04-20
 **/
@Component
public class MonitorManager extends ServiceTemplate {
    @Autowired
    private MonitorDAO monitorDAO;
    private static Logger logger = LoggerFactory.getLogger(MonitorManager.class);
    /**
     * 查询过去一段时间的数据平均值
     * monitorEngine专用，不对外，所以不用执行ServiceTemplate
     * @param monitorDO
     * @param minutes
     * @return
     */
    public MonitorDO queryAVGByTime(MonitorDO monitorDO, Long minutes){
        if(ValidationUtil.isNull(minutes)){
            return monitorDO;
        }
        Map<String,String> tagMap = Maps.newHashMap();
        RuleTypeEnum ruleType = RuleTypeEnum.typeOf(monitorDO.getType());
        if(ruleType == null){
            //找不到监控类型
            throw new BusinessException(CpfDumpConstants.MONITOR_DATA_TYPE_ERROR);
        }
        //组装表的标签
        for(String tag : ruleType.getTagList()){
            tagMap.put(tag,monitorDO.getData().get(tag));
        }
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - TimeStampUtil.minutes2Time(minutes);
        QueryResult result = monitorDAO.queryAVGByTime(tagMap,monitorDO.getType(),startTime,endTime);
        //查询结果解析成domain对象
        List<MonitorDO> monitorDOList = parseQueryResult(result).get(monitorDO.getType());
        if(CollectionUtils.isNotEmpty(monitorDOList) && monitorDOList.size() == 1){
            return monitorDOList.get(0);
        }else {
            throw new BusinessException(CpfDumpConstants.QUERY_AVG_DATA_FAILED);
        }
    }

    /**
     * 查询指定机器的平均性能数据
     * @param hostName 机器名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public CallbackResult<List<MonitorDO>> queryAllAVGByTime(String hostName, Date startTime, Date endTime){
        Object result = execute(logger, "queryAllAVGByTime", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                 if(ValidationUtil.isNotNull(hostName)){
                     return CallbackResult.success();
                 }
                 return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                Map<String,String> tagMap = Maps.newHashMap();
                tagMap.put("host",hostName);
                Long start = startTime == null? null : startTime.getTime();
                Long end = endTime == null? null : endTime.getTime();
                QueryResult result = monitorDAO.queryAllAVGByTime(tagMap,start,end);
                Map<String,List<MonitorDO>> resultMap =  parseQueryResult(result);
                List<MonitorDO> resultList = Lists.newLinkedList();
                for(List<MonitorDO> monitorDOList : resultMap.values()){
                    resultList.addAll(monitorDOList);
                }
                return new CallbackResult<>(resultList,true);
            }
        });
        return ( CallbackResult<List<MonitorDO>>)result;
    }

    /**
     * 根据时间，查询指定表的所有数据
     * @param tableName 表名
     * @param tagMap 表的tag值
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return
     */
    public CallbackResult<List<MonitorDO>> queryDataByTime(String tableName,Map<String,String> tagMap,Date startTime,Date endTime,Long limit){
        Object result = execute(logger, "queryDataByTime", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                //limit参数为空为默认全选，在此不做校验
                if(ValidationUtil.isNotNull(tableName)){
                    return CallbackResult.success();
                }
                return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                Long start = startTime==null ? null : startTime.getTime();
                Long end = endTime==null ? null : endTime.getTime();
                QueryResult result = monitorDAO.queryDataByTime(tableName,tagMap,start,end,limit);
                Map<String,List<MonitorDO>> resultMap =  parseQueryResult(result);
                List<MonitorDO> resultList = Lists.newLinkedList();
                for(List<MonitorDO> monitorDOList : resultMap.values()){
                    resultList.addAll(monitorDOList);
                }
                return new CallbackResult<>(resultList,true);
            }
        });
        return ( CallbackResult<List<MonitorDO>>)result;
    }

    /**
     * 查询绘制图表所需数据
     * @param hostName 主机名
     * @param tableName 表名
     * @param col 指定列
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public CallbackResult<List<List<String>>> queryChartDataByTime(String hostName,String tableName,String col,Date startTime, Date endTime){
        Object result = execute(logger, "queryChartDataByTime", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(ValidationUtil.isNotNull(hostName,tableName,col,startTime,endTime)){
                    if(RuleTypeEnum.typeOf(tableName) != null){
                        return CallbackResult.success();

                    }
                }
                return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                List<String> meanList  = Lists.newArrayList(col);
                Map<String,String> tagMap = Maps.newHashMap();
                tagMap.put("host",hostName);
                QueryResult result = monitorDAO.queryAVGGroupByTime(tagMap,meanList,startTime.getTime(),endTime.getTime(),RuleTypeEnum.typeOf(tableName));
                Map<String,List<MonitorDO>> resultMap =  parseQueryResult(result);
                List<String> timeList = Lists.newLinkedList();
                List<String> colList = Lists.newLinkedList();
                if(resultMap.size() != 0 && resultMap.get(tableName) != null){
                    resultMap.get(tableName).forEach(monitorDO -> timeList.add(TimeStampUtil.influxTime2Java(monitorDO.getData().get("time"))));
                    //判断是否需要转换一下单位
                    if(MonitorUtil.isBytes(col)){
                        resultMap.get(tableName).forEach(monitorDO -> colList.add(MonitorUtil.bytes2MB(monitorDO.getData().get(col))));

                    }else {
                        resultMap.get(tableName).forEach(monitorDO -> colList.add(monitorDO.getData().get(col)));

                    }
                }
                return new CallbackResult<>(Lists.newArrayList(timeList,colList),true);
            }
        });
        return (CallbackResult<List<List<String>>>)result;
    }

    /**
     * 插入监控数据
     * @param monitorDO
     * @return
     */
    public CallbackResult<Object> addMonitor(MonitorDO monitorDO){
        Object result = execute(logger, "addMonitor", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(ValidationUtil.isNotNull(monitorDO)){
                    return CallbackResult.success();
                }
                return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() {

                monitorDAO.insert(convert2Point(monitorDO));
                return CallbackResult.success();
            }
        });
        return (CallbackResult<Object>)result;
    }

    //TODO 改成private

    /**
     * 将监控对象转换成influxdb接受的对象
     * @param monitorDO
     * @return
     */
    public Point convert2Point(MonitorDO monitorDO){
        Map<String,Object> fieldMap = Maps.newHashMap();
        Map<String,String> tagMap = Maps.newHashMap();
        List<String> tagList = RuleTypeEnum.typeOf(monitorDO.getType()).getTagList();
        for(Map.Entry<String,String> entry : monitorDO.getData().entrySet()){
            if(tagList.contains(entry.getKey())){
                tagMap.put(entry.getKey(),entry.getValue());
            }else {
                fieldMap.put(entry.getKey(),new Double(entry.getValue()));
            }
        }
        return  Point.measurement(monitorDO.getType()).tag(tagMap).fields(fieldMap).build();
    }

    /**
     * 将查询结果转换为MonitorDO
     * @param result
     * @return
     */
    public Map<String,List<MonitorDO>> parseQueryResult(QueryResult result){
        Map<String,List<MonitorDO>> resultMap = Maps.newHashMap();
        //一个Result代表一个表的查询结果
        for(QueryResult.Result r : result.getResults()){
            //查询结果为空
            if(CollectionUtils.isEmpty(r.getSeries())){
                continue;
            }
            QueryResult.Series series = r.getSeries().get(0);
            List<MonitorDO> list = Lists.newArrayList();
            //如果返回的是平均结果，需要去除结果属性的mean_前缀
            List<String> keys =  deleteMeanPrefix(series.getColumns());
            for(List<Object> values : series.getValues()){
                MonitorDO monitorDO = new MonitorDO();
                monitorDO.setType(series.getName());
                Map<String,String> data = Maps.newHashMap();
                for(int i = 0; i < values.size();i++){
                    if(ValidationUtil.isNotNull(values.get(i))){
                        data.put(keys.get(i),values.get(i).toString());
                    }else {
                        data.put(keys.get(i),"0.0");
                    }
                }
                monitorDO.setData(data);
                list.add(monitorDO);
            }
            resultMap.put(series.getName(),list);
        }
        return resultMap;
    }

    /**
     * 去除 mean_ 前缀
     * @param keys
     * @return
     */
    private List<String> deleteMeanPrefix(List<String> keys){
        List<String> list = Lists.newArrayList();
        for(String key : keys){
            list.add(StringUtils.stripStart(key,"mean_"));
        }
        return list;
    }

}
