package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class SocietyMembershipPurchaseEvent {

    private Long kidId;

    private Long societyId;

    private Long categoryId;

    private Long validityInMins;

    private Long orderId;

    private Boolean isPartlyPaid;

    private Integer validityInMonths;

}
