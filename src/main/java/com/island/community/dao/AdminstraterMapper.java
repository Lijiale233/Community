package com.island.community.dao;


import com.island.community.entity.Adminstrater;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminstraterMapper {
    public List<Adminstrater> selectAll();

    public int insertAdminstrater(Adminstrater adminstrater);

    public int updatePassword(int id, String password);

    public Adminstrater selectByName(String username);
}
