package backend.endpoints.saleEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SaleEndpoint {
    public Map<Boolean, ResultSet> getSales() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\t\tsale.sale_date,sale.price, sale.name_seller, sale.name_client, sale.payment_method , headquarter.name\n" +
                    "from (select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\t\tsale.sale_date,sale.price, sale.name_seller, sale.name_client, payment.payment_method\n" +
                    "from (select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\tsale.sale_date,sale.price, sale.name_seller, (person.first_name || ' ' || person.last_name) AS name_client \n" +
                    "\tfrom (select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\t\tsale.sale_date,sale.price, (person.first_name || ' ' || person.last_name) AS name_seller\n" +
                    "\t\t\t\tfrom sale join person on sale.id_worker = person.id_person) as sale join person ON sale.id_customer = person.id_person) as sale join payment on sale.id_payment_method = payment.id_payment) as sale join headquarter using (id_headquarter)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }
}