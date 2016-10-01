package com.freetrip.trekker.manager;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理者 ,单例的
 * 
 * @author mwp
 *
 */
public class ThreadManager {

	// =====================将线程管理者设为单例的=========================
	private ThreadManager() {

	}

	private static ThreadManager instance = new ThreadManager();

	/**
	 * 返回线程管理者的唯一实例
	 * @return
	 */
	public static ThreadManager getInstance() {
		return instance;
	}

	// =========================================================

	private ThreadPoolProxy longPool;
	private ThreadPoolProxy shortPool;

	/**
	 * 像联网到长耗时操作开启一个管理长耗时线程的线程池
	 * 
	 * @return
	 */
	public synchronized ThreadPoolProxy getThreadFromLongPool() {
		if (longPool == null) {
			//最佳开启线程数：  cpu的核数*2+1
			longPool = new ThreadPoolProxy(5, 5, 5000l);
		}

		return longPool;
	}

	/**
	 * 像操作本地文件等短耗时操作开启一个管理短耗时线程的线程池
	 * 
	 * @return
	 */
	public synchronized ThreadPoolProxy getThreadFromShortPool() {
		if (shortPool == null) {
			shortPool = new ThreadPoolProxy(3, 3, 5000l);
		}

		return shortPool;
	}

	/** 线程池代理 */
	public class ThreadPoolProxy {
		private ThreadPoolExecutor pool;//Java 规范提供的线程池
		private int corePoolSize;
		private int maximumPoolSize;
		private long time;

		/**
		 * 
		 * @param corePoolSize
		 *            线程池里面最大管理多少个线程
		 * @param maximumPoolSize
		 *            如果排队满了，需要额外在开启的线程数
		 * @param time
		 *            如果线程池已经没有要执行的任务，需要存活多久，毫秒
		 */
		public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long time) {
			super();
			this.corePoolSize = corePoolSize;
			this.maximumPoolSize = maximumPoolSize;
			this.time = time;
		}

		/**
		 * 执行任务
		 * 
		 * @param runnable
		 */
		public void execute(Runnable runnable) {
			if (pool == null) {// 只有第一次指向任务时才创建线程池（懒加载）
				/*
				 * 1.线程池里面管理多少个线程，即使他们是空闲的 2.如果排队满了，需要额外在开启的线程数
				 * 3.如果线程池已经没有要执行的任务，需要存活多久 4.参数3的时间单位,一般用毫秒
				 * 5.如果线程池里管理的线程都已将用了，再有新的需要执行的异步任务，临时保存到LinkedBlockingQueue中去排队
				 */
				pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
						time, TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<Runnable>(10));
			}

			pool.execute(runnable);// 调用线程池，执行异步任务
		}

		/**
		 * 取消任务
		 * 
		 * @param runnable
		 */
		public void cancel(Runnable runnable) {
			if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
				pool.remove(runnable);// 取消异步任务
			}
		}

	}

}
