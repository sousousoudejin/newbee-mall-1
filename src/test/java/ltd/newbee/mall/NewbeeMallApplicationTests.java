package ltd.newbee.mall;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootTest
class NewbeeMallApplicationTests {

    //注入数据源对象
    @Autowired
    private DataSource defaultDataSource;

    @Test
    void contextLoads() {

        Connection con = null;
        Statement sta = null;
        ResultSet rs = null;

        try {
            //获取数据库连接
            con = defaultDataSource.getConnection();
            //创建一个会话
            sta = con.createStatement();
            //执行sql语句
            rs = sta.executeQuery(" select * from goods ");
            //获取查询记录
            while (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
                System.out.println(String.valueOf(rs.getDouble(3)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                if (sta != null) {
                    sta.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
