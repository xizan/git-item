package com.ztesoft.sca.util.seq;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author tian.xubo
 * @created 2017 - 10 - 18 16:23
 */
public class ObtainSequeceTask implements Runnable {
    private static final Logger logger = Logger.getLogger(ObtainSequeceTask.class);
    private String seqName;
    private Integer maxSize;
    private Integer batchSize;
    private JdbcTemplate jdbcTemplate ;

    public ObtainSequeceTask(String seqName, Integer maxSize, Integer batchSize,JdbcTemplate jdbcTemplate) {
        this.seqName = seqName;
        this.maxSize = maxSize;
        this.batchSize = batchSize;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        ConcurrentMap<String, AbstractQueue<Long>> map = SequenceCreator.getSeqPool();
        AbstractQueue<Long> queue = map.get(this.seqName);
        if(queue ==null){
          queue = new ConcurrentLinkedQueue<Long>();
          map.putIfAbsent(this.seqName,queue);
          paddingSequence(queue);
        }else{
            paddingSequence(queue);
        }

    }

    public void paddingSequence(AbstractQueue<Long> queue){
        if(queue.size()<maxSize){
            Long seq = null;
            try {
                seq = this.getOracleSeq();
            } catch (Exception e) {
                logger.error(this.seqName+" sequences get from oracle database occur a error",e);
                Thread.currentThread().interrupt();
            }
            if(seq != null){
                List<Long> temp = new ArrayList<Long>();
                for (int i = 0; i < batchSize; i++) {
                    Long s = seq++;
                    temp.add(s);
                }
                queue.addAll(temp);
            }else{
                logger.error(this.seqName+" sequences get from oracle database is null");
                Thread.currentThread().interrupt();
            }

        }else{
            if(logger.isDebugEnabled()){
                logger.debug(this.seqName+" is full ");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(this.seqName+" sleep occur a error ",e);
            }
        }

    }
    public Long getOracleSeq()throws Exception{
        String sql = "SELECT " + this.seqName + ".nextval FROM dual";
        long nextValue = 0;
        try {
            nextValue = this.jdbcTemplate.queryForObject(sql, Long.class);
        } catch (DataAccessException e) {
            throw  e;
        }
        return nextValue;
    }
}
