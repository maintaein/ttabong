package com.ttabong.service.user;

import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.entity.user.User;

public interface UserService {
    User login(LoginRequest loginRequest);
    User registerVolunteer(VolunteerRegisterRequest request);
    User registerOrganization(OrganizationRegisterRequest request);
}
