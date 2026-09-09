package com.inventory.dao;

import com.inventory.config.databaseConfig;
import com.inventory.exception.databaseException;
import com.inventory.model.Sales;
import com.inventory.model.orderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class salesOrderDAO {

    private final ItemDAO ItemDAO;

    public salesOrderDAO(){
        this.ItemDAO=new ItemDAO();
    }
    public Sales createOrder(Sales order) {

        validateOrder(order);

        String sql = """
                INSERT INTO salesorder
                (customer_name,order_date,expected_ship_date,address,salesperson,total_amount,payment,status,order_type)
                VALUES (?, ?, ?, ?, ?, ?,?,?,?)
                """;

        String sql1 = """
                INSERT INTO orderitems
                (order_id,item_id,quant_ordered,quant_issued,amount)
                VALUES (?, ?, ?, ?, ?)
                """;


        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setString(1, order.getCustomer_name());
            statement.setString(2, order.getOrder_date());
            statement.setString(3, order.getShipment_date());
            statement.setString(4, order.getAddress());
            statement.setString(5, order.getSales_person());
            statement.setInt(6, order.getTotal_amount());
            statement.setString(7,order.getPayment_status());
            if((order.getPayment_status()).equals("yes")){
                order.setStatus("completed");
            }
            else{
                order.setStatus("confirmed");
            }
            statement.setString(8,order.getStatus());
            statement.setString(9,order.getOrder_type());

            statement.executeUpdate();

            try (ResultSet keys=statement.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setSalesOrder_id(keys.getInt(1));
                }
            }
            if(!(order.getOrder_type()).equals("services")) {
                try (
                        PreparedStatement stm = connection.prepareStatement(sql1)
                ) {
                    for (orderItem item : order.getItems()) {
                        stm.setInt(1, order.getSalesOrder_id());
                        stm.setInt(2, item.getItem_id());
                        stm.setInt(3, item.getQuantity());
                        stm.setInt(4, 0);
                        stm.setInt(5, item.getAmount());

                        stm.executeUpdate();

                        ItemDAO.updateStock(item.getItem_id(), item.getQuantity(), "salesorder");
                    }
                } catch (SQLException e) {
                    throw new databaseException("Failed to create item", e);
                }
            }
            return order;

        } catch (SQLException e) {
            throw new databaseException("Failed to sales order", e);
        }
    }

    public Sales findOrderById(int orderId) {

        String sql = """
                SELECT order_id,
                       customer_name,
                       order_date,
                       expected_ship_date,
                       address,
                       salesperson,
                       total_amount,
                       payment,
                       status,
                       order_type
                
                FROM salesorder
                WHERE order_id = ?
                """;

        String sql2 = """
                SELECT item_id,
                quant_ordered,
                quant_issued,
                amount
                
                FROM orderitems
                WHERE order_id=?
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm = connection.prepareStatement(sql2)
        ) {
            statement.setInt(1, orderId);
            stm.setInt(1,orderId);
            try (
                    ResultSet result=statement.executeQuery();
                    ResultSet res=stm.executeQuery()
            ) {
                if(result.next()) {
                    Sales sales= mapResultSet(result);
                    List<orderItem> items=new ArrayList<>();
                    while(res.next()){
                        items.add(mapItemSet(res));
                    }
                    sales.setItems(items);
                    return sales;
                }
                return null;
            }

        } catch (SQLException e) {
            throw new databaseException("Failed to find sales order", e);
        }
    }


    public List<Sales> findAllOrders() {

        String sql = """
                SELECT order_id,
                       customer_name,
                       order_date,
                       expected_ship_date,
                       address,
                       salesperson,
                       total_amount,
                       payment,
                       status,
                       order_type
                
                FROM salesorder
                ORDER BY order_id
                """;

        String sql2 = """
                SELECT item_id,
                quant_ordered,
                quant_issued,
                amount
                
                FROM orderitems
                WHERE order_id=?
                """;

        List<Sales> orders = new ArrayList<>();
        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql);
                PreparedStatement stm=connection.prepareStatement(sql2);
                ResultSet result=statement.executeQuery();
        ) {
            while (result.next()) {
                Sales sales= mapResultSet(result);
                List<orderItem> items=new ArrayList<>();
                stm.setInt(1,sales.getSalesOrder_id());
                ResultSet res=stm.executeQuery();
                while(res.next()){
                    items.add(mapItemSet(res));
                }
                sales.setItems(items);
                orders.add(sales);
            }
            return orders;

        } catch (SQLException e) {
            throw new databaseException("Failed to fetch sales orders", e);
        }
    }

    public Sales updateorder(Sales order) {
        String sql = """
                UPDATE salesorder
                SET payment=?,
                    status=? 
                WHERE order_id = ?
                """;
        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, order.getPayment_status());
            statement.setString(2, order.getStatus());
            statement.setInt(3,order.getSalesOrder_id());
            int updated = statement.executeUpdate();
            if (updated == 0) {
                return null;
            }
            return order;

        } catch (SQLException e) {
            throw new databaseException("Failed to update item", e);
        }
    }

    public boolean deleteorder(int orderId) {

        String sql = """
                DELETE FROM salesorder
                WHERE order_id = ?
                """;

        String sql2= """
                DELETE FROM orderitems
                WHERE order_id=?
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm=connection.prepareStatement(sql2)
        ) {
            statement.setInt(1, orderId);
            stm.setInt(1,orderId);

            return statement.executeUpdate() > 0 && stm.executeUpdate()>0 ;

        } catch (SQLException e) {
            throw new databaseException("Failed to delete item", e);
        }
    }

    private void validateOrder(Sales sales){
        if((sales.getOrder_type()).equals("services")){
            return;
        }
        String sql= """
                SELECT 1
                FROM items
                WHERE item_id=?
                """;

        try(
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
            ){
            for(orderItem item: sales.getItems()) {
                statement.setInt(1, item.getItem_id());
                ResultSet res = statement.executeQuery();
                if (res.next()) {
                    continue;
                }

                else {
                    throw new IllegalArgumentException("item_id does not exist");
                }
            }
        }
        catch (SQLException e){
            throw new databaseException("couldn't validate item_id",e);
        }
    }


    public Sales mapResultSet(ResultSet result) throws SQLException{
        Sales sales=new Sales();

        sales.setSalesOrder_id(result.getInt("order_id"));
        sales.setCustomer_name(result.getString("customer_name"));
        sales.setOrder_date(result.getString("order_date"));
        sales.setShipment_date(result.getString("expected_ship_date"));
        sales.setAddress(result.getString("address"));
        sales.setSales_person(result.getString("salesperson"));
        sales.setTotal_amount(result.getInt("total_amount"));
        sales.setPayment_status(result.getString("payment"));
        sales.setStatus(result.getString("status"));
        sales.setOrder_type(result.getString("order_type"));

        return sales;
    }

    public orderItem mapItemSet(ResultSet res) throws SQLException{

        orderItem item=new orderItem();

        item.setItem_id(res.getInt("item_id"));
        item.setQuantity(res.getInt("quant_ordered"));
        item.setQuant_issued(res.getInt("quant_issued"));
        item.setAmount(res.getInt("amount"));

        return item;
    }

}
