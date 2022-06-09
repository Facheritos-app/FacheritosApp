package backend.connectionBD;
import org.postgresql.ds.PGSimpleDataSource;

public class ConnectionBD {

    public static PGSimpleDataSource dataSource = null;

    public static PGSimpleDataSource connectDB(){
        if(dataSource == null){
            dataSource = new PGSimpleDataSource();
            String[] serverAdress = {"ec2-52-204-195-41.compute-1.amazonaws.com"};
            dataSource.setServerNames(serverAdress);
            int[] serverPortNumbers = {5432};
            dataSource.setPortNumbers(serverPortNumbers);
            dataSource.setDatabaseName("dctfmpkc6bn0nv");
            dataSource.setUser("usildqidplilwm");
            dataSource.setPassword("2367bbf0f9dc54746d5b308d929d92aa69f1df973bf807151eb717db52182621");
        }
        return dataSource;

    }

}


