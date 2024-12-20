package ru.selholper.comment.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.selholper.comment.dto.CommentDTO;
import ru.selholper.comment.model.Comment;
import ru.selholper.comment.request.CommentRequest;
import ru.selholper.comment.service.CommentService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService, ObjectMapper objectMapper) {
        this.commentService = commentService;
    }

    @GetMapping("/getComments/{fundraiserId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long fundraiserId) {
        List<Comment> comments = commentService.findByFundraiserIdByOrderByCreatedAtDesc(fundraiserId);
        List<CommentDTO> commentDTOs = comments.stream().map(CommentDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    @PostMapping("/addComment")
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentRequest commentRequest) {
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());
        newComment.setFundraiserId(commentRequest.getFundraiserId());
        newComment.setUsername(commentRequest.getUsername());
        CommentDTO commentDTO = CommentDTO.fromEntity(commentService.save(newComment));
        return ResponseEntity.ok(commentDTO);
    }
    
    @DeleteMapping("/deleteComment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}
