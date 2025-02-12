package com.ttabong.service.user;

import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;

public interface UserService {
    String login(LoginRequest loginRequest);

    String registerVolunteer(VolunteerRegisterRequest request);

    String registerOrganization(OrganizationRegisterRequest request);

    boolean checkEmail(String email, String type);
}
