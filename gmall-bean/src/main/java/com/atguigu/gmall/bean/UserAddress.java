package com.atguigu.gmall.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserAddress implements Serializable {

    @Column
    @Id
    private String id;

    @Column
    private String userAddress;

    @Column
    private String userId;

    @Column
    private String consignee;

    @Column
    private String phoneNum;

    @Column
    private String isDefault;

}
