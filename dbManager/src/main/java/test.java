import dbManager.DbConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Administrator on 2015/12/12.
 */
public class test {
    public static void main(String[] args){
        Connection conn = null;

        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();

            sql = "select STATIONNAME,CONSUMERNAME,IMPORTANCELEVEL,VOLTAGELEVEL,BUREAU from IMPORTANT_CONSUMER t";
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
               String name = rs.getString(1);
                int i=0;
                i++;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DbConnectionPool.getInstance().release(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
