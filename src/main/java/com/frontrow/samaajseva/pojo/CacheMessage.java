package com.frontrow.samaajseva.pojo;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheMessage {
    private String cacheName;

    private List<String> keys;

    private Map<String, Object> map;
}
