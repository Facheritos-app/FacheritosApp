package backend.endpoints.dashboardEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SellerDashboardEndpoint {

    /**
     * amountOfSalesPerMonthChart: void -> Map<Boolean, Integer>
     * Purpose: This method connects to the DB and returns the total number of sales per month, two columns
     */
    public Map<Boolean, ResultSet> amountOfSalesChart(String category, Integer year) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            if (category.equals("year")) {
                preparedStatement = conn.prepareStatement("SELECT date_part(?, sale_date) AS cat, COUNT(*) AS total FROM sale GROUP BY cat ");
                preparedStatement.setString(1, category);
            } else if (category.equals("month")) {
                preparedStatement = conn.prepareStatement("SELECT date_part(?, sale_date) AS cat, COUNT(*) AS total FROM sale WHERE date_part('year', sale_date) = ? GROUP BY cat ");
                preparedStatement.setString(1, category);
                preparedStatement.setInt(2, year);
            }
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * customersChart: Integer x3 -> Map<Boolean, ResultSet>
     * Purpose: This method connects to the DB and returns the data for the customers chart
     */
    public Map<Boolean, ResultSet> customersChart(Integer selectionType, Integer year, Integer id) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            //If the yAxis has the amount of sales
            if(selectionType.equals(0)) {
                //If the id is the id number (cc)
                if(id == 0) {
                    preparedStatement = conn.prepareStatement("SELECT cc AS data,COUNT(*) AS total FROM sale " +
                            "JOIN person ON person.id_person=sale.id_customer " +
                            "WHERE DATE_PART('year', sale_date) = ? GROUP BY data ORDER BY total DESC LIMIT 10");
                }
                //if the id is the name of the customer
                else if(id == 1) {
                    preparedStatement = conn.prepareStatement("SELECT last_name AS data, COUNT(*) AS total FROM sale " +
                            "JOIN person ON person.id_person=sale.id_customer " +
                            "WHERE DATE_PART('year', sale_date) = ? GROUP BY data ORDER BY total DESC LIMIT 10");
                }
                preparedStatement.setInt(1, year);
            }
            //If the yAxis has the total (price) sold
            else if(selectionType.equals(1)) {
                //If the id is the id number (cc)
                if(id == 0){
                    preparedStatement = conn.prepareStatement("SELECT cc AS data, SUM(price) AS total FROM sale " +
                            " JOIN person ON person.id_person=sale.id_customer " +
                            " WHERE DATE_PART('year', sale_date) = ? GROUP BY data ORDER BY total DESC LIMIT 10;");
                }
                //if the id is the name of the customer
                else if(id == 1){
                    preparedStatement = conn.prepareStatement("SELECT last_name AS data, SUM(price) AS total FROM sale " +
                            " JOIN person ON person.id_person=sale.id_customer " +
                            " WHERE DATE_PART('year', sale_date) = ? GROUP BY data ORDER BY total DESC LIMIT 10;");
                }
                preparedStatement.setInt(1, year);
            }
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * modelsChart:  -> Map<Boolean, ResultSet>
     * Purpose: This method connects to the DB and returns the data for the models chart
     */
    public Map<Boolean, ResultSet> modelsChart(Integer idModel, Integer selectionType, Integer year) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            //Quantity of models sold
            if(selectionType == 0) {
                preparedStatement = conn.prepareStatement("SELECT DATE_PART('month', sale_date) AS data, SUM(quantity) AS total " +
                        "FROM sale JOIN sale_car USING(id_sale) JOIN car USING(id_car) " +
                        "JOIN model USING(id_model) " +
                        "WHERE id_model = ? AND DATE_PART('year', sale_date) = ?" +
                        "GROUP by data");
            }
            //Price of models sold
            else if(selectionType == 1){
                preparedStatement = conn.prepareStatement("SELECT DATE_PART('month', sale_date) AS data, SUM(model.price) AS total " +
                        "FROM sale JOIN sale_car USING(id_sale) JOIN car USING(id_car) " +
                        "JOIN model USING(id_model) " +
                        "WHERE id_model = ? AND DATE_PART('year', sale_date) = ?" +
                        "GROUP by data;");
            }

            preparedStatement.setInt(1, idModel);
            preparedStatement.setInt(2, year);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
}
