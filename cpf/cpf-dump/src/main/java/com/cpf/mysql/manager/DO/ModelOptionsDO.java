package com.cpf.mysql.manager.DO;

import com.cpf.constants.ModelTypeEnum;
import lombok.Data;

import java.util.List;
/**
 * @author jieping
 * @create 2018-04-09
 **/
@Data
public class ModelOptionsDO {
    private Long id;
    /**
     * 模型类型
     */
    private ModelTypeEnum modelType;
    /**
     * 模型的参数（json格式）
     */
    List<ModelOptionDO> options;

}
