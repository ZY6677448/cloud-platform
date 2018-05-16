package com.cpf.mysql.dao;

import com.cpf.mysql.dao.PO.AssetPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * @author jieping
 * @create 2018-04-25 19:56
 **/
public interface AssetDAO extends PagingAndSortingRepository<AssetPO, String> {
    /**
     * 查询所有对象
     * @return
     */
    @Override
    Page<AssetPO> findAll(Pageable pageable);

    /**
     * 根据ipaddr模糊查询
     * @param ipaddr
     * @param pageable
     * @return
     */
    Page<AssetPO> findByIpaddrLike(String ipaddr,Pageable pageable);

    /**
     * 保存对象
     * @param alarmPO
     * @return
     */
    @Override
    AssetPO save(AssetPO assetPO);
}
