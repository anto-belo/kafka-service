package com.itechart.kafkaservice.service;

import com.itechart.kafkaservice.dto.EventDto;

public interface TargetService {
    void invoke(EventDto event);
}
