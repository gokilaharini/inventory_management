package com.inventory.dao;

import com.inventory.model.Pack;
import com.inventory.model.Ship;
import com.inventory.model.shipItem;
import com.inventory.config.databaseConfig;
import com.inventory.exception.databaseException;
import com.inventory.dao.packageDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class shipmentDAO {

    private final packageDAO packageDAO;

    public shipmentDAO(){
        this.packageDAO=new packageDAO();
    }
    public Ship createShipment(Ship ship) {

        validateShipment(ship);

        String sql = """
                INSERT INTO shipments
                (net_quantity,shipment_date,status,container_id,delivery_charges,volume)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String sql2= """
                INSERT INTO shipitems
                (shipment_id,package_id)
                VALUES (?, ?)
                """;

        String sql3= """
                SELECT d_height,
                d_width,
                d_length
                
                FROM package
                WHERE package_id=?
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement query=connection.prepareStatement(sql3);
        ) {
            int tot_volume=0;

            for(shipItem item:ship.getItems()){
                query.setInt(1,item.getPackage_id());
                ResultSet res=query.executeQuery();
                while(res.next()) {
                    int volume = res.getInt("d_height") * res.getInt("d_width") * res.getInt("d_length");
                    tot_volume += volume;
                }
            }
            int delivery=(tot_volume/5000)*20;
            ship.setVolume(tot_volume);
            ship.setDelivery_charges(delivery);
            statement.setInt(1, ship.getNet_quantity());
            statement.setString(2, ship.getShipment_date());
            statement.setString(3, ship.getStatus());
            statement.setInt(4, ship.getContainer_id());
            statement.setInt(5,ship.getDelivery_charges());
            statement.setInt(6,ship.getVolume());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    ship.setShipment_id(keys.getInt(1));
                }
            }
            try(
                    PreparedStatement stm=connection.prepareStatement(sql2)
            ){
                for(shipItem item:ship.getItems()){
                    stm.setInt(1,ship.getShipment_id());
                    stm.setInt(2,item.getPackage_id());

                    stm.executeUpdate();

                    packageDAO.updatePackage(item.getPackage_id());
                }
            }
            catch (SQLException e){
                throw new databaseException("Failed to create packages Id", e);
            }

            return ship;

        } catch (SQLException e) {
            throw new databaseException("Failed to create shipment", e);
        }
    }

    public Ship findShipmentById(int shipId) {
        String sql = """
                SELECT shipment_id,
                       net_quantity,
                       shipment_date,
                       status,
                       container_id,
                       delivery_charges,
                       volume
                
                FROM shipments
                WHERE shipment_id = ?
                """;

        String sql2= """
                SELECT package_id
                
                FROM shipitems
                WHERE shipment_id=?
                """;

        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm = connection.prepareStatement(sql2)
        ) {
            statement.setInt(1, shipId);
            stm.setInt(1,shipId);
            try (ResultSet result=statement.executeQuery();
            ResultSet res=stm.executeQuery()
            ) {
                if(result.next()) {
                    Ship ship= mapResultSet(result);
                    List<shipItem> items=new ArrayList<>();
                    while(res.next()){
                        items.add(mapShipItemSet(res));
                    }
                    ship.setItems(items);
                    return ship;
                }
                return null;
            }

        } catch (SQLException e) {
            throw new databaseException("Failed to find shipment", e);
        }
    }

    public List<Ship> findAllShipments() {

        String sql = """
                SELECT shipment_id,
                       net_quantity,
                       shipment_date,
                       status,
                       container_id,
                       delivery_charges,
                       volume
                
                FROM shipments
                ORDER BY shipment_id
                """;

        String sql2= """
                SELECT package_id
                
                FROM shipitems
                WHERE shipment_id=?
                """;
        List<Ship> ships=new ArrayList<>();
        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql);
                PreparedStatement stm=connection.prepareStatement(sql2);
                ResultSet result =statement.executeQuery()
        ) {

            while (result.next()) {
                Ship ship=mapResultSet(result);
                List<shipItem> items=new ArrayList<>();
                stm.setInt(1,ship.getShipment_id());
                ResultSet res=stm.executeQuery();
                while(res.next()){
                    items.add(mapShipItemSet(res));
                }
                ship.setItems(items);
                ships.add(ship);
            }

            return ships;

        } catch (SQLException e) {
            throw new databaseException("Failed to fetch shipment details", e);
        }
    }

    public Ship updateShipment(Ship ship) {
        String sql = """
                UPDATE shipments
                SET status=?
                WHERE shipment_id = ?
                """;
        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, ship.getStatus());
            statement.setInt(2,ship.getShipment_id());

            int updated = statement.executeUpdate();

            if (updated == 0) {
                return null;
            }

            return ship;

        } catch (SQLException e) {
            throw new databaseException("Failed to update shipment", e);
        }
    }

    public boolean deleteShipment(int shipId) {
        String sql = """
                DELETE FROM shipments
                WHERE shipment_id = ?
                """;

        String sql2= """
                DELETE FROM shipitems
                WHERE shipment_id = ?
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm= connection.prepareStatement(sql2)
        ) {
            statement.setInt(1, shipId);
            stm.setInt(1,shipId);
            return statement.executeUpdate() > 0 && stm.executeUpdate()>0 ;

        } catch (SQLException e) {
            throw new databaseException("Failed to delete Shipment", e);
        }
    }

    public void validateShipment(Ship ship){
        String sql= """
                SELECT 1
                FROM package
                WHERE package_id=? and status="packed"
                """;

        try(
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
            ){
            for(shipItem item:ship.getItems()){
                statement.setInt(1,item.getPackage_id());
                ResultSet res=statement.executeQuery();
                if(!res.next()){
                    throw new IllegalArgumentException("the package_id does not exist or it is already shipped");
                }
            }
        } catch (SQLException e) {
            throw new databaseException("couldn't validate shipment details",e);
        }
    }

    private Ship mapResultSet(ResultSet result) throws SQLException {

        Ship ship=new Ship();

        ship.setShipment_id(result.getInt("shipment_id"));
        ship.setNet_quantity(result.getInt("net_quantity"));
        ship.setShipment_date(result.getString("shipment_date"));
        ship.setStatus(result.getString("status"));
        ship.setContainer_id(result.getInt("container_id"));
        ship.setDelivery_charges(result.getInt("delivery_charges"));
        ship.setVolume(result.getInt("volume"));

        return ship;
    }

    private shipItem mapShipItemSet(ResultSet res) throws SQLException {

        shipItem item=new shipItem();

        item.setPackage_id(res.getInt("package_id"));

        return item;
    }
}
