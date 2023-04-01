package com.zhengjianting.autosoftware.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhengjianting.autosoftware.common.Result;
import com.zhengjianting.autosoftware.entity.ProjectCount;
import com.zhengjianting.autosoftware.entity.UserCount;
import com.zhengjianting.autosoftware.entity.ViewCount;
import com.zhengjianting.autosoftware.service.impl.UserCountService;
import com.zhengjianting.autosoftware.service.impl.ViewCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@RestController
public class StatisticsController {
    @Resource
    private ViewCountService viewCountService;

    @Resource
    private UserCountService userCountService;

    @Resource
    private MongoTemplate mongoTemplate;

    private <T> Wrapper<T> queryWrapper(Class<T> ignoredClazz) {
        return new QueryWrapper<T>()
                .eq("delete_flag", "N")
                .apply("date_part('year', creation_date) = date_part('year', now())")
                .groupBy("date_part('month', creation_date)")
                .select("date_part('month', creation_date) as month, count(*) as count");
    }

    private List<Long> calculateViewCount() {
        List<Long> viewCount = LongStream.iterate(0, n -> n).limit(12).boxed().collect(Collectors.toList());
        List<ViewCount> monthViewCount = viewCountService.list(queryWrapper(ViewCount.class));
        monthViewCount.forEach(vc -> viewCount.set(vc.getMonth() - 1, vc.getCount()));
        return viewCount;
    }

    private List<Long> calculateUserCount() {
        List<Long> userCount = LongStream.iterate(0, n -> n).limit(12).boxed().collect(Collectors.toList());
        List<UserCount> monthUserCount = userCountService.list(queryWrapper(UserCount.class));
        monthUserCount.forEach(uc -> userCount.set(uc.getMonth() - 1, uc.getCount()));
        return userCount;
    }

    private List<Long> calculateProjectCount() {
        List<Long> projectCount = LongStream.iterate(0, n -> n).limit(12).boxed().collect(Collectors.toList());
        Aggregation agg = Aggregation.newAggregation(
                ProjectCount.class,
                Aggregation.project()
                        .and("created").substring(0, 4).as("year")
                        .and("created").substring(5, 2).as("month"),
                Aggregation.match(Criteria.where("year").is(String.valueOf(LocalDateTime.now().getYear()))),
                Aggregation.group("month").count().as("count"),
                Aggregation.project("count").and("month").previousOperation()
        );
        AggregationResults<ProjectCount> monthProjectCount = mongoTemplate.aggregate(agg, "user_project", ProjectCount.class);
        monthProjectCount.getMappedResults().forEach(pc -> projectCount.set(Integer.parseInt(pc.getMonth()) - 1, pc.getCount()));
        return projectCount;
    }

    private Map<String, Long> calculateYAxis(List<Long> viewCount, List<Long> userCount, List<Long> projectCount) {
        long max = Long.MIN_VALUE;
        for (int i = 0; i < viewCount.size(); i++) {
            max = Math.max(max, Math.max(viewCount.get(i), userCount.get(i) + projectCount.get(i)));
        }

        long k = 1, temp = max;
        while (temp / 10 > 0) {
            k *= 10;
            temp /= 10;
        }
        long yAxisMax = max - max % k + k;

        Map<String, Long> map = new HashMap<>();
        map.put("yAxisMin", 0L);
        map.put("yAxisMax", yAxisMax);
        map.put("yAxisInterval", yAxisMax / 10);

        return map;
    }

    private List<Long> randomCount() {
        return new Random().longs(12, 0, 3000).boxed().collect(Collectors.toList());
    }

    @GetMapping("/statistics")
    public Result statistics() {
        boolean random = true;

        List<Long> viewCount = random ? randomCount() : calculateViewCount();
        List<Long> userCount = random ? randomCount() : calculateUserCount();
        List<Long> projectCount = random ? randomCount() : calculateProjectCount();
        Map<String, Long> yAxis = calculateYAxis(viewCount, userCount, projectCount);

        JSONObject data = new JSONObject();
        data.set("viewCount", viewCount);
        data.set("userCount", userCount);
        data.set("projectCount", projectCount);
        data.set("yAxisMin", yAxis.get("yAxisMin"));
        data.set("yAxisMax", yAxis.get("yAxisMax"));
        data.set("yAxisInterval", yAxis.get("yAxisInterval"));

        return new Result(200, "calculate statistics successfully", data);
    }
}
