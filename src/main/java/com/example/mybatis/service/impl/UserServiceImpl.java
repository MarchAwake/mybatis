package com.example.mybatis.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.mybatis.dto.ResponseDto;
import com.example.mybatis.dto.UserDto;
import com.example.mybatis.entity.User;
import com.example.mybatis.mapper.UserMapper;
import com.example.mybatis.service.IUserService;
import com.example.mybatis.utils.CopyUtil;
import com.example.mybatis.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.text.SupplementaryCharacterData;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>用户服务实现</h1>
 *
 * @author March
 * @date 2020/7/16
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    /** 持久化接口 */
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ResponseDto list() {

        List<User> userList = null;
        try {
            userList = mapper.selectByUser();
        } catch (Exception e) {
            log.error("用户服务层错误: {}",JSON.toJSONString(e));
            return ResponseDto.failure("服务器错误");
        }

        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : userList
        ) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            userDto.setAge(user.getAge());
            userDto.setSex(user.getSex());
            userDtoList.add(userDto);
        }

        return new ResponseDto(userDtoList);
    }

    @Override
    public ResponseDto save(UserDto userDto) {

        if (null == userDto) {
            return ResponseDto.failure("用户数据为空");
        }

        boolean flag = StringUtils.isEmpty(userDto.getName())
                || StringUtils.isEmpty(userDto.getAge())
                || StringUtils.isEmpty(userDto.getSex());

        if (flag) {
            log.error("用户数据不完整: {}", JSON.toJSONString(userDto));
            return ResponseDto.failure("用户数据不完整");
        }

        User user = CopyUtil.copy(userDto, User.class);

        if (StringUtils.isEmpty(userDto.getId())) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    /**
     * <h2>添加用户</h2>
     */
    private ResponseDto update(User user) {
        try {
            return mapper.updateByUser(user) == 1 ?
                    ResponseDto.success() : ResponseDto.failure("修改用户失败！");
        } catch (Exception e) {
            log.error("用户服务层错误 :{}",JSON.toJSONString(e));
            return ResponseDto.failure("服务器错误");
        }
    }

    /**
     * <h2>修改用户</h2>
     */
    private ResponseDto insert(User user) {

        user.setId(UuidUtil.getShortUuid());

        try {
            return mapper.insert(user) == 1 ?
                    ResponseDto.success() : ResponseDto.failure("添加用户失败！");
        } catch (Exception e) {
            log.error("用户服务层错误 :{}",JSON.toJSONString(e));
            return ResponseDto.failure("服务器错误");
        }


    }

    @Override
    public ResponseDto delete(String id) {
        return null;
    }

}
