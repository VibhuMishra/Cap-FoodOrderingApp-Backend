package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.PaymentBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")

/**
 * A controller method to fetch all the payment methods.
 * @return - ResponseEntity<List<PaymentResponse>> type object along with Http status OK.
 */

public class PaymentController {

    @Autowired
    private PaymentBusinessService paymentBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PaymentResponse>> getPaymentMethods(){

        List<PaymentEntity> paymentEntityList = paymentBusinessService.getpaymentMode().getResultList();

        List<PaymentResponse> paymentResponseList = new ArrayList<>();

        for(PaymentEntity paymentEntity : paymentEntityList){
            paymentResponseList.add(new PaymentResponse().id(UUID.fromString(paymentEntity.getUuid())).paymentName(paymentEntity.getPaymentName()));
        }
        return new ResponseEntity<List<PaymentResponse>>(paymentResponseList,HttpStatus.OK);
    }
}
