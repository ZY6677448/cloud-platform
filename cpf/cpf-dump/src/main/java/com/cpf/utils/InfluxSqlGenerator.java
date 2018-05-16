package com.cpf.utils;

import com.cpf.constants.TimeIntervalEnum;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-21
 * @desc influx sql语句生成器
 **/
public class InfluxSqlGenerator {
    private static final String FROM = " from ";
    private static final String SELECT = " select ";
    private static final String WHERE = " where ";
    private static final String GROUP_BY = " group by ";
    private static final String AND = " and ";
    private static final String FINISH = " ; ";
    private static final String FILL = "  fill(0)" + FINISH;
    private static final String LIMIT = " limit ";
    private static final String COMMA = " , ";
    private static final String ALL  = " * ";


    /**
     *  生成 select mean(*) from tableName where [tag=tagValue] and time > startTime and time < endTime fill(0);
     * @param tags 条件
     * @param tableName 表名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public static String meanDataSql(Map<String,String> tags, String tableName, Long startTime, Long endTime){
        return meanSql(tags,null,tableName,startTime,endTime) + FILL;
    }

    /**
     *  生成 select mean(*) from tableName where [tag=tagValue] and time > startTime and time < endTime group by time(interval) fill(0);
     * @param tags
     * @param tableName
     * @param startTime
     * @param endTime
     * @return
     */
    public static String meanDatasSql(Map<String,String> tags,List<String> meanList, String tableName, Long startTime, Long endTime){
        String sql = meanSql(tags,meanList,tableName,startTime,endTime);
        String group = GROUP_BY + "time(" + TimeIntervalEnum.interval(startTime,endTime) +")";
        return sql + group + FILL;
    }

    /**
     *  生成 select * from tableName where [and tag=value] and time > startTime and time < endTime ;
     * @param tableName
     * @param startTime
     * @param endTime
     * @param limit
     * @return
     */
    public static String dataSql(String tableName, Map<String,String> tagMap, Long startTime, Long endTime, Long limit){
        StringBuilder sql = new StringBuilder();
        sql.append(SELECT + ALL + FROM).append(tableName).append(condition(tagMap, startTime, endTime));
        if(limit != null){
            sql.append(LIMIT).append(limit);
        }
        sql.append(FINISH);
        return sql.toString();
    }

    /**
     * 查询最新数据
     * select * from tableName where [and tag=value] order by time desc limit 1
     * @param tableName
     * @param tagMap
     * @return
     */
    public static String recentDataSql(String tableName,Map<String,String> tagMap){
        return (SELECT + ALL + FROM + tableName + condition(tagMap, null, null) + " order by time desc limit 1") +
                FINISH;
    }

    /**
     * 生成 select [,derivative(key2,unit)] from tableName where [and tag=value] order by time desc limit 1
     * @param tableName
     * @param keys
     * @param unit
     * @param tagMap
     * @return
     */
    public static String changeRateSql(String tableName,List<String> keys,String unit,Map<String,String> tagMap){
        return (SELECT + derivative(keys, unit) + FROM + tableName + condition(tagMap, null, null)
                + " order by time desc limit 1");
    }

    /**
     * 生成 select mean(a) as a,mean(b) as b from tableName where [tag=value] and time < endTime and time > startTime
     * @param tags
     * @param meanList
     * @param tableName
     * @param startTime
     * @param endTime
     * @return
     */
    private static String meanSql(Map<String,String> tags, List<String> meanList, String tableName, Long startTime, Long endTime){
        return SELECT + mean(meanList) + FROM + tableName + condition(tags,startTime,endTime);
    }

    /**
     *  产生 mean(select1),mean(select2),mean(select2)
     * @param selectList
     * @return
     */
    private  static String mean(List<String> selectList){
        //为空代表全选
        if(selectList == null){
            return " mean(*) ";
        }
        List<String> means = Lists.newArrayList();
        for(String select : selectList){
            String str = " mean(" + select + ") as " + select + " ";
            means.add(str);
        }
        return Joiner.on(COMMA).join(means);
    }

    /**
     * 产生 key1=value1 and key2=value1 and key3=value1 and key4=value1 的条件语句
     * @param conditionMap
     * @return
     */
    private static String condition(Map<String,String> conditionMap,Long startTime,Long endTime){
        if(ValidationUtil.isAllNull(conditionMap,startTime,endTime)){
            return "";
        }
        List<String> conditionList = Lists.newArrayList();
        if(MapUtils.isNotEmpty(conditionMap)){
            for(Map.Entry<String,String> tag : conditionMap.entrySet()){
                String condition = tag.getKey() +
                        "='" +
                        tag.getValue() +
                        "'";
                conditionList.add(condition);
            }
        }
        if(startTime != null){
            conditionList.add(" time > " +  TimeStampUtil.javaTime2Influx(startTime));
        }
        if(endTime != null){
            conditionList.add(" time < " +  TimeStampUtil.javaTime2Influx(endTime));
        }
        return WHERE + Joiner.on(AND).join(conditionList);
    }

    private static String derivative(List<String> keys, String unit){
        if(CollectionUtils.isEmpty(keys)){
            return "derivative(*," + unit + ")";
        }
        List<String> derivatives = Lists.newArrayList();
        keys.forEach(key-> derivatives.add("derivative(" + key + "," + unit + ")"));
        return Joiner.on(COMMA).join(derivatives);
    }
    public static void main(String[] args){
        List<String> keys = Lists.newArrayList("idal_time","pec_time");
        Map<String,String> tagMap = Maps.newHashMap();
        tagMap.put("instance","0");
        tagMap.put("host","windows");
        System.out.println(changeRateSql("win_cpu",keys,"1h",tagMap));
        System.out.println(recentDataSql("win_cpu",tagMap));
    }

}
