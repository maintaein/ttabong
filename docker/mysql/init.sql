-- 봉사활동 매칭 플랫폼 데이터베이스 설계
-- 에러 방지를 위해 모든 DB & table 생성 앞에 if exists를 붙임.
-- 기존 데이터베이스 삭제 및 생성
DROP DATABASE IF EXISTS volunteer_service;
CREATE DATABASE IF NOT EXISTS volunteer_service;
USE volunteer_service;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='봉사자 추가 정보 테이블';

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

-- 🔹 1. Template 테이블 먼저 생성
CREATE TABLE IF NOT EXISTS Template (
    template_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '개별 템플릿 id',
    group_id INT NOT NULL COMMENT '그룹id',
    org_id INT COMMENT '관련 기관 id',
    category_id INT COMMENT '봉사 분류',
    title VARCHAR(255) COMMENT '공고 제목(봉사 제목)',
    activity_location VARCHAR(255) NOT NULL COMMENT '봉사 활동 장소(재택 또는 도로명주소)',
    status ENUM('ALL', 'YOUTH', 'ADULT') NOT NULL DEFAULT 'ALL' COMMENT '봉사자 유형',
    -- image_id INT COMMENT '이미지 경로',
    contact_name VARCHAR(50) COMMENT '담당자명',
    contact_phone VARCHAR(20) COMMENT '담당자 연락처',
    description VARCHAR(500) COMMENT '봉사활동 상세 내용',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '템플릿 생성 일시',
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
    activity_start DECIMAL(7,2) DEFAULT 0.00 NOT NULL COMMENT '봉사 시작시간',
    activity_end DECIMAL(7,2) DEFAULT 0.00 NOT NULL COMMENT '봉사 종료시간',
    -- activity_time VARCHAR(50) NOT NULL COMMENT '활동 시간(=봉사해야하는 시간)', -- 현재 varchar타입으로 받는데, 이를 어떻게 매칭해 줄 것인가?
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
    img_count INT NULL DEFAULT NULL COMMENT '몇 개 이미지를 올렸는가?',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시각',
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
        FOREIGN KEY (writer_id) REFERENCES User (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='후기 테이블';


-- 이미지 테이블 (리뷰, 템플릿에 사용)
DROP TABLE IF EXISTS Review_image;
CREATE TABLE IF NOT EXISTS Review_image (
    image_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '이미지 id',
    template_id INT NULL COMMENT '템플릿 이미지와 연결됨 (NULL 가능)',
    review_id INT NULL COMMENT '어느 후기에 대한 이미지인가? (NULL 가능)',
    image_url VARCHAR(500) NOT NULL COMMENT '이미지 url',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    is_thumbnail BOOLEAN DEFAULT FALSE COMMENT '대표 이미지 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '이미지 생성시간',
    next_image_id INT NULL DEFAULT NULL COMMENT '다음에 올 이미지 id',
    CONSTRAINT fk_next_image_id
    FOREIGN KEY (next_image_id) REFERENCES Review_image(image_id),
    CONSTRAINT fk_review_image_template
    FOREIGN KEY (template_id) REFERENCES Template(template_id)
    ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_review_image_review
    FOREIGN KEY (review_id) REFERENCES Review(review_id)
    ON DELETE SET NULL ON UPDATE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;


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
INSERT INTO Category (name, parent_id) VALUES ('활동보조', 1), ('이동지원', 1), ('청결지도', 1), ('급식지원', 1), ('식사·반찬지원', 1), ('기타', 1);
INSERT INTO Category (name, parent_id) VALUES ('주거개선', 2), ('마을공동체활동', 2), ('기타', 2);
INSERT INTO Category (name, parent_id) VALUES ('말벗·상담', 3), ('전문상담', 3), ('기타', 3);
INSERT INTO Category (name, parent_id) VALUES ('방과후 교육', 4), ('학습지도 교육', 4), ('특수교육', 4), ('평생교육', 4), ('전문교육', 4), ('진로체험지도', 4), ('기타', 4);
INSERT INTO Category (name, parent_id) VALUES ('간호·간병', 5), ('의료지원', 5), ('헌혈', 5), ('기타', 5);
INSERT INTO Category (name, parent_id) VALUES ('일손지원', 6), ('기타', 6);
INSERT INTO Category (name, parent_id) VALUES ('행사보조', 7), ('공연활동', 7), ('캠페인', 7), ('관광안내', 7), ('사진촬영', 7), ('기타', 7);
INSERT INTO Category (name, parent_id) VALUES ('환경정화', 8), ('환경감시', 8), ('기타', 8);
INSERT INTO Category (name, parent_id) VALUES ('사무지원', 9), ('업무지원', 9), ('기타', 9);
INSERT INTO Category (name, parent_id) VALUES ('어린이 안전', 10), ('청소년 안전', 10), ('취약계층 안전', 10), ('안전신고·활동', 10), ('기타', 10);
INSERT INTO Category (name, parent_id) VALUES ('인권개선', 11), ('공익보호', 11), ('기타', 11);
INSERT INTO Category (name, parent_id) VALUES ('긴급구조', 12), ('예방접종지원', 12), ('기타', 12);
INSERT INTO Category (name, parent_id) VALUES ('해외봉사', 13), ('국제행사단체지원', 13), ('통·번역', 13), ('기타', 13);
INSERT INTO Category (name, parent_id) VALUES ('멘토링', 14), ('학습지도 교육', 14), ('진로적성', 14), ('취업', 14), ('창업', 14), ('기타', 14);
INSERT INTO Category (name, parent_id) VALUES ('기타', 15);


-- SELECT * FROM Category;

USE volunteer_service;

INSERT INTO User (email, name, password, phone, total_volunteer_hours, profile_image, is_deleted, created_at) VALUES
('user1@example.com', '홍길동', 'hashedpassword1', '010-1234-5678', 12.50, 'profile1.jpg', FALSE, NOW()),
('user2@example.com', '김영희', 'hashedpassword2', '010-2345-6789', 25.75, 'profile2.jpg', FALSE, NOW()),
('user3@example.com', '이철수', 'hashedpassword3', '010-3456-7890', 30.00, 'profile3.jpg', FALSE, NOW()),
('user4@example.com', '박민수', 'hashedpassword4', '010-4567-8901', 45.20, 'profile4.jpg', FALSE, NOW()),
('user5@example.com', '정다혜', 'hashedpassword5', '010-5678-9012', 5.50, 'profile5.jpg', FALSE, NOW());

INSERT INTO Volunteer (user_id, preferred_time, interest_theme, duration_time, region, birth_date, gender, recommended_count, not_recommended_count) VALUES
(1, '오전', '환경보호', '2시간', '서울', '1995-06-15', 'M', 10, 2),
(2, '오후', '교육', '3시간', '부산', '2000-01-20', 'F', 8, 1),
(3, '저녁', '동물보호', '1시간', '대구', '1998-11-03', 'M', 5, 0),
(4, '주말', '노인복지', '4시간', '인천', '1993-08-22', 'M', 12, 3),
(5, '평일', '아동복지', '2시간', '대전', '1997-12-10', 'F', 15, 5);

INSERT INTO Organization (user_id, business_reg_number, org_name, representative_name, org_address, created_at) VALUES
(1, '123-45-67890', '희망나눔센터', '이명수', '서울특별시 종로구 세종대로 1', NOW()),
(2, '234-56-78901', '한마음봉사단', '김지혜', '부산광역시 해운대구 해운대로 23', NOW()),
(3, '345-67-89012', '사랑의손길', '박철민', '대구광역시 수성구 동대구로 45', NOW()),
(4, '456-78-90123', '행복한세상', '최민영', '인천광역시 남동구 남동대로 67', NOW()),
(5, '567-89-01234', '희망의빛', '정은영', '대전광역시 유성구 유성대로 89', NOW());

INSERT INTO Template_group (org_id, group_name, is_deleted) VALUES
(1, '환경보호 봉사', FALSE),
(2, '교육 봉사', FALSE),
(3, '동물보호 봉사', FALSE),
(4, '노인복지 봉사', FALSE),
(5, '아동복지 봉사', FALSE);

-- 샘플 데이터 삽입을 위한 그룹, 기관, 카테고리 테이블에 존재하는 ID가 필요합니다.
-- (이미 존재하는 데이터를 사용하거나, 직접 INSERT 해야 함)

-- Template 테이블 데이터 삽입
INSERT INTO Template (group_id, org_id, category_id, title, activity_location, status, contact_name, contact_phone, description, is_deleted, created_at) VALUES
(1, 1, 1, '한강 플로깅', '서울특별시 한강공원', 'ALL', '박지훈', '010-1234-5678', '한강에서 쓰레기를 줍는 봉사 활동입니다.', FALSE, NOW()),
(2, 2, 2, '무료 영어 교육', '부산광역시 초등학교', 'YOUTH', '김선영', '010-2345-6789', '초등학생을 대상으로 무료 영어 수업을 진행합니다.', FALSE, NOW()),
(3, 3, 3, '유기동물 보호소 돕기', '대구광역시 동물보호센터', 'ADULT', '이철호', '010-3456-7890', '유기동물 보호소에서 봉사 활동을 진행합니다.', FALSE, NOW()),
(4, 4, 4, '노인복지센터 봉사', '인천광역시 노인복지센터', 'ALL', '최미경', '010-4567-8901', '노인복지센터에서 말벗 및 환경미화 봉사를 진행합니다.', FALSE, NOW()),
(5, 5, 5, '아동복지센터 후원', '대전광역시 아동복지센터', 'YOUTH', '정예진', '010-5678-9012', '아동복지센터에서 아이들과 함께하는 프로그램을 운영합니다.', FALSE, NOW());

INSERT INTO Recruit (template_id, deadline, activity_date, activity_start, activity_end, max_volunteer, participate_vol_count, status, is_deleted, updated_at, created_at) VALUES
(1, NOW(), '2025-03-01', 9.00, 12.00, 10, 5, 'RECRUITING', FALSE, NOW(), NOW()),
(2, NOW(), '2025-04-05', 14.00, 17.00, 20, 15, 'RECRUITING', FALSE, NOW(), NOW()),
(3, NOW(), '2025-05-10', 10.00, 13.00, 15, 10, 'RECRUITMENT_CLOSED', FALSE, NOW(), NOW()),
(4, NOW(), '2025-06-15', 8.00, 11.00, 12, 12, 'ACTIVITY_COMPLETED', FALSE, NOW(), NOW()),
(5, NOW(), '2025-07-20', 13.00, 16.00, 25, 18, 'RECRUITING', FALSE, NOW(), NOW());

INSERT INTO Application (volunteer_id, recruit_id, status, evaluation_done, is_deleted, created_at, updated_at) VALUES
(1, 1, 'PENDING', FALSE, FALSE, NOW(), NOW()),
(2, 2, 'APPROVED', TRUE, FALSE, NOW(), NOW()),
(3, 3, 'REJECTED', FALSE, FALSE, NOW(), NOW()),
(4, 4, 'COMPLETED', TRUE, FALSE, NOW(), NOW()),
(5, 5, 'PENDING', FALSE, FALSE, NOW(), NOW());

INSERT INTO Review (parent_review_id, group_id, recruit_id, org_id, writer_id, title, content, is_deleted, is_public, img_count, updated_at, created_at) VALUES
                             (NULL, 1, 1, 1, 1, '봉사 후기 - 한강 플로깅', '정말 보람찬 시간이었습니다!', FALSE, TRUE, 2, NOW(), NOW()),
                             (NULL, 2, 2, 2, 2, '봉사 후기 - 무료 영어 교육', '아이들에게 영어를 가르쳐주는 것이 너무 즐거웠어요.', FALSE, TRUE, 3, NOW(), NOW());

INSERT INTO Review_image (template_id, review_id, image_url, is_deleted, is_thumbnail, created_at) VALUES
(1, 1, 'https://example.com/review1_image1.jpg', FALSE, TRUE, NOW()),
(1, 1, 'https://example.com/review1_image2.jpg', FALSE, FALSE, NOW()),
(2, 2, 'https://example.com/review2_image1.jpg', FALSE, TRUE, NOW()),
(2, 2, 'https://example.com/review2_image2.jpg', FALSE, FALSE, NOW()),
(3, NULL, 'https://example.com/template_image1.jpg', FALSE, TRUE, NOW()),
(4, NULL, 'https://example.com/template_image2.jpg', FALSE, FALSE, NOW());

INSERT INTO Review_comment (writer_id, review_id, content, is_deleted, updated_at, created_at) VALUES
(2, 1, '정말 멋진 후기네요! 저도 참여하고 싶어요!', FALSE, NOW(), NOW()),
(3, 1, '한강 플로깅 너무 재밌었어요!', FALSE, NOW(), NOW()),
(1, 2, '무료 영어 교육 봉사 너무 의미 있었을 것 같아요!', FALSE, NOW(), NOW()),
(4, 2, '아이들과 함께하는 시간이 정말 보람찼겠네요!', FALSE, NOW(), NOW()),
(5, 1, '환경 보호 봉사활동에 많은 분들이 참여하면 좋겠어요!', FALSE, NOW(), NOW());

INSERT INTO Volunteer_reaction (volunteer_id, recruit_id, is_like, is_deleted, created_at) VALUES
(1, 1, TRUE, FALSE, NOW()),
(2, 2, TRUE, FALSE, NOW()),
(3, 3, FALSE, FALSE, NOW()),
(4, 4, TRUE, FALSE, NOW()),
(5, 5, FALSE, FALSE, NOW());

