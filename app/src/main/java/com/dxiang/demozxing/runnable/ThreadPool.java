package com.dxiang.demozxing.runnable;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 作者：dongixang
 * 时间：2017/12/21 15:54
 * 功能：
 * 使用：
 */

public class ThreadPool {
    private static ThreadPool pool;
    private static ThreadPoolExecutor executor;
    private static Lock lock=new ReentrantLock();
    public static ThreadPoolExecutor get(){
        if (pool==null){
            lock.lock();
            if (pool==null){
                pool=new ThreadPool();
                return executor;
            }
            lock.unlock();
        }
        return executor;
    }

    public ThreadPool() {
        executor =  new  ThreadPoolExecutor(
                1,
                10,
                2,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

}
