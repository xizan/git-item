package com.ztesoft.sca.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.ztesoft.sca.model.AiChatDto;

@Component
public class AiChatDaoImpl implements AiChatDao{
	private JdbcTemplate jdbcTemplate;
	
    @Autowired
    public AiChatDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
	@Override
	public void insertAiChat(AiChatDto aiChat) throws Exception {
        StringBuffer sqlBuf = new StringBuffer("insert into SCA_RECEIVE_MSG" +
                "" +
                "  (RECEIVE_ID, RECEIVE_FILEID, RECEIVE_FILECODEC, RECEIVE_STATUS,RECEIVE_FILEPATH)" +
                "" +
                "VALUES" +
                "" +
                "  (?, ?, ?,?,?)");
        Object[] param = new Object[]{aiChat.getReceiveId(),aiChat.getReceiveFileid(),aiChat.getReceiveFilecodec(),aiChat.getReceiveStatus(),aiChat.getReceiveFilepath()};
        this.jdbcTemplate.update(sqlBuf.toString(),param);
	}

	@Override
	public List<AiChatDto> selectAichatRules(long cId) {
        String sqlStr="select  a.rule_id from qa_rule a, qa_scene_rule b where b.scene_id =? and a.rule_id = b.rule_id ";
        StringBuffer sqlBuf =new StringBuffer(sqlStr);
        Object[] param = new Object[]{cId};
        List<AiChatDto> list = this.jdbcTemplate.query(sqlBuf.toString(),param,BeanPropertyRowMapper.newInstance(AiChatDto.class));
        return list;
	}

	@Override
	public void updateAiChatTask(AiChatDto aiChatUpdate) throws Exception {
        StringBuffer sqlBuf = new StringBuffer("update SCA_RECEIVE_MSG set RECEIVE_TASKID=? where RECEIVE_ID=? AND RECEIVE_FILEID=?");
        Object[] param = new Object[]{aiChatUpdate.getReceiveTaskid(),aiChatUpdate.getReceiveId(),aiChatUpdate.getReceiveFileid()};
        this.jdbcTemplate.update(sqlBuf.toString(),param);
	}

	@Override
	public List<AiChatDto> selectAichatTaskList(String receiveStatus) throws Exception {
        String sqlStr="select RECEIVE_ID,RECEIVE_TASKID,RECEIVE_FILEID from SCA_RECEIVE_MSG where RECEIVE_STATUS=?";
        StringBuffer sqlBuf =new StringBuffer(sqlStr);
        Object[] param = new Object[]{receiveStatus};
        List<AiChatDto> list = this.jdbcTemplate.query(sqlBuf.toString(),param,BeanPropertyRowMapper.newInstance(AiChatDto.class));
        return list;
	}

	@Override
	public void updateAiChatTaskInStr(List<Long> listId) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("listId", listId);
        String sql = "update SCA_RECEIVE_MSG set RECEIVE_STATUS=1 where RECEIVE_ID in (:listId)";
        namedParameterJdbcTemplate.update(sql, params);
	}

}
