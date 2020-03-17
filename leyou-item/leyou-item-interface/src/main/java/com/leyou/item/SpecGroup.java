package com.leyou.item;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**商品的规格组表 一个分类多个组
 * @author hp
 */
@Data
@Table(name = "tb_spec_group")
public class  SpecGroup{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;

    private String name;

    @Transient
    private List<SpecParam> params;

   // getter和setter省略
}
