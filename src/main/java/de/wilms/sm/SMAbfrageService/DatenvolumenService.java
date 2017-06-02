package de.wilms.sm.SMAbfrageService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class DatenvolumenService {

    private static final Logger LOG = LoggerFactory.getLogger(DatenvolumenService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sm.login.url}")
    private String LOGIN_URL;

    @Value("${sm.data.url}")
    private String DATA_URL;

    @Cacheable("datavolumes")
    @GetMapping("/datavolume")
    public ResponseEntity<DatenvolumenResponse> getDatavolume(@RequestParam final String username, @RequestParam final String password) {
        LOG.info("handling request for {}", username);
        String sid = getSID(username, password);
        if (sid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            DatenvolumenResponse datenvolumen = getDatenvolumen(sid);
            if (datenvolumen == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(datenvolumen);
            }
        }
    }

    private String getSID(final String username, final String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "refid=24429; partnerid=24429; cookie_time=2017-04-25+05%3A54%3A42; _ga=GA1.2.204146203.1493092484; promotion_partner_id=24842; promotion_product_id=3248; promotion_channel_id=19386; promotion_customer_journey=%5B24842]; Bestandskunde=true; connect=smart3; _SID=3lo6nd0504dpdsue7n5unjj5v7; isCookieAllowed=true; _gid=GA1.2.1997218551.1496306791; smartmobil=o1vdkqqq5hsar4esrrm6jkbn85; te_sid=1a482b7f-53ce-eaea-bde9-f7fcf5c2a031; sw_UNC=MDAwMjczNmQ2MTcyovTxsf%2BGTP7FPs%2Frhc8DLNRBFYeJCY5a");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("UserLoginType[alias]", username);
        map.add("UserLoginType[password]", password);
        map.add("UserLoginType[_token]", "c5ce512ea37e34ed7f89ef0706c057c485177412");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        ResponseEntity<String> exchange = restTemplate.exchange(LOGIN_URL, HttpMethod.POST, requestEntity, String.class);
        List<String> strings = exchange.getHeaders().get("Set-Cookie");
        if (strings == null) {
            return null;
        } else {
            return Arrays.stream(
                    strings
                            .stream()
                            .filter(s -> s.startsWith("_SID"))
                            .findFirst().get()
                            .split(";"))
                    .filter(s -> s.startsWith("_SID"))
                    .map(s -> s.split("=")[1])
                    .findFirst().get();
        }
    }

    private DatenvolumenResponse getDatenvolumen(final String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "_SID=" + sid + ";");

        HttpEntity requestEntity = new HttpEntity(headers);

        ResponseEntity<String> exchange = restTemplate.exchange(DATA_URL, HttpMethod.GET, requestEntity, String.class);

        return htmlToDatenvolumenResponse(exchange.getBody());
    }

    private DatenvolumenResponse htmlToDatenvolumenResponse(String html) {
        Document parse = Jsoup.parse(html);

        Elements elements = parse.select("table > tbody > tr > td[style=\"text-align: right;\"] > b");

        if (elements.size() == 3) {
            return new DatenvolumenResponse(
                    getValueInMB(elements.get(0)),
                    getValueInMB(elements.get(1)),
                    getValueInMB(elements.get(2))
            );
        } else {
            return null;
        }
    }

    private double getValueInMB(Element e) {
        String[] split = e.html().split(" ");
        String value = split[0].replace(",", ".");
        String type = split[1];
        if (type.startsWith("GB")) {
            return Double.valueOf(value) * 1000;
        } else {
            return Double.valueOf(value);
        }
    }

}
