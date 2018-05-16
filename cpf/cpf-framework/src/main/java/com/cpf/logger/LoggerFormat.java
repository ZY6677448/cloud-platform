package com.cpf.logger;

import com.google.common.base.Joiner;


/**
 * @author jieping
 * @create 2018-04-02.
 * @desc 日志格式
 **/
public class LoggerFormat {
    private static final String FIRST_SPLIT = "|";
    private static final String SECOND_SPLIT = ",";
    public static String format(String business,String[] params,String result,String desc){
        if(params == null){
            params = new String[]{"null"};
        }
        return Joiner.on(FIRST_SPLIT).useForNull("null").join(business,
                Joiner.on(SECOND_SPLIT).useForNull("null").join(params),result,desc);

    }
}

