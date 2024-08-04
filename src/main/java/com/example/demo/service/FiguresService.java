package com.example.demo.service;

import com.example.demo.dto.response.BestSellingProducts;
import com.example.demo.dto.response.FiguresResponse;
import com.example.demo.dto.response.WeeklyOrders;
import com.example.demo.model.Category;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.ProductItem;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FiguresService {

    OrderRepository orderRepository;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    public FiguresResponse getData() {
        FiguresResponse figuresResponse = new FiguresResponse();
        List<Order> allOrders = getAllOrders();

        figuresResponse.setTotalOrders(countOrders(allOrders));
        figuresResponse.setPendingOrders(countOrdersByStatus(allOrders, "chờ"));
        figuresResponse.setDeliveringOrders(countOrdersByStatus(allOrders, "đang giao hàng"));
        figuresResponse.setDeliveredOrders(countOrdersByStatus(allOrders, "đã giao hàng"));
        figuresResponse.setReceivedOrders(countOrdersByStatus(allOrders, "đã nhận hàng"));
        figuresResponse.setCancelOrders(countOrdersByStatus(allOrders, "hủy"));
        figuresResponse.setTodayOrders(countOrdersCreatedToday());
        figuresResponse.setYesterdayOrders(countOrdersCreatedOnDay(allOrders, -1));
        figuresResponse.setThisMonthOrders(countOrdersForMonth(allOrders, Calendar.getInstance().get(Calendar.MONTH)));
        figuresResponse.setLastMonthOrders(countOrdersForMonth(allOrders, Calendar.getInstance().get(Calendar.MONTH) - 1));
        figuresResponse.setWeeklyOrders(countOrdersByDay());
        figuresResponse.setBestSellingProducts(getBestSellingProducts());
        return figuresResponse;
    }

    public List<BestSellingProducts> getBestSellingProducts() {
        List<Order> orders = orderRepository.findAll();
        List<Product> products = new ArrayList<>();
        for (Order o : orders) {
            List<ProductItem> productItems = o.getProducts();
            for (ProductItem p : productItems) {
                productRepository.findById(p.getProduct()).ifPresent(products::add);
            }
        }
        Map<ObjectId, Long> categoryCountMap = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        Optional<Map.Entry<ObjectId, Long>> mostFrequentCategory = categoryCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostFrequentCategory.isPresent()) {
            Map.Entry<ObjectId, Long> entry = mostFrequentCategory.get();
            ObjectId categoryId = entry.getKey();
            long count = entry.getValue();

            List<BestSellingProducts> bestSellingList = new ArrayList<>();
            categoryRepository.findById(categoryId.toString()).ifPresent(category -> {
                bestSellingList.add(BestSellingProducts.builder()
                        ._id(Collections.singletonList(category))
                        .total(count)
                        .build());
            });
            return bestSellingList;

        } else {
            System.out.println("No category found.");
        }
        return Collections.emptyList();
    }

    private List<Date> getLast7Days() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dates.add(getStartOfDay(calendar.getTime()));
        }

        return dates;
    }

    public List<WeeklyOrders> countOrdersByDay() {
        List<WeeklyOrders> orderCounts = new ArrayList<>();
        List<Date> dates = getLast7Days();
        List<Order> orders = orderRepository.findAll();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        for (Date day : dates) {
            Date startOfDay = day;
            Date endOfDay = getEndOfDay(startOfDay);
            long count = orders.stream()
                    .filter(order -> isDateInRange(order.getCreatedAt(), startOfDay, endOfDay))
                    .count();

            String formattedDate = formatter.format(day);

            WeeklyOrders weeklyOrders = WeeklyOrders.builder()
                    ._id(formattedDate)
                    .totalOrders(count)
                    .build();

            orderCounts.add(weeklyOrders);
        }

        return orderCounts;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public long countOrders(List<Order> orders) {
        return orders.size();
    }

    public long countOrdersByStatus(List<Order> orders, String status) {
        return orders.stream()
                .filter(order -> status.equals(order.getStatus()))
                .count();
    }

    public long countOrdersCreatedToday() {
        Date startOfDay = getStartOfDay(new Date());
        Date endOfDay = getEndOfDay(new Date());
        return countOrdersCreatedOnDay(getAllOrders(), startOfDay, endOfDay);
    }

    public long countOrdersCreatedOnDay(List<Order> orders, Date startOfDay, Date endOfDay) {
        return orders.stream()
                .filter(order -> isDateInRange(order.getCreatedAt(), startOfDay, endOfDay))
                .count();
    }

    public long countOrdersCreatedOnDay(List<Order> orders, int daysOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, daysOffset);
        Date startOfDay = getStartOfDay(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = getStartOfDay(calendar.getTime());
        return orders.stream()
                .filter(order -> isDateInRange(order.getCreatedAt(), startOfDay, endOfDay))
                .count();
    }

    public long countOrdersForMonth(List<Order> orders, int monthOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthOffset);
        Date startOfMonth = getStartOfMonth(calendar.getTime());
        Date endOfMonth = getEndOfMonth(calendar.getTime());
        return orders.stream()
                .filter(order -> isDateInRange(order.getCreatedAt(), startOfMonth, endOfMonth))
                .count();
    }

    public long countOrdersForLast7Days(List<Order> orders) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date startOfPeriod = getStartOfDay(calendar.getTime());
        Date endOfPeriod = new Date();
        return orders.stream()
                .filter(order -> isDateInRange(order.getCreatedAt(), startOfPeriod, endOfPeriod))
                .count();
    }

    private boolean isDateInRange(Date date, Date startDate, Date endDate) {
        return date != null && !date.before(startDate) && !date.after(endDate);
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
