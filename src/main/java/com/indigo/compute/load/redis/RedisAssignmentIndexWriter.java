package com.indigo.compute.load.redis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.load.AssignmentIndexWriter;
import com.indigo.config.DeskAssignmentRedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class RedisAssignmentIndexWriter implements AssignmentIndexWriter {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DeskAssignmentRedisConfig redisIndexConfig;

    @Override
    public void save(AssignmentIndexSnapshot indexes) {
        Objects.requireNonNull(indexes, "Aggregated indexes must not be null");
        Map<String, Object> values = new LinkedHashMap<>();
        values.put(redisIndexConfig.getStationKey(), emptyIfNull(indexes.getStationIndexes()));
        values.put(redisIndexConfig.getAircraftRegistrationKey(), emptyIfNull(indexes.getAircraftRegistrationIndexes()));
        values.put(redisIndexConfig.getAircraftTypeKey(), emptyIfNull(indexes.getAircraftTypeIndexes()));
        values.put(redisIndexConfig.getZoneKey(), emptyIfNull(indexes.getZoneIndexes()));
        values.put(redisIndexConfig.getAircraftZoneKey(), emptyIfNull(indexes.getAircraftZoneIndexes()));
        // RedisTemplate serializes all values before issuing the MSET command.
        redisTemplate.opsForValue().multiSet(values);
    }

    private Map<?, ?> emptyIfNull(Map<?, ?> index) {
        return CollectionUtils.isEmpty(index) ? Map.of() : index;
    }
}