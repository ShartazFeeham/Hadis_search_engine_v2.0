package hadis.searchengine.v2.static_data_server.controller;

import hadis.searchengine.v2.static_data_server.server.HadisProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/static-data-server")
public class HadisController {

    private final HadisProcessor hadisProcessor;

    public HadisController(HadisProcessor hadisProcessor) {
        this.hadisProcessor = hadisProcessor;
    }

    @PostMapping("/load-hadis")
    public Map<?, ?> loadHadis() {
        return hadisProcessor.process();
    }
}
