package com.newroof.shelter;

import com.newroof.shelter.dto.ShelterApiResponse;
import com.newroof.shelter.dto.ShelterApiResponse.Item;
import com.newroof.shelter.dto.ShelterResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShelterApiClient {

    private final WebClient webClient;

    @Value("${shelter.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://apis.data.go.kr/1741000/CvilAflshelter1";
    private static final int PAGE_SIZE = 1000;

    public List<ShelterResponseDto> fetchNearby(double lat, double lng, double radiusM) {
        return fetchAll().stream()
                .filter(item -> item.getLat() != null && item.getLot() != null)
                .map(item -> {
                    double dist = haversine(lat, lng, item.getLat(), item.getLot());
                    return toDto(item, dist);
                })
                .filter(dto -> dto.getDistanceM() <= radiusM)
                .sorted(Comparator.comparingDouble(ShelterResponseDto::getDistanceM))
                .limit(20)
                .toList();
    }

    public ShelterResponseDto fetchById(String shelterId) {
        return fetchAll().stream()
                .filter(item -> shelterId.equals(item.getFcltCd()))
                .findFirst()
                .map(item -> toDto(item, 0))
                .orElse(null);
    }

    public List<Item> fetchAllItems() {
        return fetchAll();
    }

    private List<Item> fetchAll() {
        List<Item> result = new ArrayList<>();
        int pageNo = 1;

        while (true) {
            ShelterApiResponse response = callApi(pageNo);
            if (response == null
                    || response.getResponse() == null
                    || response.getResponse().getBody() == null
                    || response.getResponse().getBody().getItems() == null
                    || response.getResponse().getBody().getItems().getItem() == null) {
                break;
            }

            List<Item> page = response.getResponse().getBody().getItems().getItem();
            result.addAll(page);

            int totalCount = response.getResponse().getBody().getTotalCount();
            if (result.size() >= totalCount || page.size() < PAGE_SIZE) {
                break;
            }
            pageNo++;
        }

        log.debug("API에서 민방위 대피소 {}건 조회", result.size());
        return result;
    }

    private ShelterApiResponse callApi(int pageNo) {
        String uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/getCvilAflshelter1List")
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", PAGE_SIZE)
                .queryParam("type", "json")
                .build(true)
                .toUriString();

        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(ShelterApiResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("민방위 대피소 API 호출 실패 (page={}): {}", pageNo, e.getMessage());
            return null;
        }
    }

    private ShelterResponseDto toDto(Item item, double distanceM) {
        return ShelterResponseDto.builder()
                .shelterId(item.getFcltCd())
                .name(item.getFcltNm())
                .address(item.getRonAdres())
                .lat(item.getLat() != null ? item.getLat() : 0)
                .lng(item.getLot() != null ? item.getLot() : 0)
                .capacity(item.getPrtcptnPsblCnt())
                .shelterType("2".equals(item.getGrndUdgd()) ? "지하" : "지상")
                .managerName(item.getMngNm())
                .managerPhone(item.getMngTelno())
                .distanceM(distanceM)
                .build();
    }

    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
