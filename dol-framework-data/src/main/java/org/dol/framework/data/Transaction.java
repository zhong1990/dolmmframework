package org.dol.framework.data;

import org.dol.framework.logging.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class Transaction {

    private static final Logger LOGGER = Logger.getLogger(Transaction.class);
    private TransactionStatus transactionStatus;
    private PlatformTransactionManager transactionManager;
    private DefaultTransactionDefinition transactionDefinition;

    Transaction(PlatformTransactionManager transactionManager, DefaultTransactionDefinition transactionDefinition) {
        this.transactionManager = transactionManager;
        this.transactionDefinition = transactionDefinition;
    }

    /**
     * 开始事务
     */
    public void begin() {
        if (transactionStatus != null) {
            throw new RuntimeException("事务已经开启");
        }
        this.transactionStatus = transactionManager.getTransaction(transactionDefinition);
    }

    /**
     * 提交事务
     */
    public void commit() {
        transactionManager.commit(transactionStatus);
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        try {
            if (transactionStatus == null) {
                return;
            }
            if (!transactionStatus.isCompleted()) {
                transactionManager.rollback(transactionStatus);
            }
        } catch (Throwable e) {
            LOGGER.error("rollback", "回滚事务失败", e);
        }
    }
}
