package com.yukina.thumbbackend.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import com.yukina.thumbbackend.constant.ThumbConstant;
import com.yukina.thumbbackend.service.BlogService;
import com.yukina.thumbbackend.service.ThumbService;
import com.yukina.thumbbackend.util.RedisKeyUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class SyncThumb2DBCompensatoryJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SyncThumb2DBJob syncThumb2DBJob;

    @Scheduled(cron = "0 2 2 * * *")
    public void run() {
        log.info("开始补偿数据");
        Set<String> thumbKeys = redisTemplate.keys(RedisKeyUtil.getTempThumbKey("") + "*");
        Set<String> needHandleDateSet = new HashSet<>();
        thumbKeys.stream().filter(ObjUtil::isNotNull).forEach(thumbKey -> needHandleDateSet
                .add(thumbKey.replace(ThumbConstant.TEMP_THUMB_KEY_PREFIX.formatted(""), "")));
        if (CollUtil.isEmpty(needHandleDateSet)) {
            log.info("没有需要补偿的临时数据");
        }
        // 补偿1小时前的数据数据，避免重复处理
        for (String dateStr : needHandleDateSet) {
            DateTime datetime= DateUtil.parse(dateStr, "HH:mm:ss");
            if(datetime.before(DateUtil.offsetHour(DateUtil.date(), -1))) {
                syncThumb2DBJob.syncThumb2DBByDate(dateStr);
            }
        }
        log.info("临时数据补偿完成");
    }
}
