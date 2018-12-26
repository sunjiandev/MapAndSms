package com.sunkaisens.gisandsms.utils;

import android.util.Log;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池代理类
 * @author sun
 */
public class ThreadPoolProxy {

    private static final String TAG =  ThreadPoolProxy.class.getCanonicalName();
    private static Serializable serializable;
    ThreadPoolExecutor mThreadPoolExecutor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static ThreadPoolProxy threadPoolProxy;

    private ThreadPoolProxy() {
    }

    public static ThreadPoolProxy getInstence(Serializable serializable) {
        ThreadPoolProxy.serializable = serializable;
        if (threadPoolProxy == null) {
            synchronized (ThreadPoolProxy.class) {
                if (threadPoolProxy == null) {
                    threadPoolProxy = new ThreadPoolProxy();
                }
            }
        }

        return threadPoolProxy;

    }

    private ThreadPoolExecutor initExecutor() {
        if (mThreadPoolExecutor == null) {
            synchronized (ThreadPoolProxy.class) {
                if (mThreadPoolExecutor == null) {

                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
                    LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

                    mThreadPoolExecutor = new ThreadPoolExecutor(
                            //核心线程数
                            CORE_POOL_SIZE,
                            //最大线程数
                            MAXIMUM_POOL_SIZE,
                            //保持时间
                            KEEP_ALIVE_SECONDS,
                            //保持时间对应的单位
                            unit,
                            workQueue,
                            //线程工厂
                            threadFactory,
                            //异常捕获器
                            handler);
                }
            }
        }
        Log.d(TAG,"CORE_POOL_SIZE : " + CORE_POOL_SIZE + "  MAXIMUM_POOL_SIZE：   " + MAXIMUM_POOL_SIZE + "   KEEP_ALIVE_SECONDS：  " + KEEP_ALIVE_SECONDS);
        return mThreadPoolExecutor;
    }


    /**
     * 执行任务
     */
    public void executeTask(Runnable r) {
        initExecutor();
        mThreadPoolExecutor.execute(r);
    }


    /**
     * 提交任务
     */
    public Future<?> commitTask(Runnable r) {
        initExecutor();
        return mThreadPoolExecutor.submit(r);
    }

    /**
     * 删除任务
     */
    public void removeTask(Runnable r) {
        initExecutor();
        mThreadPoolExecutor.remove(r);
    }
    public Serializable getSerializable(){
        return serializable;
    }

}