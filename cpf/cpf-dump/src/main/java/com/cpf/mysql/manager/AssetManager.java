package com.cpf.mysql.manager;

import com.cpf.mysql.dao.AssetDAO;
import com.cpf.mysql.manager.DO.AssetDO;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.utils.mapper.AssetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
/**
 * @author jieping
 * @create 2018-04-22
 **/
@Component
public class AssetManager extends ServiceTemplate {
    private static final Logger logger = LoggerFactory.getLogger(AssetManager.class);
    private static final String LIKE = "%";
    @Autowired
    private AssetDAO assetDAO;

    /**
     * 获取所有资产信息
     * @return
     */
    public CallbackResult<Page<AssetDO>> findByIP(Pageable pageable, String ipaddr){
        Object result =execute(logger, "all", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                return CallbackResult.success();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                String ipLike = ipaddr + LIKE;
                return new CallbackResult<>(assetDAO.findByIpaddrLike(ipLike,pageable).map(AssetMapper.INSTANCE::assetPO2DO),true);
            }
        });
        return (CallbackResult<Page<AssetDO>>)result;
    }
}
