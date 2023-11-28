package com.caster.normal.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caster.hsu
 * @Since 2023/9/14
 */
@Data
@Accessors(chain = true)
public class Menu {
    private Long id;
    private String name;
    private String url;
    private String text;

}
