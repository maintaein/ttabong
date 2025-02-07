package com.ttabong.service.user;

import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.entity.user.User;
import com.ttabong.entity.user.Volunteer;
import com.ttabong.entity.user.Organization;
import com.ttabong.repository.user.UserRepository;
import com.ttabong.repository.user.VolunteerRepository;
import com.ttabong.repository.user.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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
        Optional<User> userOpt = userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();
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

        // 기본 User 정보 저장 (비밀번호는 암호화)
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .profileImage(request.getProfileImage())
                .isDeleted(false)
                .build();
        user = userRepository.save(user);

        // 봉사자 전용 정보 저장
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

        // 기본 User 정보 저장 (비밀번호는 암호화)
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .profileImage(request.getProfileImage())
                .isDeleted(false)
                .build();
        user = userRepository.save(user);

        // 기관 전용 정보 저장
        Organization organization = Organization.builder()
                .user(user)
                .businessRegNumber(request.getBusinessRegNumber())
                .orgName(request.getOrgName())
                .representativeName(request.getRepresentativeName())
                .orgAddress(request.getOrgAddress())
                .build();
        organizationRepository.save(organization);
    }
}
