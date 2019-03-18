package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.OrderBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")

public class OrderController {

    @Autowired
    OrderBusinessService orderBusinessService;

    /**
     * A controller method to fetch details of the coupon based on coupon name.
     *
     * @param coupon_name    - The name of the coupon whose details are to be fetched from the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<CouponDetailsResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws CouponNotFoundException
     */
    @RequestMapping(method= RequestMethod.GET, path = "/order/coupon/{coupon_name}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@RequestHeader("authorization") final String authorization, @PathVariable String coupon_name) throws AuthorizationFailedException, CouponNotFoundException {
        CouponEntity couponEntity = orderBusinessService.getCouponByName(authorization, coupon_name);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse().id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName()).percent(couponEntity.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse,HttpStatus.OK);
    }

    /**
    * A controller method to fetch details of the past orders of the user.
    */

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CustomerOrderResponse>> getPastOrdersForCustomer(@RequestHeader("authorization") final String authorization)throws AuthorizationFailedException{

        List<OrdersEntity> ordersEntity = orderBusinessService.getPastOrdersForCustomer(authorization).getResultList();

        OrderList orderList1 = new OrderList();
        OrderListCoupon orderListCoupon = new OrderListCoupon();
        OrderListPayment orderListPayment = new OrderListPayment();
        OrderListCustomer orderListCustomer = new OrderListCustomer();
        OrderListAddress orderListAddress = new OrderListAddress();
        OrderListAddressState statesList = new OrderListAddressState();


        List<OrderItemEntity> orderItemEntity = new ArrayList<>();
        ItemQuantityResponse itemQuantityResponses = new ItemQuantityResponse();
        ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
        List<ItemQuantityResponse> itemQuantityResponses1 = new ArrayList<>();


        for (OrdersEntity ordersEntity1 : ordersEntity){

            orderList1.setId(UUID.fromString(ordersEntity1.getUuid()));
            orderList1.setBill(BigDecimal.valueOf(ordersEntity1.getBill()));
            orderListCoupon.setCouponName(ordersEntity1.getCouponId().getCouponName());
            orderListCoupon.setId(UUID.fromString(ordersEntity1.getCouponId().getUuid()));
            orderListCoupon.setPercent(ordersEntity1.getCouponId().getPercent());
            orderList1.setCoupon(orderListCoupon);

            orderList1.setDiscount(BigDecimal.valueOf(ordersEntity1.getDiscount()));
            orderList1.setDate(String.valueOf(ordersEntity1.getDate()));

            orderListPayment.setId(UUID.fromString(ordersEntity1.getPaymentId().getUuid()));
            orderListPayment.setPaymentName(ordersEntity1.getPaymentId().getPaymentName());
            orderList1.setPayment(orderListPayment);

            orderListCustomer.setId(UUID.fromString(ordersEntity1.getCustomerId().getUuid()));
            orderListCustomer.setFirstName(ordersEntity1.getCustomerId().getFirstname());
            orderListCustomer.setLastName(ordersEntity1.getCustomerId().getLastname());
            orderListCustomer.setEmailAddress(ordersEntity1.getCustomerId().getEmail());
            orderListCustomer.setContactNumber(ordersEntity1.getCustomerId().getContactNumber());
            orderList1.setCustomer(orderListCustomer);

            orderListAddress.setId(ordersEntity1.getAddressId().getUuid());
            orderListAddress.setFlatBuildingName(ordersEntity1.getAddressId().getFlatBuilNumber());
            orderListAddress.setLocality(ordersEntity1.getAddressId().getLocality());
            orderListAddress.setCity(ordersEntity1.getAddressId().getCity());
            orderListAddress.setPincode(ordersEntity1.getAddressId().getPincode());
            statesList.setId(UUID.fromString(ordersEntity1.getAddressId().getState().getUuid()));
            statesList.setStateName(ordersEntity1.getAddressId().getState().getStateName());
            orderListAddress.setState(statesList);
            orderList1.setAddress(orderListAddress);

            orderItemEntity = orderBusinessService.getItemByOrder(ordersEntity1);
            for (OrderItemEntity orderItemEntity1: orderItemEntity){
                itemQuantityResponseItem.setId(UUID.fromString(orderItemEntity1.getItemId().getUuid()));
                itemQuantityResponseItem.setItemName(orderItemEntity1.getItemId().getItemName());
                itemQuantityResponseItem.setItemPrice(orderItemEntity1.getItemId().getPrice());
                itemQuantityResponseItem.setType(ItemQuantityResponseItem.TypeEnum.fromValue(orderItemEntity1.getItemId().getType()));
                itemQuantityResponses.setItem(itemQuantityResponseItem);
                itemQuantityResponses.setQuantity(orderItemEntity1.getQuantity());
                itemQuantityResponses.setPrice(orderItemEntity1.getPrice());
            }

            itemQuantityResponses1.add(itemQuantityResponses);
            orderList1.setItemQuantities(itemQuantityResponses1);
        }

        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse().addOrdersItem(orderList1);
        List<CustomerOrderResponse> customerOrderResponses1 = new ArrayList<>();
        customerOrderResponses1.add(customerOrderResponse);

        return new ResponseEntity<List<CustomerOrderResponse>>(customerOrderResponses1,HttpStatus.OK);
    }

    @RequestMapping(method= RequestMethod.POST, path = "/order")
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization, SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, CouponNotFoundException, PaymentMethodNotFoundException, AddressNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

       List<UUID> orderItemsUuid = new ArrayList<>();
       Integer billAmount = 0;
       for(ItemQuantity item : saveOrderRequest.getItemQuantities()){
           orderItemsUuid.add(item.getItemId());
           billAmount = billAmount+(item.getPrice()*item.getQuantity());
       }
        OrdersEntity response =  orderBusinessService.saveOrder(authorization,saveOrderRequest.getPaymentId(),orderItemsUuid, billAmount, saveOrderRequest.getCouponId(), saveOrderRequest.getAddressId(), saveOrderRequest.getRestaurantId());
       SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
       saveOrderResponse.status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>((SaveOrderResponse) saveOrderResponse,HttpStatus.OK);
    }

}
