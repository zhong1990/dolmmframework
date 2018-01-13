package org.dol.framework.data;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 事务创建工厂类
 *
 * @author dolphin
 */
public class TransactionFactory extends DefaultTransactionDefinition {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private PlatformTransactionManager transactionManager;

    public TransactionFactory(PlatformTransactionManager transactionManager) {
        setTransactionManager(transactionManager);
    }

    public TransactionFactory() {
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;

    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * 创建一个事务对象
     *
     * @return
     */
    public Transaction create() {
        return new Transaction(transactionManager, this);
    }
}
