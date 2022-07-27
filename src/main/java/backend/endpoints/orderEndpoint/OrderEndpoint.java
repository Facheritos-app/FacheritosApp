package backend.endpoints.orderEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.orderDTO.OrderDTO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OrderEndpoint {

    /**
     * getOrdersForTableView: void -> Map<Boolean, ResultSet>
     * Purpose: this method obtains orders data from the database to fill the orders table.
     */
    public Map<Boolean, ResultSet> getOrdersForTableView() {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM\n" +
                            "(SELECT (person.first_name || ' ' || person.last_name) AS worker_name, worker.id_worker\n" +
                            "FROM worker JOIN person USING(id_person)) AS worker_data\n" +
                            "JOIN (SELECT job_order.id_job_order, job_order.id_worker, headquarter.name AS seat, person.cc, \n" +
                            "job_order.due_date, state.name AS status FROM person JOIN job_order \n" +
                            "ON person.id_person = job_order.id_customer JOIN state USING(id_state) JOIN headquarter \n" +
                            "USING(id_headquarter)) AS order_data USING(id_worker)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getOrderById: Integer -> Map<Boolean, ResultSet>
     * Purpose: gets an order from the database according to an input ID.
     */
    public Map<Boolean, ResultSet> getOrderById(Integer idOrder) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "SELECT job_order.id_job_order, person.id_person,(person.first_name || ' ' || person.last_name) \n" +
                            "AS person_name, person.cc,  person.cellphone, headquarter.id_headquarter, headquarter.name \n" +
                            "AS headquarter_name,job_order.created_at, job_order.due_date, job_order.price, job_order.id_state,\n" +
                            "state.name AS status \n" +
                            "FROM person \n" +
                            "JOIN job_order ON person.id_person = job_order.id_customer\n" +
                            "JOIN state USING(id_state) \n" +
                            "JOIN headquarter\n" +
                            "USING(id_headquarter) \n" +
                            "WHERE id_job_order = ?");
            preparedStatement.setInt(1, idOrder);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getOrderParts: Integer -> Map<Boolean, ResultSet>
     * Purpose: obtains the parts of an order from the database.
     */
    public Map<Boolean, ResultSet> getOrderParts(Integer idOrder) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "SELECT part.name, part.price,\n" +
                            "job_order_part.quantity, part.id_part\n" +
                            "FROM job_order\n" +
                            "JOIN job_order_part USING(id_job_order)\n" +
                            "JOIN part USING (id_part)\n" +
                            "WHERE id_job_order = ?");
            preparedStatement.setInt(1, idOrder);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getPartsFromInventory: Integer -> Map<Boolean, ResultSet>
     * Purpose: obtains the parts inventory from the database.
     */
    public Map<Boolean, ResultSet> getPartsFromInventory(Integer idHeadquarter) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("" +
                    "SELECT *, headquarter.name AS hq \n" +
                    "FROM part \n" +
                    "JOIN part_inventory USING(id_part)\n" +
                    "JOIN headquarter USING(id_headquarter)\n" +
                    "JOIN city USING(id_city) \n" +
                    "WHERE part.status = 'Activo' AND id_headquarter = ?");
            preparedStatement.setInt(1, idHeadquarter);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getTotalPriceAndQuantity: Integer -> Map<Boolean, ResultSet>
     * Purpose: gets the total price and the quantity of parts of an order from the database
     * according to an input ID.
     */
    public Map<Boolean, ResultSet> getTotalPriceAndQuantity(Integer idOrder) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "SELECT (sum(job_order_part.quantity*part.price)+job_order.price) AS total,\n" +
                            "sum(job_order_part.quantity) AS quantity\n" +
                            "FROM job_order\n" +
                            "JOIN job_order_part USING(id_job_order)\n" +
                            "JOIN part USING (id_part)\n" +
                            "WHERE id_job_order = ?\n" +
                            "GROUP BY(id_job_order)");
            preparedStatement.setInt(1, idOrder);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * updateOrder: OrderDTO -> void
     * Purpose: updates a order
     */
    public void updateOrder(OrderDTO orderDTO) {
        PreparedStatement preparedStatement;
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "UPDATE job_order \n" +
                            "SET id_customer = ?, id_state = ?, due_date = ?, price = ?\n" +
                            "WHERE id_job_order = ?");
            preparedStatement.setInt(1, orderDTO.getId_customer());
            preparedStatement.setInt(2, orderDTO.getId_status());
            preparedStatement.setDate(3, orderDTO.getDue_date());
            preparedStatement.setDouble(4, orderDTO.getPrice());
            preparedStatement.setInt(5, orderDTO.getId_order());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * updateParts: Integer, Integer, Integer -> void
     * Purpose: updates the parts in the inventory
     */
    public void updatePart(Integer idPart, Integer idHeadquarter, Integer quantity) {
        PreparedStatement preparedStatement;
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "UPDATE part_inventory \n" +
                            "SET quantity = ?\n" +
                            "WHERE (id_part = ?  AND id_headquarter = ?)");
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, idPart);
            preparedStatement.setInt(3, idHeadquarter);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * removeParts: Integer -> void
     * Purpose: remove the parts of an order
     */
    public void removeParts(Integer idOrder) {
        PreparedStatement preparedStatement;
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "DELETE FROM job_order_part WHERE id_job_order = ?");
            preparedStatement.setInt(1, idOrder);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * addParts: Integer, Integer, Integer -> void
     * Purpose: add parts to an order
     */
    public void addPart(Integer idPart, Integer idJobOrder, Integer quantity) {
        PreparedStatement preparedStatement;
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "INSERT INTO job_order_part VALUES(?, ?, ?);");
            preparedStatement.setInt(1, idJobOrder);
            preparedStatement.setInt(2, idPart);
            preparedStatement.setInt(3, quantity);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * getPartQuantity: Integer -> Map<Boolean, ResultSet>
     * Purpose: gets the quantity of a part from the inventory
     */
    public Integer getPartQuantity(Integer idPart, Integer idHeadquarter) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement(
                    "SELECT quantity FROM part_inventory WHERE id_part = ? AND id_headquarter = ?");
            preparedStatement.setInt(1, idPart);
            preparedStatement.setInt(2, idHeadquarter);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
            return 0;
        }
        return resultSet.getInt("quantity");
    }



}