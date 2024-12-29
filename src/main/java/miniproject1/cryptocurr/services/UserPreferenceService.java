package miniproject1.cryptocurr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import miniproject1.cryptocurr.models.User;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;


@Service
public class UserPreferenceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserPreferenceService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Save the entire User object as a JSON string in Redis.
     */
    public void saveUser(User user) {
        try {
            String userKey = "user:" + user.getUsername();
            String userData = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(userKey, userData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user data", e);
        }
    }

    /**
     * Retrieve the User object from Redis based on username.
     */
    public User getUser(String username) {
        try {
            String userKey = "user:" + username;
            String userData = redisTemplate.opsForValue().get(userKey);
            if (userData != null) {
                return objectMapper.readValue(userData, User.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize user data", e);
        }
    }
}

