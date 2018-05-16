package com.cpf.mysql.dao.PO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
/**
 * @author jieping
 * @create 2018-04-21
 * @desc 资产信息
 **/
@Data
@Entity(name = "asset")
public class AssetPO {
    @Id
    private String id;
    private String aid;
    private String hostname;
    private String ipaddr;
}
