package hadis.searchengine.v2.experiments.laodTest;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class SearchPublicEndpointLoadTest {

    public static ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) throws Exception {
        String url = "https://century-stroke-evaluating-cornwall.trycloudflare.com/search-server/1/10/2/BUK";
        hitTheApi(url, 1);
        hitTheApi(url, 3);
        hitTheApi(url, 10);
        hitTheApi(url, 50);
        hitTheApi(url, 200);
        hitTheApi(url, 1000);
    }

    private static void hitTheApi(String url, int numRequests) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30)) // Set a connection timeout
                .build();

        List<Future<?>> futures = new ArrayList<>();
        List<Long> responseTimes = new ArrayList<>();

        for (int i = 0; i < numRequests; i++) {
            Future<?> future = executorService.submit(() -> {
                try {
                    long startTime = System.nanoTime();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    long endTime = System.nanoTime();
                    if (response.statusCode() == 200) {
//                        log.info("Request successful with response time: {}", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
                        responseTimes.add(TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
                    } else {
//                        log.warn("Request failed with status code: {}", response.statusCode());
                    }

                } catch (Exception e) {
//                    log.error("Error during request: {}", e.getMessage());
                }
            });
            futures.add(future);
        }

        for (Future<?> future : futures) {
            future.get();
        }

        if (responseTimes.isEmpty()) {
            System.out.println("No successful requests were made.");
            return;
        }

        double averageTime = responseTimes.stream().mapToLong(val -> val).average().orElse(0.0);
        long fastestTime = Collections.min(responseTimes);
        long slowestTime = Collections.max(responseTimes);
//        log.info("Load test results for {}:", url);
//        log.info("Number of requests: {}", numRequests);
//        log.info("Number of successful requests: {}", responseTimes.size());
//        log.info("Average response time: {} milliseconds", averageTime);
//        log.info("Fastest response time: {} milliseconds", fastestTime);
//        log.info("Slowest response time: {} milliseconds", slowestTime);
//        long end = System.currentTimeMillis();
//        log.info("Total time taken: {} milliseconds", end - start);
        log.info("------------Summery: Succeeded {}/{} with {} ms average------------", responseTimes.size(), numRequests, (int) averageTime);
    }
}