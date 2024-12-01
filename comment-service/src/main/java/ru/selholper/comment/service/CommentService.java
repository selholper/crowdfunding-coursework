package ru.selholper.comment.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.selholper.comment.model.Comment;
import ru.selholper.comment.repository.CommentRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {

	@Autowired 
	private CommentRepository commentRepository;
	
	@Transactional
	public Comment save(Comment comment) {
		comment.setCreatedAt(new Date());
		return commentRepository.saveAndFlush(comment);
	}
	
	public List<Comment> findAllByOrderByCreatedAtDesc(){
		return commentRepository.findAllByOrderByCreatedAtAsc();
	}
	
	public List<Comment> findByFundraiserIdByOrderByCreatedAtDesc(Long postId){
		return commentRepository.findByFundraiserId(postId);
	}
	
	@Transactional
	public void deleteComment(Long commentId) {
		Optional<Comment> comment = commentRepository.findById(commentId);
		if(comment.isPresent()) {
			commentRepository.deleteById(commentId);
		} else {
            throw new RuntimeException("Comment not found.");
        }
	}
}
