package com.itechart.kafkaservice.service.impl;

import com.itechart.kafkaservice.dto.EventDto;
import com.itechart.kafkaservice.service.TargetService;
import org.springframework.stereotype.Service;

@Service
public class TargetServiceImpl implements TargetService {
    @Override
    public void invoke(EventDto event) {
        System.out.printf("Target service method invoked with event from %s created at %s%n",
                event.getSource(), event.getCreated());
    }
}
