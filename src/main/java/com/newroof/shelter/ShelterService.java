package com.newroof.shelter;

import com.newroof.shelter.dto.ShelterResponseDto;
import com.newroof.shelter.entity.Shelter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public List<ShelterResponseDto> findNearby(double lat, double lng, double radiusM) {
        return shelterRepository.findAll().stream()
                .filter(s -> s.getLat() != null && s.getLng() != null)
                .map(s -> {
                    double dist = haversine(lat, lng, s.getLat(), s.getLng());
                    return toDto(s, dist);
                })
                .filter(dto -> dto.getDistanceM() <= radiusM)
                .sorted(Comparator.comparingDouble(ShelterResponseDto::getDistanceM))
                .limit(20)
                .toList();
    }

    public ShelterResponseDto findById(String shelterId) {
        return shelterRepository.findById(shelterId)
                .map(s -> toDto(s, 0))
                .orElseThrow(() -> new IllegalArgumentException("대피소를 찾을 수 없어요: " + shelterId));
    }

    private ShelterResponseDto toDto(Shelter s, double distanceM) {
        return ShelterResponseDto.builder()
                .shelterId(s.getFcltCd())
                .name(s.getName())
                .address(s.getAddress())
                .lat(s.getLat())
                .lng(s.getLng())
                .capacity(s.getCapacity())
                .shelterType(s.getShelterType())
                .managerName(s.getManagerName())
                .managerPhone(s.getManagerPhone())
                .distanceM(distanceM)
                .build();
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
