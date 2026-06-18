package com.newroof.shelter;

import com.newroof.shelter.dto.ShelterApiResponse.Item;
import com.newroof.shelter.entity.Shelter;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShelterDataLoader implements ApplicationRunner {

    private final ShelterRepository shelterRepository;
    private final ShelterApiClient shelterApiClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (shelterRepository.count() > 0) return;

        List<Shelter> shelters = loadFromApi();
        if (shelters.isEmpty()) {
            log.warn("API 조회 실패 또는 결과 없음, CSV 폴백 실행");
            shelters = loadFromCsv();
        }

        shelterRepository.saveAll(shelters);
        log.info("민방위 대피소 {}건 로드 완료", shelters.size());
    }

    private List<Shelter> loadFromApi() {
        try {
            List<Item> items = shelterApiClient.fetchAllItems();
            List<Shelter> result = new ArrayList<>();
            for (Item item : items) {
                if (item.getLat() == null || item.getLot() == null) continue;
                result.add(Shelter.builder()
                        .fcltCd(item.getFcltCd())
                        .name(item.getFcltNm())
                        .address(item.getRonAdres())
                        .lat(item.getLat())
                        .lng(item.getLot())
                        .capacity(item.getPrtcptnPsblCnt())
                        .shelterType("2".equals(item.getGrndUdgd()) ? "지하" : "지상")
                        .managerName(item.getMngNm())
                        .managerPhone(item.getMngTelno())
                        .build());
            }
            log.info("API에서 민방위 대피소 {}건 로드", result.size());
            return result;
        } catch (Exception e) {
            log.error("API 로드 실패: {}", e.getMessage());
            return List.of();
        }
    }

    private List<Shelter> loadFromCsv() throws Exception {
        List<Shelter> shelters = new ArrayList<>();
        var resource = new ClassPathResource("shelter.csv");
        try (var reader = new CSVReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readNext(); // 1행: 컬럼 코드명 skip
            reader.readNext(); // 2행: 한국어 설명 skip

            String[] row;
            while ((row = reader.readNext()) != null) {
                try {
                    String fcltCd   = row[2].trim();
                    String name     = row[3].trim();
                    String address  = row[11].trim();
                    double lat = toDecimal(row[15].trim(), row[16].trim(), row[17].trim());
                    double lng = toDecimal(row[12].trim(), row[13].trim(), row[14].trim());
                    String grndUdgd = row[18].trim();
                    String capacity = row[19].trim();
                    String mngNm    = row[21].trim();
                    String mngTel   = row[23].trim();

                    if (lat == 0 || lng == 0) continue;

                    shelters.add(Shelter.builder()
                            .fcltCd(fcltCd)
                            .name(name)
                            .address(address)
                            .lat(lat)
                            .lng(lng)
                            .capacity(parseIntSafe(capacity))
                            .shelterType("2".equals(grndUdgd) ? "지하" : "지상")
                            .managerName(mngNm)
                            .managerPhone(mngTel)
                            .build());
                } catch (Exception e) {
                    log.warn("CSV 파싱 오류 (skip): {}", e.getMessage());
                }
            }
        }
        return shelters;
    }

    private double toDecimal(String deg, String min, String sec) {
        try {
            return Double.parseDouble(deg)
                    + Double.parseDouble(min) / 60
                    + Double.parseDouble(sec) / 3600;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Integer parseIntSafe(String val) {
        try { return Integer.parseInt(val.replaceAll("[^0-9]", "")); }
        catch (Exception e) { return null; }
    }
}
