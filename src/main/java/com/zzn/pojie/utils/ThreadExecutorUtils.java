package com.zzn.pojie.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: LEIYU
 */
public class ThreadExecutorUtils {

    /**
     * 直播数据推送
     */
    public final static ThreadPoolExecutor LiveDataPushExecutor = new ThreadPoolExecutor(50, 2000,
            300L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5000),
            new ThreadFactoryBuilder().setNameFormat("calcUserEarningsRate-task-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    /**
     * 直播数据推送
     */
    public final static ThreadPoolExecutor POJIE_EXECUTOR = new ThreadPoolExecutor(8, 40,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500),
            new ThreadFactoryBuilder().setNameFormat("pojieZip-task-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
    );

}
