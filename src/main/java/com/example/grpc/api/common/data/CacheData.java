package com.example.grpc.api.common.data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.grpc.api.common.logger.Logger;

import lombok.Getter;
import lombok.Setter;

public abstract class CacheData<FO> {
	private static final Logger logger = Logger.getLogger(CacheData.class.getSimpleName());
	@Setter private static int RETRY = 1;
	private final Lock lock = new ReentrantLock();
	private FO object;
	private FO cacheObject;
	@Getter private Status status;

	enum Status {
		SUCCESS,
		FAILURE,
		BLOCK;
	}

	public void load() {
		Status status = null;

		if (lock.tryLock()) {
			System.out.println("CacheData loading ...");
			// cacheにsetしてからrefreshし、更新までの期間はcacheを返す
			setToCacheObject();
			setLoadStatus(Status.BLOCK);
			try {
				refresh();
				go:
				{
					for (int t = 0; t < RETRY; t++) {
						// setLoadTime();
						status = load0();
					}
				}
			} finally {
				lock.unlock();
			}
		}
		setLoadStatus(status);
		if (status == Status.SUCCESS) {
			refreshCache();
			logger.info(this.getClass().getSimpleName() + " is loaded");
		}
	}

	protected abstract Status load0();

	private void refresh() {
		this.object = null;
	}

	private void refreshCache() {
		this.cacheObject = null;
	}

	protected void setObject(FO object) {
		this.object = object;
	}

	protected void setToCacheObject() {
		this.cacheObject = this.object;
	}

	protected FO getActiveObject() {
		System.out.println("status -> " + status);
		switch (status) {
			case SUCCESS:
				return this.object;
			case BLOCK:
				return this.cacheObject;
			default:
				return null;
		}
	}

	private void setLoadStatus(Status status) {
		this.status = status;
	}
}