package com.newroof.shelter;

import com.newroof.common.response.ApiResponse;
import com.newroof.shelter.dto.ShelterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelters")
@RequiredArgsConstructor
public class ShelterController {

    private final ShelterService shelterService;

    // 내 위치 기준 가까운 대피소 목록
    @GetMapping("/nearby")
    public ApiResponse<List<ShelterResponseDto>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10000") double radiusM) {
        return ApiResponse.ok(shelterService.findNearby(lat, lng, radiusM));
    }

    // 대피소 상세
    @GetMapping("/{shelterId}")
    public ApiResponse<ShelterResponseDto> getDetail(@PathVariable String shelterId) {
        return ApiResponse.ok(shelterService.findById(shelterId));
    }
}
