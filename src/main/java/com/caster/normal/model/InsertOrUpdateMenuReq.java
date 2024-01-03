package com.caster.normal.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caster.hsu
 * @Since 2023/12/26
 */
@Data
@Accessors(chain = true)
public class InsertOrUpdateMenuReq {
    Long id;
    String text;
}
