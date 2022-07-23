package backend.endpoints.orderEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.orderDTO.OrderDTO;

import java.sql.*;
import java.time.LocalDate;
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
                    "SELECT part.name, part.price, job_order_part.quantity, part.id_part\n" +
                            "FROM job_order \n" +
                            "JOIN job_order_part USING(id_job_order) \n" +
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
                            "SET id_customer = ?, id_headquarter = ?, id_state = ?, due_date = ?, price = ?\n" +
                            "WHERE id_job_order = ?");
            preparedStatement.setInt(1, orderDTO.getId_customer());
            preparedStatement.setInt(2, orderDTO.getId_headquarter());
            preparedStatement.setInt(3, orderDTO.getId_status());
            preparedStatement.setDate(4, orderDTO.getDue_date());
            preparedStatement.setDouble(5, orderDTO.getPrice());
            preparedStatement.setInt(6, orderDTO.getId_order());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}