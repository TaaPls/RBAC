package com.taxi.trip.service;

import com.taxi.trip.entity.dto.DriverResponseDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Service
public class DriverCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String AVAILABLE_DRIVERS_KEY = "available:drivers";
    private static final String BUSY_DRIVERS_KEY = "busy:drivers";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public DriverCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    public void updateAvailableDrivers(List<DriverResponseDto> drivers) {
        redisTemplate.delete(AVAILABLE_DRIVERS_KEY);

        if (!drivers.isEmpty()) {
            drivers.forEach(driver -> redisTemplate.opsForSet().add(AVAILABLE_DRIVERS_KEY, driver));
            redisTemplate.expire(AVAILABLE_DRIVERS_KEY, CACHE_TTL);
        }
    }

    public DriverResponseDto popRandomAvailableDriver() {
        Object driverObj = redisTemplate.opsForSet().pop(AVAILABLE_DRIVERS_KEY);

        if (driverObj == null) {
            return null;
        }

        if (driverObj instanceof DriverResponseDto) {
            return (DriverResponseDto) driverObj;
        }

        return objectMapper.convertValue(driverObj, DriverResponseDto.class);
    }

    public void returnDriverToAvailable(DriverResponseDto driver) {
        if (driver != null) {
            redisTemplate.opsForSet().add(AVAILABLE_DRIVERS_KEY, driver);
        }
    }

    public Set<Object> getAvailableDrivers() {
        return redisTemplate.opsForSet().members(AVAILABLE_DRIVERS_KEY);
    }

    public long getAvailableDriversCount() {
        Long count = redisTemplate.opsForSet().size(AVAILABLE_DRIVERS_KEY);
        return count != null ? count : 0;
    }

    public void clearCache() {
        redisTemplate.delete(AVAILABLE_DRIVERS_KEY);
        redisTemplate.delete(BUSY_DRIVERS_KEY);
    }
}
