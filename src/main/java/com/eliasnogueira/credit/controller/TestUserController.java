package com.eliasnogueira.credit.controller;

import com.eliasnogueira.credit.dto.TestUserDTO;
import com.eliasnogueira.credit.entity.TestUser;
import com.eliasnogueira.credit.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/user")
public class TestUserController {
    @Resource
    public UserRepository userRepository;

    @Operation(summary = "查询所有用户")
    @GetMapping("/getAllUsers")
    public List<TestUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "通过主键查询用户")
    @GetMapping("/getUser/{userId}")
    public TestUser getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Operation(summary = "添加用户")
    @PutMapping("/addUser")
    public TestUser addUser(@RequestBody TestUserDTO testUserDTO) {
        TestUser entity = new TestUser();
        BeanUtils.copyProperties(testUserDTO, entity);
        TestUser save = userRepository.saveAndFlush(entity);
        return save;
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delUser/{userId}")
    public String delUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return "success";
    }

    @Operation(summary = "修改用户昵称")
    @PostMapping("/modifyUserNickName")
    public String modifyUserNickName(@RequestBody TestUser user) {
        int row = userRepository.modifyUserNickName(user.getNickname(), user.getId());
        return row > 0 ? "success" : "fail";
    }
}
