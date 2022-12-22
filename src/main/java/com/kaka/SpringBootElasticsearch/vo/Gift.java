package com.kaka.SpringBootElasticsearch.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gift {
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String describe;
    /**
     * 商品价格
     */
    private Integer price;
    /**
     * 日期
     */
    private Date date;

}
