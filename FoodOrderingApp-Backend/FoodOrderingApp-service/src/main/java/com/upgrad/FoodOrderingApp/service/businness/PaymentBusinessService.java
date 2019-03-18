package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

@Service
public class PaymentBusinessService {

    @Autowired
    private PaymentDao paymentDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public TypedQuery<PaymentEntity> getpaymentMode(){
        return paymentDao.getPaymentName();
    }
}
