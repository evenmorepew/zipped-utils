package zip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.util.DriverDataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPOutputStream;

public class WriteZip {

    @Test
    @SneakyThrows
    public void name() {

        ObjectMapper objectMapper = new ObjectMapper();

        Set<Dummy> set = IntStream.rangeClosed(1, 200_000)
                .mapToObj(i -> new Dummy("A" + (1000000000 + i), String.valueOf(9780000000000L + i)))
                .collect(Collectors.toSet());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);
        byte[] bytesPlain = objectMapper.writeValueAsBytes(set);
        gzipOutputStream.write(bytesPlain);

        gzipOutputStream.close();

        byte[] zippedBytes = baos.toByteArray();

        Files.write(Paths.get("src/test/resources/dummy_zipped.binary"), zippedBytes);
        Files.write(Paths.get("src/test/resources/dummy_plain.txt"), bytesPlain);


        DataSource ds = new DriverDataSource(
                "jdbc:mysql://localhost:3306/my_dummy",
                "com.mysql.Driver", new Properties(),
                "root",
                "test");
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(ds));

        List<Long> maxId = jdbcTemplate.query("""
                select max(id) from zipped;
                """, (rs, i) -> rs.getLong(1));

        System.out.println(maxId.size());

        Map<String, Object> params = Map.of("id", maxId.get(0) + 1, "zip", bytesPlain);

        int update = jdbcTemplate.update("""
                insert into zipped values (:id, :zip);
                """, params);

        System.out.println(update);
    }
}
