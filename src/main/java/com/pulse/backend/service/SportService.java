package com.pulse.backend.service;
import com.pulse.backend.entity.Sport;
import com.pulse.backend.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SportService {
    private final SportRepository sportRepository;

    public List<Sport> getAllSports(){
        return sportRepository.findAll();
    }
}
