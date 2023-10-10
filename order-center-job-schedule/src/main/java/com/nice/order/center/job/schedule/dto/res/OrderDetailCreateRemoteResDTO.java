package com.nice.order.center.job.schedule.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrderDetailCreateRemoteResDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

}
