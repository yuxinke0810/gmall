package com.atguigu.gmall.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class BaseAttrValue implements Serializable {

    @Id
    @Column
    private String id;

    @Column
    private String valueName;

    @Column
    private String attrId;

    @Transient
    private String urlParam;

}
