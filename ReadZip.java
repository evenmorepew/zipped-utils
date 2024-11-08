package zip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class ReadZip {

    @Test
    @SneakyThrows
    public void name() {

        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/dummy_zipped.binary"));

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

        byte[] bytesUnzipped = gzipInputStream.readAllBytes();
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Dummy> dummies = objectMapper.readValue(bytesUnzipped, new TypeReference<>() {
        });

        System.out.println(dummies);
    }
}
