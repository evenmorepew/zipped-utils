package zip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.util.DriverDataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class ReadTest {


    DataSource ds = new DriverDataSource(
            "jdbc:mysql://localhost:3306/my_dummy",
            "com.mysql.Driver",
            new Properties(),
            "root",
            "test"
    );
    NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(ds));

    @Test
    @SneakyThrows
    void name() {

        long t1 = System.currentTimeMillis();

        List<Long> maxId = jdbcTemplate.query("""
                select max(id) from zipped
                """, (rs, i) -> rs.getLong(1));

        System.out.println(maxId.get(0));

        System.out.println("Lese Id aus DB: " + (System.currentTimeMillis() - t1));

        long t11 = System.currentTimeMillis();

        List<byte[]> bytes = jdbcTemplate.query("""
                select id, zip from zipped
                where id = 1;
                """, (rs, i) -> rs.getBytes(2));

        System.out.println("Lese aus DB: " + (System.currentTimeMillis() - t11));

        long t3 = System.currentTimeMillis();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.get(0));
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

        byte[] bytesUnzipped = gzipInputStream.readAllBytes();
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Dummy> dummies = objectMapper.readValue(bytesUnzipped, new TypeReference<>() {
        });

        System.out.println(dummies.size());

        System.out.println("Lese Liste von Objekten: " + (System.currentTimeMillis() - t3));
    }

    @Test
    void name_2() {
    }
}
