package com.example.demo.repository;

import com.example.demo.dto.response.WeeklyOrders;
import com.example.demo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<WeeklyOrders> findOrdersForLast7Days() {
        Date startOfPeriod = getStartOfLast7Days();
        Date endOfPeriod = new Date();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("createdAt").gte(startOfPeriod).lt(endOfPeriod)),
                Aggregation.project("createdAt")
                        .andExpression("dateToString('%Y-%m-%d', createdAt)").as("_id"),
                Aggregation.group("_id").count().as("totalOrders"),
                Aggregation.sort(Sort.Direction.ASC, "_id")
        );

        AggregationResults<WeeklyOrders> results = mongoTemplate.aggregate(aggregation, Order.class, WeeklyOrders.class);
        return results.getMappedResults();
    }

    private Date getStartOfLast7Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6); // Start from 6 days ago
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
