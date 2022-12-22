package com.kaka.SpringBootElasticsearch.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    //学号
    private String studentNo;
    //姓名
    private String name;
    //年龄
    private Integer age;
    //住址
    private String address;
    //版本号
    private Integer version;
    //评价
    private String evaluate;
}
