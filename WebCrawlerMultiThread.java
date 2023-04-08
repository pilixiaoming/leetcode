package databricks;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebCrawlerMultiThread {
    private static final int THREAD_POOL_MAX_SIZE = 8;
    private static String HOST = null;

    String getHostName(String url){
        return url.substring(7).split("/")[0];
    }

    public void crawl(String startUrl, int depth) {
        HOST = getHostName(startUrl);
        ConcurrentHashMap<String, String> visited = new ConcurrentHashMap<>();
        ExecutorService crawlerThreadExecutor = Executors.newFixedThreadPool(THREAD_POOL_MAX_SIZE);
        ExecutorService diskWriteExecutor = Executors.newFixedThreadPool(THREAD_POOL_MAX_SIZE);

        Future rootCrawlerFuture = crawlerThreadExecutor.submit(new InnerCrawler(
                visited,
                crawlerThreadExecutor,
                diskWriteExecutor,
                startUrl,
                1,
                depth
        ));

        try {
            rootCrawlerFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        crawlerThreadExecutor.shutdown();
        System.out.println("====crawler finished===");
        diskWriteExecutor.shutdown();
        try {
            diskWriteExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class InnerCrawler implements Runnable {
        private final ConcurrentHashMap<String, String> visited;
        private final ExecutorService executor;
        private final ExecutorService diskWriteExecutor;
        private final String url;
        private final int curDepth;
        private final int maxDepth;
        private final List<Future> tasks = new ArrayList<>();

        public InnerCrawler(
                ConcurrentHashMap<String, String> visited,
                ExecutorService executor,
                ExecutorService diskWriteExecutor,
                String url,
                int curDepth,
                int maxDepth
        ) {
            this.visited = visited;
            this.executor = executor;
            this.diskWriteExecutor = diskWriteExecutor;
            this.url = url;
            this.curDepth = curDepth;
            this.maxDepth = maxDepth;
        }

        @Override
        public void run() {
            HTML html = CrawlerHelper.fetch(url);

//          diskWriteExecutor.submit(() -> CrawlerHelper.save(url, html));
            diskWriteExecutor.submit(() -> {
                try {
                    CrawlerHelper.save(url, html);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            if (curDepth > maxDepth) {
                return;
            }

            List<String> nextUrls = CrawlerHelper.parse(html);
            for (String nextUrl : nextUrls) {
                // concurrentHashMap put will lock the map and only current thread can access
                // if return null meaning no same url in the map, thus safe to proceed
                if (getHostName(nextUrl).equals(HOST) && visited.put(nextUrl, "") == null) {
                    tasks.add(executor.submit(new InnerCrawler(
                            visited,
                            executor,
                            diskWriteExecutor,
                            nextUrl,
                            curDepth + 1,
                            maxDepth
                    )));
                }
            }

            // wait for subthreads to finish
            tasks.forEach(task -> {
                try {
                    task.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
