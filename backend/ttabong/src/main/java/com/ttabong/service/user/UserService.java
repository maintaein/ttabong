package com.ttabong.service.user;

import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;

public interface UserService {
    long login(LoginRequest loginRequest);

    void registerVolunteer(VolunteerRegisterRequest request);

    void registerOrganization(OrganizationRegisterRequest request);

    boolean checkEmail(String email, String type);
}
