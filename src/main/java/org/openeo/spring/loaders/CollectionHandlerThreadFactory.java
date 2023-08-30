package org.openeo.spring.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * Thread factory for executors that catches and logs uncaught
 * exceptions in threads.
 */
public class CollectionHandlerThreadFactory extends CustomizableThreadFactory {

    final Logger logger = LogManager.getLogger(CollectionHandlerThreadFactory.class);

    private final String engineType;

    public CollectionHandlerThreadFactory(String engineType, String threadNameprefix) {
        super(threadNameprefix);
        this.engineType = engineType;
    }

    public CollectionHandlerThreadFactory(String engineType) {
        super();
        this.engineType = engineType;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = super.newThread(r);
        thread.setUncaughtExceptionHandler((t, e) -> {
            logger.error("Error while handling coverage {}.", engineType, e);
        });
        return thread;
    }

    private static final long serialVersionUID = -1286843491619265844L;
}
