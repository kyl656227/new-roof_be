package com.newroof.shelter.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShelterResponseDto {
    private String shelterId;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private Integer capacity;       // 수용인원
    private String shelterType;     // 지하철역, 지하주차장, 건물지하 등
    private String managerName;
    private String managerPhone;
    private double distanceM;       // 내 위치에서 거리 (미터)
}
