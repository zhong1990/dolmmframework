/**
 * dol-framework-data
 * CounterTransactionStatus.java
 * org.dol.framework.data
 * TODO
 *
 * @author dolphin
 * @date 2015年12月7日 上午11:42:38
 * @Copyright 2015, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.data;

import org.springframework.transaction.TransactionStatus;

/**
 * ClassName:CounterTransactionStatus <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年12月7日 上午11:42:38 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class CounterTransactionStatus {

    private TransactionStatus transactionStatus;
    private int counter;
    public CounterTransactionStatus(TransactionStatus transactionStatus) {
        setTransactionStatus(transactionStatus);
        this.counter = 0;

    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public int getCounter() {
        return counter;
    }

    public void inreaseCounter() {
        counter = counter + 1;
    }

    public void decreaseCounter() {
        counter = counter - 1;
    }

}
