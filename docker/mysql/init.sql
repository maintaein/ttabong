-- 봉사활동 매칭 플랫폼 데이터베이스 설계
-- 에러 방지를 위해 모든 DB & table 생성 앞에 if exists를 붙임.
-- 기존 데이터베이스 삭제 및 생성
DROP DATABASE IF EXISTS volunteer_service;
CREATE DATABASE IF NOT EXISTS volunteer_service;
USE volunteer_service;

/* 
테이블 생성을 먼저 할 것인데, 외래키 참조 순서에 따라 에러 없이 생성하기 위해 순서를 정했음!
외래키 참조 순서는 일단 다음과 같이 하였음:

테이블 생성 순서:
1. User
2. Volunteer
3. Organization
4. Template_group
5. Template
6. Recruit
7. Application
8. OrganizationReview
9. OrganizationReviewImage
10. VolunteerReview
11. ReviewImage
12. ReviewComment
13. VolunteerReaction
14. main_category
15. sub_category
*/


-- 전체 유저 테이블 생성
DROP TABLE IF EXISTS User;
CREATE TABLE User (
    user_id        INT AUTO_INCREMENT PRIMARY KEY COMMENT '유저 ID',
    email          VARCHAR(80) NOT NULL UNIQUE COMMENT '유저 이메일 (로그인 ID)',
    name           VARCHAR(50)  NOT NULL COMMENT '유저 이름',
    password       VARCHAR(256) NOT NULL COMMENT '비밀번호(해시로 바꾸고 넣어야 함)',
    phone          VARCHAR(20)  NOT NULL COMMENT '전화번호',
    total_volunteer_hours DECIMAL(7,2) DEFAULT 0.00 NOT NULL, -- 총 봉사 시간 (소수점 2자리까지 허용, 최대 99999.99 시간)
    profile_image  VARCHAR(200) COMMENT '프로필 사진 경로', -- 일단 널값. 회원이 이미지를 업로드하면 그때 보이도록 반영
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입 일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='전체 유저 정보 테이블';

-- 봉사자 추가 정보 테이블
DROP TABLE IF EXISTS Volunteer; 
CREATE TABLE Volunteer(
   volunteer_id   INT AUTO_INCREMENT NOT NULL COMMENT '봉사자 아이디_자동증가값 받는 최소성 고유키',
    user_id        INT NOT NULL COMMENT 'User(user_id) 참조',
    preferred_time   VARCHAR(100) COMMENT '봉사 선호 시간',-- 시간을 어떻게 받아서 추천해줄지 고민 중에 있음.!!
    interest_theme   VARCHAR(100) COMMENT '관심 봉사 테마', -- 이것을 받아서 추천해줘야 하는데, 크롤링으로 테마를 어떻게 가져오느냐에 따라 다를 듯!
    duration_time    VARCHAR(100) COMMENT '봉사 선호 소요시간', 
    region           VARCHAR(30)  COMMENT '지역', -- 30바이트필요
    birth_date         DATE         COMMENT '생년월일',
    gender             CHAR(1)      COMMENT '성별(M/F)',
    recommended_count INT         DEFAULT 0 COMMENT '추천 횟수',
    not_recommended_count INT     DEFAULT 0 COMMENT '비추천 횟수',
    -- updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최종 수정 일시',
    PRIMARY KEY (volunteer_id),
    CONSTRAINT fk_volunteer_extra_user
        FOREIGN KEY (user_id) REFERENCES User(user_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; COMMENT='봉사자 추가 정보 테이블';

-- 봉사기관 기본 정보 테이블
DROP TABLE IF EXISTS Organization;
CREATE TABLE Organization (
    org_id               INT AUTO_INCREMENT PRIMARY KEY COMMENT '기관 ID_ 지금은 쓸 일이 없지만 테이블별로 관리할 기본키가 있어야 하기 때문에 생성함!', 
    user_id              INT NOT NULL COMMENT 'User(user_id) 참조',
    business_reg_number  VARCHAR(30)  NOT NULL COMMENT '사업자등록번호',
    org_name             VARCHAR(100) NOT NULL COMMENT '회사/점포명',
    representative_name  VARCHAR(80)  NOT NULL COMMENT '대표 담당자명',
    org_address          VARCHAR(200) NOT NULL COMMENT '주소',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입 일시',
    CONSTRAINT fk_organization_user
        FOREIGN KEY (user_id) REFERENCES User(user_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='봉사기관 기본 정보';

-- category 테이블 (main이랑 sub 합침)
DROP TABLE IF EXISTS Category;
CREATE TABLE Category (
    category_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'category PK',
    name VARCHAR(50) NOT NULL COMMENT '카테고리 이름',
    parent_id INT NULL COMMENT '부모 카테고리 ID, NULL이면 대분류',
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id) REFERENCES Category(category_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- 기관 측은 관련 템플릿들을 하나로 묶어(그룹화) 관리할 수 있음
DROP TABLE IF EXISTS Template_group;
CREATE TABLE IF NOT EXISTS Template_group (
    group_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '템플릿그룹 id',
    org_id INT NOT NULL COMMENT '해당 그룹을 관리 및 이용 중인 기관ID',
    group_name VARCHAR(100) NOT NULL DEFAULT '봉사' COMMENT '그룹명(사용자가 입력할 수 있음_기본은 "봉사")',
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    CONSTRAINT fk_group_org
		FOREIGN KEY (org_id) REFERENCES Organization(org_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS Template;
CREATE TABLE IF NOT EXISTS Template (
    template_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '개별 템플릿 id',
    group_id INT NOT NULL COMMENT '그룹id',
    org_id INT COMMENT '관련 기관 id',
    category_id INT COMMENT '봉사 분류',
    title VARCHAR(255) COMMENT '공고 제목(봉사 제목)',
    activity_location VARCHAR(255) NOT NULL COMMENT '봉사 활동 장소(재택 또는 도로명주소)',
    status ENUM('ALL', 'YOUTH', 'ADULT') NOT NULL DEFAULT 'ALL' COMMENT '봉사자 유형',
    image_id VARCHAR(500) COMMENT '이미지 경로',
    contact_name VARCHAR(50) COMMENT '담당자명',
    contact_phone VARCHAR(20) COMMENT '담당자 연락처',
    description VARCHAR(500) COMMENT '봉사활동 상세 내용',
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
	created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '템플릿 생성 일시',
    CONSTRAINT fk_template_group
        FOREIGN KEY (group_id) REFERENCES Template_group(group_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT fk_template_org
        FOREIGN KEY (org_id) REFERENCES Organization(org_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT fk_template_category
        FOREIGN KEY (category_id) REFERENCES Category(category_id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS Recruit;
CREATE TABLE IF NOT EXISTS Recruit (
    recruit_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '봉사공고 ID',
    template_id INT NOT NULL COMMENT '개별 템플릿 id',
    deadline DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '공고 마감일 (디폴트는 오늘 날짜)',
    activity_date DATETIME NOT NULL COMMENT '봉사활동 날짜',
    activity_start DECIMAL(7,2) DEFAULT 0.00 NOT NULL COMMENT '활동 시작하는는 시간(=봉사해야하는 시간)', 
    activity_end DECIMAL(7,2) DEFAULT 0.00 NOT NULL COMMENT '활동 끝나는 시간(=봉사해야하는 시간)', 
    max_volunteer INT DEFAULT 0 COMMENT '모집할 봉사자 수',
    participate_vol_count INT DEFAULT 0 COMMENT '참여한 봉사자 수', -- 봉사가 끝난 후, 몇 명이 참여했는지
    status ENUM('RECRUITING', 'RECRUITMENT_CLOSED', 'ACTIVITY_COMPLETED')
          NOT NULL DEFAULT 'RECRUITING' COMMENT '공고 모집 상태',
	is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시각', -- 새로 추가한 컬럼
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '공고 등록 일시',
    CONSTRAINT fk_recruit_template_id
        FOREIGN KEY (template_id) REFERENCES Template (template_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;


-- 봉사공고 신청 테이블 (자동 취소 상태 및 평가 여부 추가)
DROP TABLE IF EXISTS Application;
CREATE TABLE Application (
    application_id     INT AUTO_INCREMENT PRIMARY KEY COMMENT '신청 ID',
    volunteer_id INT NOT NULL COMMENT '신청한 봉사자', -- 봉사자만 신청할 수 있으니까 user_id가 아닌, volunteer_id로 함!
    recruit_id   INT NOT NULL COMMENT '신청 대상 공고',
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED', 'AUTO_CANCEL', 'NO_SHOW')
          NOT NULL DEFAULT 'PENDING' COMMENT '신청 상태', -- 신청하면 생기는 데이터니까 디폴트가 PENDING임
    evaluation_done BOOLEAN DEFAULT FALSE COMMENT '평가 여부',
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '신청 일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '상태 변경 일시', -- 새로 추가한 컬럼
    CONSTRAINT fk_application_volunteer
        FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_application_recruit
        FOREIGN KEY (recruit_id) REFERENCES Recruit(recruit_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='봉사공고 신청 상태 관리 테이블';

-- 리뷰 이미지
DROP TABLE IF EXISTS Review_image;
CREATE TABLE IF NOT EXISTS Review_image (
    image_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '이미지 id',
    review_id INT NOT NULL COMMENT '어느 후기에 대한 이미지인가?',
    image_url VARCHAR(500) NOT NULL COMMENT '이미지 url',
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '이미지 생성시간',
    next_image_id INT NULL DEFAULT NULL COMMENT '다음에 올 이미지 id',
    CONSTRAINT fk_next_image_id
		FOREIGN KEY (next_image_id) REFERENCES Review_image(image_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- 후기 테이블(쓰레드를 열어줌)
DROP TABLE IF EXISTS Review;
CREATE TABLE Review (
    review_id      INT AUTO_INCREMENT PRIMARY KEY COMMENT '후기 ID', 
    parent_review_id INT NULL DEFAULT NULL COMMENT '자기참조 (FK)',
    group_id INT NULL DEFAULT NULL COMMENT '비슷한 것을 거르기 위해 생성',
    recruit_id INT NULL DEFAULT NULL COMMENT '어느 공고에 대한 글인가? (FK)',
    org_id     	   INT NOT NULL COMMENT '작성하는 기관 (FK)',
    writer_id INT NULL DEFAULT NULL COMMENT '작성자 ID(FK)',
    title	VARCHAR(255) COMMENT '후기 제목 부분',
    content        VARCHAR(500) NOT NULL COMMENT '후기 내용',
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    is_public 	   BOOLEAN DEFAULT TRUE COMMENT '공개 여부',
	thumbnail_img INT NULL DEFAULT NULL COMMENT '썸네일 이미지 한 장 보여줄 것임(FK)',
	img_count INT NULL DEFAULT NULL COMMENT '몇 개 이미지를 올렸는가?',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시각', -- 새로 추가한 컬럼
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '기록 생성 일시',
    CONSTRAINT fk_parent_review_id
		FOREIGN KEY (parent_review_id) REFERENCES Review (review_id),
	CONSTRAINT fk_group_id
		FOREIGN KEY (group_id) REFERENCES Template_group (group_id),
	CONSTRAINT fk_review_recruit
        FOREIGN KEY (recruit_id) REFERENCES Recruit(recruit_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_review_org
        FOREIGN KEY (org_id) REFERENCES Organization(org_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_writer_id
		FOREIGN KEY (writer_id) REFERENCES User (user_id),
	CONSTRAINT fk_thumbnail_img_id
		FOREIGN KEY (thumbnail_img) REFERENCES Review_image (image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='후기 테이블';


DROP TABLE IF EXISTS Review_comment;
CREATE TABLE Review_comment (
    comment_id    INT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 ID',
    writer_id     INT NOT NULL COMMENT '작성자(User(user_id)) (FK)',
    review_id     INT NOT NULL COMMENT '어느 봉사자 후기에 대한 댓글인지 (FK)',
    content       VARCHAR(500) NOT NULL COMMENT '댓글 내용',
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시각', -- 새로 추가한 컬럼
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '댓글 작성 일시',
    CONSTRAINT fk_comment_writer
        FOREIGN KEY (writer_id) REFERENCES User(user_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_comment_review
        FOREIGN KEY (review_id) REFERENCES Review(review_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='후기에 대한 댓글 테이블';

-- 봉사자의 "좋아요 or 싫어요"한 봉사공고 목록
DROP TABLE IF EXISTS Volunteer_reaction;
CREATE TABLE Volunteer_reaction (
    reaction_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '봉사자의 반응 ID',
    volunteer_id  INT NOT NULL COMMENT '봉사자 ID (FK)',  -- 얘도 마찬가지로 봉사자만 할 수 있으니까 user_id로 안 했는데 .. 뭐가 좋을까?<- 일단 저는 user_id로 찬성이요!(유진)
    recruit_id    INT NOT NULL COMMENT '반응한 대상 봉사공고 (FK)',
    is_like      BOOLEAN NOT NULL COMMENT '리액션종류가 좋아요인가? TRUE(1) : 좋아요, FALSE(0) : 싫어요',
    is_deleted     BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 or 싫어요 반응 누른 일시',
    
    CONSTRAINT fk_reaction_volunteer
        FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_reaction_recruit
        FOREIGN KEY (recruit_id) REFERENCES Recruit(recruit_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='봉사자가 좋아요한 공고 정보';

-- 아래는 category에 값 추가하는 ddl
INSERT INTO Category (name, parent_id) VALUES
('활동보조', 1), ('이동지원', 1), ('청결지도', 1), ('급식지원', 1), ('식사·반찬지원', 1), ('기타', 1);
INSERT INTO Category (name, parent_id) VALUES
('주거개선', 2), ('마을공동체활동', 2), ('기타', 2);
INSERT INTO Category (name, parent_id) VALUES
('말벗·상담', 3), ('전문상담', 3), ('기타', 3);
INSERT INTO Category (name, parent_id) VALUES
('방과후 교육', 4), ('학습지도 교육', 4), ('특수교육', 4), ('평생교육', 4), ('전문교육', 4), ('진로체험지도', 4), ('기타', 4);
INSERT INTO Category (name, parent_id) VALUES
('간호·간병', 5), ('의료지원', 5), ('헌혈', 5), ('기타', 5);
INSERT INTO Category (name, parent_id) VALUES
('일손지원', 6), ('기타', 6);
INSERT INTO Category (name, parent_id) VALUES
('행사보조', 7), ('공연활동', 7), ('캠페인', 7), ('관광안내', 7), ('사진촬영', 7), ('기타', 7);
INSERT INTO Category (name, parent_id) VALUES
('환경정화', 8), ('환경감시', 8), ('기타', 8);
INSERT INTO Category (name, parent_id) VALUES
('사무지원', 9), ('업무지원', 9), ('기타', 9);
INSERT INTO Category (name, parent_id) VALUES
('어린이 안전', 10), ('청소년 안전', 10), ('취약계층 안전', 10), ('안전신고·활동', 10), ('기타', 10);
INSERT INTO Category (name, parent_id) VALUES
('인권개선', 11), ('공익보호', 11), ('기타', 11);
INSERT INTO Category (name, parent_id) VALUES
('긴급구조', 12), ('예방접종지원', 12), ('기타', 12);
INSERT INTO Category (name, parent_id) VALUES
('해외봉사', 13), ('국제행사단체지원', 13), ('통·번역', 13), ('기타', 13);
INSERT INTO Category (name, parent_id) VALUES
('멘토링', 14), ('학습지도 교육', 14), ('진로적성', 14), ('취업', 14), ('창업', 14), ('기타', 14);
INSERT INTO Category (name, parent_id) VALUES ('기타', 15);
USE volunteer_service;

INSERT INTO User (email, name, password, phone, total_volunteer_hours, profile_image, is_deleted, created_at)
VALUES
('user1@example.com', '김철수', 'hashed_password_1', '010-1234-5678', 10.50, 'profile1.jpg', FALSE, NOW()),
('user2@example.com', '이영희', 'hashed_password_2', '010-2345-6789', 20.00, 'profile2.jpg', FALSE, NOW()),
('user3@example.com', '박지훈', 'hashed_password_3', '010-3456-7890', 5.75, 'profile3.jpg', FALSE, NOW()),
('user4@example.com', '최민호', 'hashed_password_4', '010-4567-8901', 15.30, 'profile4.jpg', FALSE, NOW()),
('user5@example.com', '정유진', 'hashed_password_5', '010-5678-9012', 8.25, 'profile5.jpg', FALSE, NOW());

INSERT INTO Volunteer (user_id, preferred_time, interest_theme, duration_time, region, birth_date, gender, recommended_count, not_recommended_count)
VALUES
(1, '오전', '환경보호', '2시간', '서울', '1995-06-15', 'M', 3, 1),
(2, '오후', '노인복지', '3시간', '부산', '1990-11-25', 'F', 5, 0),
(3, '저녁', '아동교육', '1시간', '대전', '2000-02-10', 'M', 2, 2),
(4, '오전', '장애인 복지', '4시간', '광주', '1987-09-05', 'M', 1, 3),
(5, '오후', '동물 보호', '2시간', '대구', '1998-07-30', 'F', 4, 1);


INSERT INTO Organization (user_id, business_reg_number, org_name, representative_name, org_address, created_at)
VALUES
(1, '123-45-67890', '희망복지센터', '이상호', '서울시 강남구 테헤란로 123', NOW()),
(2, '234-56-78901', '행복나눔재단', '김지수', '부산시 해운대구 센텀로 45', NOW()),
(3, '345-67-89012', '사랑의집', '박영철', '대구시 동구 동대구로 10', NOW()),
(4, '456-78-90123', '푸른환경센터', '정은지', '광주시 북구 문화로 22', NOW()),
(5, '567-89-01234', '어린이사랑재단', '최민기', '대전시 서구 둔산대로 77', NOW());

INSERT INTO Template_group (org_id, group_name, is_deleted)
VALUES
(1, '환경 봉사팀', FALSE),
(2, '노인 돌봄 봉사단', FALSE),
(3, '아동 교육 봉사', FALSE),
(4, '장애인 지원팀', FALSE),
(5, '동물 보호 단체', FALSE);

INSERT INTO Template (group_id, org_id, category_id, title, activity_location, status, image_id, contact_name, contact_phone, description, is_deleted, created_at)
VALUES
(1, 1, 1, '강남구 환경 정화 봉사', '서울시 강남구 코엑스 앞', 'ALL', 'image1.jpg', '김철수', '010-1234-5678', '환경 보호 활동', FALSE, NOW()),
(2, 2, 2, '부산 노인 돌봄 봉사', '부산시 해운대구', 'YOUTH', 'image2.jpg', '이영희', '010-2345-6789', '노인 돌봄 활동', FALSE, NOW()),
(3, 3, 3, '아동 교육 봉사', '대구시 동구 어린이 도서관', 'ADULT', 'image3.jpg', '박지훈', '010-3456-7890', '아이들과 함께하는 교육', FALSE, NOW()),
(4, 4, 4, '장애인 지원 활동', '광주시 북구', 'ALL', 'image4.jpg', '최민호', '010-4567-8901', '장애인 지원 프로그램', FALSE, NOW()),
(5, 5, 5, '유기 동물 보호소 봉사', '대전시 서구', 'ALL', 'image5.jpg', '정유진', '010-5678-9012', '유기 동물 보호 활동', FALSE, NOW());

INSERT INTO Recruit (template_id, deadline, activity_date, activity_start, activity_end, max_volunteer, participate_vol_count, status, is_deleted, updated_at, created_at)
VALUES
(1, '2025-12-31', '2025-06-15', 10.00, 12.00, 20, 5, 'RECRUITING', FALSE, NOW(), NOW()),
(2, '2025-11-30', '2025-07-20', 13.00, 16.00, 15, 7, 'RECRUITING', FALSE, NOW(), NOW()),
(3, '2025-10-15', '2025-08-10', 14.00, 17.00, 10, 8, 'RECRUITING', FALSE, NOW(), NOW()),
(4, '2025-09-25', '2025-09-05', 09.00, 11.00, 25, 12, 'RECRUITING', FALSE, NOW(), NOW()),
(5, '2025-08-10', '2025-10-20', 15.00, 18.00, 30, 15, 'RECRUITING', FALSE, NOW(), NOW());

INSERT INTO Review (recruit_id, org_id, writer_id, title, content, is_deleted, is_public, img_count, updated_at, created_at)
VALUES
(1, 1, 1, '환경 정화 봉사 후기', '이번 봉사는 정말 뜻깊은 경험이었습니다!', FALSE, TRUE, 2, NOW(), NOW()),
(2, 2, 2, '노인 돌봄 봉사 후기', '어르신들과 함께 시간을 보내며 많은 것을 배웠습니다.', FALSE, TRUE, 3, NOW(), NOW()),
(3, 3, 3, '아동 교육 봉사 후기', '아이들에게 유익한 시간을 제공할 수 있어서 보람찼습니다.', FALSE, TRUE, 1, NOW(), NOW()),
(4, 4, 4, '장애인 지원 활동 후기', '도움이 필요한 분들에게 직접적인 지원을 할 수 있어 좋았습니다.', FALSE, TRUE, 4, NOW(), NOW()),
(5, 5, 5, '유기 동물 보호소 봉사 후기', '동물들이 편안한 환경에서 지낼 수 있도록 돕는 것이 뜻깊었습니다.', FALSE, TRUE, 2, NOW(), NOW());

INSERT INTO Application (volunteer_id, recruit_id, status, evaluation_done, is_deleted, created_at, updated_at)
VALUES
(1, 1, 'PENDING', FALSE, FALSE, NOW(), NOW()),
(2, 2, 'APPROVED', TRUE, FALSE, NOW(), NOW()),
(3, 3, 'REJECTED', FALSE, FALSE, NOW(), NOW()),
(4, 4, 'COMPLETED', TRUE, FALSE, NOW(), NOW()),
(5, 5, 'AUTO_CANCEL', FALSE, FALSE, NOW(), NOW());

INSERT INTO Review_image (review_id, image_url, is_deleted, created_at, next_image_id)
VALUES
(1, 'https://example.com/review1_image1.jpg', FALSE, NOW(), NULL),
(1, 'https://example.com/review1_image2.jpg', FALSE, NOW(), NULL),
(2, 'https://example.com/review2_image1.jpg', FALSE, NOW(), NULL),
(3, 'https://example.com/review3_image1.jpg', FALSE, NOW(), NULL),
(4, 'https://example.com/review4_image1.jpg', FALSE, NOW(), NULL);

INSERT INTO Review_comment (writer_id, review_id, content, is_deleted, updated_at, created_at)
VALUES
(1, 1, '정말 좋은 후기네요! 저도 참여하고 싶어요.', FALSE, NOW(), NOW()),
(2, 1, '환경 보호 활동이 중요하죠!', FALSE, NOW(), NOW()),
(3, 2, '노인분들과 함께하는 시간이 뜻깊어 보이네요.', FALSE, NOW(), NOW()),
(4, 3, '아동 교육 봉사에 관심이 많아요!', FALSE, NOW(), NOW()),
(5, 4, '장애인 지원 활동에 더 많은 관심이 필요합니다.', FALSE, NOW(), NOW());

INSERT INTO Volunteer_reaction (volunteer_id, recruit_id, is_like, is_deleted, created_at)
VALUES
(1, 1, TRUE, FALSE, NOW()),
(2, 2, FALSE, FALSE, NOW()),
(3, 3, TRUE, FALSE, NOW()),
(4, 4, TRUE, FALSE, NOW()),
(5, 5, FALSE, FALSE, NOW());

select * from reviews;

-- SELECT * FROM Category;