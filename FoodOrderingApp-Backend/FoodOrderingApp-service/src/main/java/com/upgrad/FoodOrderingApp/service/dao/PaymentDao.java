package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.UUID;

@Repository
public class PaymentDao {
    @PersistenceContext
    private EntityManager entityManager;

    public TypedQuery<PaymentEntity> getPaymentName(){
        try{
            return entityManager.createNamedQuery("paymentName", PaymentEntity.class);
        }catch (NoResultException nre){
            return null;
        }
    }

    public TypedQuery<PaymentEntity> getPaymentById(UUID paymentId){
        try{
            return entityManager.createNamedQuery("paymentId", PaymentEntity.class).setParameter("uuid", paymentId);
        }catch (NoResultException nre){
            return null;
        }
    }

}
