package databricks;

import javax.swing.text.html.HTML;
import java.util.*;

public class WebCrawler {
    public List<String> crawl(String startUrl, int depth) throws InterruptedException {
        Queue<String> q = new LinkedList<>();
        q.offer(startUrl);

        Set<String> visited = new HashSet<>();
        visited.add(startUrl);

        String hostName = getHostName(startUrl);
        int level = 0;
        while (!q.isEmpty()) {
            Queue<String> childrenQ = new LinkedList<>();
            while (!q.isEmpty()) {
                String currentUrl = q.poll();
                // I/O blocking
                HTML curHtml = CrawlerHelper.fetch(currentUrl);
                CrawlerHelper.save(currentUrl, curHtml);
                for (String url : CrawlerHelper.parse(curHtml)) {
                    if (url.contains(hostName) && !visited.contains(url)) {
                        q.offer(url);
                        visited.add(url);
                    }
                }
            }
            level++;
            if (level == depth) {
                break;
            }
            q = childrenQ;
        }

        return new LinkedList<String>(visited);
    }

    private String getHostName(String startUrl) {
        String[] ss = startUrl.split("/");
        return ss[2];
    }

}


class CrawlerHelper {
    private static Map<String, String> htmlMap = new HashMap<String, String>() {{
        put("1", "2/3/4");
        put("2", "3/4");
        put("3", "5");
        put("4", "5");
        put("5", "6");
    }};

    public static HTML fetch(String url) {
        return null;
//        return new HTML(htmlMap.getOrDefault(url, ""));
    }

    public static List<String> parse(HTML html) {
        return null;
//        return Arrays.asList(html.html.split("/")).stream().filter(s -> !s.isEmpty()).toList();
    }

    public static void save(String url, HTML html) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("url " + url + " is saved");
    }
}