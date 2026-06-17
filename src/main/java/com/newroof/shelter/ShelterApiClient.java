package com.newroof.shelter;

import com.newroof.shelter.dto.ShelterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShelterApiClient {

    private final WebClient webClient;

    @Value("${shelter.api.key}")
    private String apiKey;

    // 행정안전부 민방위대피소 API
    // 공공데이터포털: 행정안전부_민방위대피소
    private static final String BASE_URL = "https://apis.data.go.kr/1741000/CvilAflshelter1";

    public List<ShelterResponseDto> fetchNearby(double lat, double lng, double radiusM) {
        // TODO: 전체 대피소 조회 후 거리 필터링 (Haversine 공식)
        // GET /getCvilAflshelter1List?serviceKey={key}&pageNo=1&numOfRows=1000&type=json
        return List.of();
    }

    public ShelterResponseDto fetchById(String shelterId) {
        // TODO: 특정 대피소 조회
        return null;
    }

    // Haversine 공식으로 두 좌표 간 거리(미터) 계산
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
