package com.ztesoft.sca.dao;

import java.util.List;

import com.ztesoft.sca.model.AiChatDto;

public interface AiChatDao {

	public void insertAiChat(AiChatDto aiChat) throws Exception;

	public List<AiChatDto> selectAichatRules(long cId) throws Exception;

	public void updateAiChatTask(AiChatDto aiChatUpdate) throws Exception;

	public List<AiChatDto> selectAichatTaskList(String receiveStatus) throws Exception;

	public void updateAiChatTaskInStr(List<Long> listId) throws Exception;

}
