package com.jiannanzhi.managebd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiannanzhi.managebd.Entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
//关于MapperScan和Mapper http://www.mybatis.cn/archives/862.html
public interface UserMapper extends BaseMapper<User> {

    List<User> selectAll();

    int addUser(User user);

//    @Update("update user set username = #{username}, email = #{email}, nickname = #{nickname} where id = #{id}")
//    int updateUser(User user);

    @Delete("delete from user where id = #{user_id}")
    boolean deleteUser(@Param("user_id") Integer id);

    @Select("select * from user where username like #{username} and address like #{address} limit #{pageNum}, #{pageSize}")
    List<User> selectUserByPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize, @Param("username") String username, @Param("address") String address);

    @Select("select count(*) from user where username like #{username} and address like #{address}")
    Integer selectTotalNum(@Param("username") String username, @Param("address") String address);
}
