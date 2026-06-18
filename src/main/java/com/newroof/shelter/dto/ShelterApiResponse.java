package com.newroof.shelter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShelterApiResponse {

    private Response response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<Item> item;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String fcltCd;       // 시설코드
        private String fcltNm;       // 시설명
        private String ronAdres;     // 도로명주소
        private Double lot;          // 경도
        private Double lat;          // 위도
        private String grndUdgd;     // 지상지하구분 (1=지상, 2=지하)
        private Integer prtcptnPsblCnt; // 수용인원
        private String mngNm;        // 관리기관명
        private String mngTelno;     // 관리기관전화번호
    }
}
