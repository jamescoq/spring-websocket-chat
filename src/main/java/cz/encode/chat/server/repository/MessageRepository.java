package cz.encode.chat.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.encode.chat.server.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	public List<Message> findBySessionId(String sessionId);
}
