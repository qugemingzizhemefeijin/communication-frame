package com.andrcid.process.client.core.hotkey;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

import android.util.Log;

import com.andrcid.process.client.core.context.Global;
import com.andrcid.process.client.core.message.MessageWaitProcessor;
import com.andrcid.process.client.resolve.ResolveFactory;
import com.tigerjoys.communication.protocol.enums.MessageType;
import com.tigerjoys.communication.protocol.exception.ExceptionType;
import com.tigerjoys.communication.protocol.exception.ProtocolException;
import com.tigerjoys.communication.protocol.exception.RemoteException;
import com.tigerjoys.communication.protocol.exception.ThrowErrorHelper;
import com.tigerjoys.communication.protocol.exception.TimeoutException;
import com.tigerjoys.communication.protocol.message.ExceptionMessage;
import com.tigerjoys.communication.protocol.message.IMessage;
import com.tigerjoys.communication.protocol.message.ResponseMessage;
import com.tigerjoys.communication.protocol.utility.FastJsonHelper;

/**
 * DefaultFuture
 * @author chengang
 *
 */
@ThreadSafe
public class DefaultFuture implements Future {
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFuture.class);
	
	/**
	 * 消息ID
	 */
	private final int messageId;
	
	/**
	 * 超时时间
	 */
	private final int timeout;
	
	private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    
    private volatile IMessage response;
    private volatile boolean cancel;
    
    public DefaultFuture(int messageId , int timeout) {
    	this.messageId = messageId;
    	this.timeout = timeout;
    }

	@Override
	public boolean cancel() {
		if(isDone()) {
			return false;
		}
		
		lock.lock();
		try {
			if (!isDone()) {
				this.cancel = true;
				//定时器解除绑定
				MessageWaitProcessor.unregisterEvent(this.messageId);
				//设置返回值为抛出异常
				this.response = new ExceptionMessage(ExceptionType.CANCEL_INVOKE_EXCEPTION.getCode() , "request future has been canceled.");
				//解锁wait锁
				if (done != null) {
					done.signal();
				}
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public String get() throws InterruptedException, RemoteException {
		return get(timeout , TimeUnit.SECONDS);
	}
	
	@Override
	public <T>T get(Class<T> clazz) throws InterruptedException, RemoteException {
		return FastJsonHelper.toObject(get(timeout , TimeUnit.SECONDS), clazz);
	}

	@Override
	public String get(int timeout, TimeUnit unit) throws InterruptedException, RemoteException {
		long t = unit.toMillis(timeout);
		if(t <= 0) {
			t = Global.getInstance().getClientConfig().getAsyncRequestTimeout() * 1000;
		}
		
		if (!isDone()) {
			long start = System.currentTimeMillis();
			lock.lock();
			try {
				while (!this.isDone()) {
                    done.await(t, TimeUnit.MILLISECONDS);
                    if (this.isDone() || System.currentTimeMillis() - start > t) {
                        break;
                    }
                }
			} catch (InterruptedException e) {
				//移除定时器
				MessageWaitProcessor.unregisterEvent(this.messageId);
                throw new RuntimeException(e);
            } catch (Exception e) {
//				LOGGER.error(e.getMessage() , e);
				Log.d("netty msg:", "Error"+e.getMessage()+"");
            } finally {
                lock.unlock();
            }
			
			if (!this.isDone()) {
				//移除定时器
				MessageWaitProcessor.unregisterEvent(this.messageId);
                throw new TimeoutException("messageId " + messageId + " Future Mode wait timeout.");
            }
		}
		return returnFromResponse();
	}
	
	@Override
	public <T> T get(int timeout , TimeUnit unit , Class<T> clazz) throws InterruptedException, RemoteException {
		if(clazz == null) {
			throw new IllegalArgumentException("class can't null");
		}
		
		String res = get(timeout , TimeUnit.SECONDS);
		return ResolveFactory.getInstance().resolve(res, clazz);
	}
	
	/**
	 * 处理回调信息
	 * @return Object
	 * @throws RemoteException
	 */
	private String returnFromResponse() throws RemoteException {
		if (this.response == null) {
            throw new IllegalStateException("response cannot be null");
        }
		
		MessageType type = this.response.messageType();
		switch(type) {
			case Response:
				return ((ResponseMessage)this.response).getBody();
			case Exception:
				ExceptionMessage ex = (ExceptionMessage)this.response;
				throw ThrowErrorHelper.throwServiceError(ex.getErrCode(), ex.getErrMsg());
			default :
				throw new ProtocolException("Unable to process data information!");
		}
	}

	@Override
	public boolean isDone() {
		return this.response != null;
	}
	
	@Override
	public void received(IMessage message) {
		lock.lock();
		try {
			this.response = message;
            if (done != null) {
                done.signal();
            }
		} finally {
            lock.unlock();
        }
	}

}
