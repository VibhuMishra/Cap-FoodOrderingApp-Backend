package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CustomerAddressDao {
    @PersistenceContext
    private EntityManager entityManager;


    public TypedQuery<CustomerAddressEntity> customerAddressByUuId(CustomerEntity customerEntity) {
        try {
            return entityManager.createNamedQuery("getCustomerAddressByUUID", CustomerAddressEntity.class).setParameter("customer_Id", customerEntity);
        } catch (NoResultException nre) {
            return null;
        }
    }


    public TypedQuery<CustomerAddressEntity> getCustomerAddressByUuId(String id) {
        try {
            return entityManager.createNamedQuery("getCustomerAddressById", CustomerAddressEntity.class).setParameter("id", id);
        } catch (NoResultException nre) {
            return null;
        }
    }
}