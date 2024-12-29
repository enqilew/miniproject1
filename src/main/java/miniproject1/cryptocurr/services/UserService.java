package miniproject1.cryptocurr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import miniproject1.cryptocurr.models.User;

@Service
public class UserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        String redisKey = "user:" + user.getUsername();
    
        // Check if the username already exists
        if (redisTemplate.opsForValue().get(redisKey) != null) {
            throw new IllegalArgumentException("Username already exists!");
        }

        try {
            // Encode the password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Save the user to Redis
            String userJson = objectMapper.writeValueAsString(user);
            System.out.println("Serialized User JSON: " + userJson); // Log serialized JSON
            redisTemplate.opsForValue().set(redisKey, userJson);

            String redisValue = redisTemplate.opsForValue().get(redisKey);
            System.out.println("Value in Redis after registration: " + redisValue);

        } catch (JsonProcessingException e) {
            System.err.println("Error serializing user: " + e.getMessage());
            throw new RuntimeException("Error serializing user", e);
        }
    }
    

    public User findUser(String username) {
        try {
            String redisKey = "user:" + username;
            String userJson = redisTemplate.opsForValue().get(redisKey);
    
            if (userJson == null) {
                throw new UsernameNotFoundException("User not found!");
            }
    
            // Deserialize JSON into User object
            return objectMapper.readValue(userJson, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing user", e);
        }
    }
    
}
