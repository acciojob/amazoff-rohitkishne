package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    HashMap<String, Order> orderDB;
    HashMap<String, DeliveryPartner> partnerDB;
    HashMap<String, List<String>> orderToPartnerDB;
    HashMap<String,String> assignedOrderDB;

    public OrderRepository() {
        this.orderDB = new HashMap<>();
        this.partnerDB = new HashMap<>();
        this.orderToPartnerDB = new HashMap<>();
        this.assignedOrderDB = new HashMap<>();
    }

    public void addOrder(Order order)
    {
        String id = order.getId();
        orderDB.put(id, order);
    }

    public void addPartner(String partnerId)
    {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerDB.put(partnerId, partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){

        //check whether partnerId have order or not;

        List<String> order = new ArrayList<>();

        if(orderToPartnerDB.containsKey(partnerId))
        {
            order = orderToPartnerDB.get(partnerId);
        }

        order.add(orderId);
        orderToPartnerDB.put(partnerId, order);

        //Now order has been assigned
        assignedOrderDB.put(orderId, partnerId);
    }

    public Order getOrderById(String orderId)
    {
        Order order = orderDB.get(orderId);
        return order;
    }
    public DeliveryPartner getPartnerById(String partnerId)
    {
        DeliveryPartner deliveryPartner = partnerDB.get(partnerId);
        return deliveryPartner;
    }

    public Integer getOrderCountByPartnerId(String partnerId)
    {
        Integer numberOfOrders = orderToPartnerDB.get(partnerId).size();
        return numberOfOrders;
    }

    public List<String> getOrdersByPartnerId(String partnerId)
    {
        //contains all orders in a list
        List<String> getOrder = orderToPartnerDB.get(partnerId);

        return getOrder;
    }

    public List<String> getAllOrders()
    {
        //create a list to get all order
        List<String> orders = new ArrayList<>();

        for(String order : orderDB.keySet())
        {
            orders.add(order);
        }

        return orders;
    }

    public Integer getCountOfUnassignedOrders(){

        Integer unassignedOrder = 0;

        for(String order : orderDB.keySet())
        {
            if(!assignedOrderDB.containsKey(order))
            {
                unassignedOrder++;
            }
        }

        return unassignedOrder;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){

        int timeOfOrder = (Integer.parseInt(time.substring(0,2)) * 60) + Integer.parseInt(time.substring(3));
        List<String> orders = orderToPartnerDB.get(partnerId);

        Integer lateOrder = 0;

        for(String order : orders)
        {
            int orderTime =  orderDB.get(order).getDeliveryTime();

            if(orderTime > timeOfOrder)
            {
                lateOrder++;
            }
        }

        return lateOrder;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId)
    {
        int time = 0;

        List<String> orders = orderToPartnerDB.get(partnerId);

        for(String order : orders)
        {
            int orderTime = orderDB.get(order).getDeliveryTime();

            if(orderTime > time)
            {
                time = orderTime;
            }
        }

        int hour = time/60;
        int minute = time - hour*60;

        String lastOrderTime = "";

        if(hour<=9)
        {
            lastOrderTime = lastOrderTime + "0"+hour+":";
        }
        else
        {
            lastOrderTime = lastOrderTime + hour+":";
        }

        if(minute<=9)
        {
            lastOrderTime = lastOrderTime + "0" +minute;
        }
        else
        {
            lastOrderTime = lastOrderTime + minute;
        }

        return lastOrderTime;
    }

    public void deletePartnerById(String partnerId)
    {
        List<String> orders = orderToPartnerDB.get(partnerId);

        for(String order : orders)
        {
            assignedOrderDB.remove(order);
        }

        orderToPartnerDB.remove(partnerId);
    }

    public void deleteOrderById(String orderId)
    {
        //delete from orders
        orderDB.remove(orderId);

        //get partner id from assigned orders, if present
        if(assignedOrderDB.containsKey(orderId))
        {
            String partnerId = assignedOrderDB.get(orderId);

            //delete from assigned orders
            assignedOrderDB.remove(orderId);

            //get the order by partnerId
            List<String> orders = orderToPartnerDB.get(partnerId);

            for(String order : orders)
            {
                if(order.equals(orderId))
                {
                    orders.remove(order);
                    return;
                }
            }
        }

    }
}
