package backend.endpoints.headquarterEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.city.CityDTO;
import backend.dto.headquarterDTO.HeadquarterDTO;
import backend.dto.personDTO.PersonDTO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class HeadquarterEndpoint {

    public Map<Boolean, ResultSet> getHeadquarters() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("SELECT * FROM headquarter JOIN city USING(id_city) ORDER BY name");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }

    public static void createHeadquarter(HeadquarterDTO headquarterDTO) {
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            PreparedStatement preparedStatement = null;
            try {
                String query = "insert into headquarter(id_city,name,cellphone,email,address) values (?,?,?,?,?)";
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(1, headquarterDTO.getId_city());
                preparedStatement.setString(2, headquarterDTO.getName());
                preparedStatement.setString(3, headquarterDTO.getCellphone());
                preparedStatement.setString(4, headquarterDTO.getEmail());
                preparedStatement.setString(5, headquarterDTO.getAddress());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {

            }
        } catch (Exception ex) {

        }
    }

    public static Integer getCity(String name, CityDTO cityDTO) throws SQLException {
        Connection conn = ConnectionBD.connectDB().getConnection();
        //try(Connection conn = ConnectionBD.connectDB().getConnection()){
            PreparedStatement ps=null;
            ResultSet rs=null;
            Integer result=0;
            String query="select distinct id_city from city where city_name=  ?";

                ps=conn.prepareStatement(query);
                ps.setString(1, cityDTO.getCity_name());
                rs=ps.executeQuery();
                while(rs.next()){
                    result= rs.getInt("id_city");
                    System.out.println(result);
                }
                 System.out.println(result);
                return  result;
          //  } catch (SQLException e) {
          //      return 2;
          //  }
    }

    public Map<Boolean, ResultSet> getHeadquarterById(Integer idHeadquarter){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM headquarter JOIN city USING(id_city)WHERE id_headquarter= ?");
            preparedStatement.setInt(1, idHeadquarter);
            resultSet = preparedStatement.executeQuery();
            resultSet.next(); //show the row data
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public static void updateHeadquarter(Integer idHeadquarter,Integer idCity, String name, String cellphone, String email, String address){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE headquarter set id_city = ?, name = ?, cellphone = ? , email = ? , address = ? where id_headquarter = ?");
            preparedStatement.setInt(1,idCity);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, cellphone);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5,address) ;
            preparedStatement.setInt(6, idHeadquarter);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }


    }



}
