package com.eliasnogueira.credit.controller;

import com.eliasnogueira.credit.dto.TestUserDTO;
import com.eliasnogueira.credit.entity.TestUser;
import com.eliasnogueira.credit.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class TestUserController {
    @Resource
    public UserRepository userRepository;

    @GetMapping("/getAllUsers")
    public List<TestUser> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/getUser/{userId}")
    public TestUser getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @PutMapping("/addUser")
    public TestUser addUser(@RequestBody TestUserDTO testUserDTO) {
        TestUser entity = new TestUser();
        BeanUtils.copyProperties(testUserDTO, entity);
        TestUser save = userRepository.saveAndFlush(entity);
        return save;
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @DeleteMapping("/delUser/{userId}")
    public String delUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return "success";
    }

    /**
     * 修改用户昵称
     * @param user
     * @return
     */
    @PostMapping("/modifyUserNickName")
    public String modifyUserNickName(@RequestBody TestUser user) {
        int row = userRepository.modifyUserNickName(user.getNickname(), user.getId());
        return row > 0 ? "success" : "fail";
    }
}
