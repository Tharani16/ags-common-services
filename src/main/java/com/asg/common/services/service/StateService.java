package com.asg.common.services.service;

import com.asg.common.services.entity.State;
import com.asg.common.services.exceptions.ResourceNotFoundException;
import com.asg.common.services.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {

    private final StateRepository stateRepository;
    @Autowired
    public StateService(StateRepository stateRepository){
        this.stateRepository = stateRepository;
    }

    public List<State> getStatesByCountry(Long countryPoid) {

        if (!stateRepository.existsByCountryPoid(countryPoid)) {
            throw new ResourceNotFoundException("Country", "countryPoid", countryPoid);
        }
        return stateRepository.findByCountryPoidAndActive(countryPoid, "Y");
    }

}
