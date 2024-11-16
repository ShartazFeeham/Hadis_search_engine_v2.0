package hadis.searchengine.v2.static_data_server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import hadis.searchengine.v2.utils.FFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class StaticRepo {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticRepo.class);

    public Map<String, String> getHadisMapJsonAsObject()  {
        try {
            String asText = FFiles.read(getStaticHadisMapAbsolutePath());
            asText = asText.replace("\n", " ");
            return objectMapper.readValue(asText, HashMap.class);
        } catch (Exception e) {
            LOGGER.error("Error reading static file: hadismap.json");
            return new HashMap<>();
        }
    }

    private static String getStaticHadisMapAbsolutePath() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/hadismap.json");
        File file = classPathResource.getFile();
        return file.getAbsolutePath();
    }
}
