package com.newroof.shelter;

import com.newroof.shelter.dto.ShelterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterApiClient shelterApiClient;

    public List<ShelterResponseDto> findNearby(double lat, double lng, double radiusM) {
        return shelterApiClient.fetchNearby(lat, lng, radiusM);
    }

    public ShelterResponseDto findById(String shelterId) {
        return shelterApiClient.fetchById(shelterId);
    }
}
