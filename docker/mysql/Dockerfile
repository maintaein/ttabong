# MySQL 8.0 공식 이미지 사용
FROM mysql:8.0

# 환경 변수 설정 (Kubernetes Secret 또는 ConfigMap과 호환)
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=ddabong_db
ENV MYSQL_USER=ddabong
ENV MYSQL_PASSWORD=707

# 데이터 지속성 유지
VOLUME ["/var/lib/mysql"]

# Healthcheck 추가
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD mysqladmin ping -h localhost || exit 1
