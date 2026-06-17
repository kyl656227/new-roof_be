package com.newroof.shelter;

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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (shelterRepository.count() > 0) return;

        List<Shelter> shelters = new ArrayList<>();

        var resource = new ClassPathResource("shelter.csv");
        try (var reader = new CSVReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readNext(); // 1행: 컬럼 코드명 skip
            reader.readNext(); // 2행: 한국어 설명 skip

            String[] row;
            while ((row = reader.readNext()) != null) {
                try {
                    String fcltCd    = row[2].trim();
                    String name      = row[3].trim();
                    String address   = row[11].trim();
                    String lngDeg    = row[12].trim();
                    String lngMin    = row[13].trim();
                    String lngSec    = row[14].trim();
                    String latDeg    = row[15].trim();
                    String latMin    = row[16].trim();
                    String latSec    = row[17].trim();
                    String grndUdgd  = row[18].trim(); // 1=지상, 2=지하
                    String capacity  = row[19].trim();
                    String mngNm     = row[21].trim();
                    String mngTel    = row[23].trim();

                    double lat = toDecimal(latDeg, latMin, latSec);
                    double lng = toDecimal(lngDeg, lngMin, lngSec);

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

        shelterRepository.saveAll(shelters);
        log.info("민방위 대피소 {}건 로드 완료", shelters.size());
    }

    // 도분초 → 소수점 좌표 변환
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
