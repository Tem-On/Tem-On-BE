package com.example.temon.queuestockservice.queue.ctrl;

import com.example.temon.queuestockservice.queue.domain.dto.QueueAvailableResponse;
import com.example.temon.queuestockservice.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/queue")
@RequiredArgsConstructor
public class InternalQueueCtrl {

    private final QueueService queueService;

    @GetMapping("/available")
    public ResponseEntity<QueueAvailableResponse> getAvailable(
            @RequestParam Long eventProductId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
                queueService.getAvailable(eventProductId, userId)
        );
    }

    @PostMapping("/expire")
    public ResponseEntity<Void> expire(
            @RequestParam Long eventProductId
    ) {
        queueService.expire(eventProductId);
        return ResponseEntity.ok().build();
    }
}