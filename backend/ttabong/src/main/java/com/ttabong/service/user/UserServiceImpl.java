package com.ttabong.service.user;

import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.UserLoginProjection;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.entity.user.Organization;
import com.ttabong.entity.user.User;
import com.ttabong.entity.user.Volunteer;
import com.ttabong.repository.user.OrganizationRepository;
import com.ttabong.repository.user.UserRepository;
import com.ttabong.repository.user.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final OrganizationRepository organizationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           VolunteerRepository volunteerRepository,
                           OrganizationRepository organizationRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.volunteerRepository = volunteerRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public long login(LoginRequest loginRequest) {
        Optional<UserLoginProjection> userOpt = userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        UserLoginProjection user = userOpt.get(); // 아이디와 비밀번호만 가져오도록 프로젝션추가

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user.getId();
    }

    @Override
    public void registerVolunteer(VolunteerRegisterRequest request) {
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new RuntimeException("이미 계정이 존재합니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .profileImage(request.getProfileImage())
                .createdAt(Instant.now())
                .isDeleted(false)
                .totalVolunteerHours(BigDecimal.valueOf(0))
                .build();
        user = userRepository.save(user);

        Volunteer volunteer = Volunteer.builder()
                .user(user)
                .preferredTime(request.getPreferredTime())
                .interestTheme(request.getInterestTheme())
                .durationTime(request.getDurationTime())
                .region(request.getRegion())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .recommendedCount(0)
                .notRecommendedCount(0)
                .build();
        volunteerRepository.save(volunteer);
    }

    @Override
    public void registerOrganization(OrganizationRegisterRequest request) {
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new RuntimeException("이미 계정이 존재합니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .profileImage(request.getProfileImage())
                .createdAt(Instant.now())
                .isDeleted(false)
                .totalVolunteerHours(BigDecimal.valueOf(0))
                .build();
        user = userRepository.save(user);

        Organization organization = Organization.builder()
                .user(user)
                .businessRegNumber(request.getBusinessRegNumber())
                .orgName(request.getOrgName())
                .representativeName(request.getRepresentativeName())
                .orgAddress(request.getOrgAddress())
                .build();
        organizationRepository.save(organization);
    }

    @Override
    public boolean checkEmail(String email, String type) {
        //이메일이 있다면 true, 없다면 false를 반환하는데, 이를 상황에 따라 어떻게 응답할 지는 컨트롤러에서 정할 것임.
        return userRepository.existsByEmailAndIsDeletedFalse(email);
    }
}
