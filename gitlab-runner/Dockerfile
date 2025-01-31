FROM alpine:latest

# 기본 패키지 설치
RUN apk add --no-cache bash curl git docker openrc

# GitLab Runner 설치
RUN curl -L --output /usr/local/bin/gitlab-runner https://gitlab-runner-downloads.s3.amazonaws.com/latest/binaries/gitlab-runner-linux-amd64 \
    && chmod +x /usr/local/bin/gitlab-runner \
    && gitlab-runner --version

# 환경 변수 설정
ENV GITLAB_URL="https://lab.ssafy.com"
ENV EXECUTOR="docker"
ENV DEFAULT_IMAGE="alpine:latest"
# 환경 변수 설정 (선택 사항)
ENV CONFIG_FILE="/etc/gitlab-runner/config.toml"

# GitLab Runner 설정 파일 복사
COPY config.toml /etc/gitlab-runner/config.toml
# 실행 스크립트 복사
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# GitLab Runner 실행
ENTRYPOINT ["/entrypoint.sh"]