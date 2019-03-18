package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByName(String coupon_name){
        try {
            return entityManager.createNamedQuery("couponByName", CouponEntity.class).setParameter("coupon_name", coupon_name).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public CouponEntity getCouponByUuid(UUID UUID){
        try {
            return entityManager.createNamedQuery("couponByUuid", CouponEntity.class).setParameter("uuid", UUID).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }
}
