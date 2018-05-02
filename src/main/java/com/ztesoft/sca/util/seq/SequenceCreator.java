package com.ztesoft.sca.util.seq;


import com.ztesoft.sca.common.Constants;
import com.ztesoft.sca.common.BeanFactory;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author tian.xubo
 * @created 2017 - 10 - 18 15:14
 */
@Component
public class SequenceCreator {
    private static final Logger logger = Logger.getLogger(SequenceCreator.class);
    private static final ScheduledThreadPoolExecutor THREAD_POOL_EXECUTOR ;
    private static final ConcurrentMap<String, AbstractQueue<Long>> SEQ_POOL;
    private static final Long TIME_OUT_GET_SEQUENCE ;
    private static JdbcTemplate jdbcTemplate;
    static {
        ThreadFactory threadFactory = new SequenceThreadFactory();
        THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1,threadFactory);
        SEQ_POOL= new ConcurrentHashMap<String, AbstractQueue<Long>>();
        TIME_OUT_GET_SEQUENCE = Long.parseLong(Constants.sysBundle.getString("sequence.TIME_OUT"));
    }

    public static ConcurrentMap<String, AbstractQueue<Long>> getSeqPool() {
        return SEQ_POOL;
    }

    @PostConstruct
    public  void init(){
        logger.warn("开始初始化所有ALL SEQUENCE...");
        jdbcTemplate = BeanFactory.getApplicationContext().getBean(JdbcTemplate.class);
        Set<String> seqs = Constants.sysBundle.keySet();
        Iterator<String> it = seqs.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = Constants.sysBundle.getString(key);
            String[] args = value.split(",");
            if(args.length == 3 && key.startsWith("sequence")){
                String queueName = key;
                AbstractQueue<Long> queue = new ConcurrentLinkedQueue<Long>();
                SEQ_POOL.put(args[0], queue);// 设置队列到池中
                ObtainSequeceTask task = new ObtainSequeceTask(args[0],Integer.parseInt(args[1]), Integer.parseInt(args[2]),jdbcTemplate);
                THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(task,1000,10,TimeUnit.MILLISECONDS);
                logger.warn("初始化sequence=" + queueName + "中...");
            }
            //args[0],, queue
        }
    }
    public static Long getSequence(String seqName) throws Exception{
        Long seq = null;
        long start = System.currentTimeMillis();
        AbstractQueue<Long> queue = SEQ_POOL.get(seqName);
        if(queue == null){
            logger.error("this sequence does not exist! seqName is : "+seqName);
            throw new Exception("this sequence does not exist! seqName is : "+seqName);
        }
        while(seq == null && (System.currentTimeMillis()-start)<TIME_OUT_GET_SEQUENCE){
            //设置超时时间，如果返回为空 会一直尝试重新获取
            seq = queue.poll();
        }
        //超时直接查数据库
        if(seq == null){
            try {
                seq = getOracleSeq(seqName);
            } catch (Exception e) {
                throw  e;
            }
        }
        return seq;
    }
    public static Long getOracleSeq(String seqName)throws Exception{
        String sql = "SELECT " + seqName + ".nextval FROM dual";
        long nextValue = 0;
        try {
            nextValue = jdbcTemplate.queryForObject(sql, Long.class);
        } catch (DataAccessException e) {
            throw  e;
        }
        return nextValue;
    }

}
