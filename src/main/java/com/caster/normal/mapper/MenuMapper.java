package com.caster.normal.mapper;

import com.caster.normal.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author caster.hsu
 * @Since 2023/9/14
 */
@Mapper
public interface MenuMapper {
    List<Menu> findAll();

}
