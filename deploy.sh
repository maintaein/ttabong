#!/bin/sh

set -e  # 오류 발생 시 즉시 종료

echo "🚀 배포 스크립트 실행 중..."

mkdir -p ~/.ssh  # 디렉토리 생성
echo "$SSH_PRIVATE_KEY" > ~/.ssh/deploy_key.pem
chmod 600 ~/.ssh/deploy_key.pem

# SSH 접속 및 배포 실행
ssh -v -o StrictHostKeyChecking=no -i ~/.ssh/deploy_key.pem -o SendEnv=DOCKER_HUB_USERNAME -o SendEnv=DOCKER_HUB_TOKEN -o SendEnv=DEPLOY_SERVER $DEPLOY_SERVER << 'ENDSSH'
set -e  # ssh 세션 안에서도 오류 발생 시 즉시 종료

echo '✅ [Step 1] AWS EC2 접속 완료'

# ✅ 2. Docker & Git 설치 확인
echo '🔍 Checking for Docker & Git installation...'

if ! command -v docker &> /dev/null; then
  echo '🚀 Docker not found. Installing Docker...'
  sudo apt update
  sudo apt install -y docker.io
  sudo systemctl start docker
  sudo systemctl enable docker
  echo '✅ Docker installed successfully!'
else
  echo '✅ Docker is already installed.'
fi

if ! command -v git &> /dev/null; then
  echo '🚀 Git not found. Installing Git...'
  sudo apt install -y git
  echo '✅ Git installed successfully!'
else
  echo '✅ Git is already installed.'
fi

# ✅ 3. Docker Hub 로그인
echo '🔑 Logging into Docker Hub...'
# 여기서 --username 옵션 추가
echo "$DOCKER_HUB_TOKEN" | docker login -u "$DOCKER_HUB_USERNAME" --password-stdin

# ✅ 4. GitHub 최신 코드 Pull
echo '🔄 Pulling latest code from GitHub...'
cd /home/ubuntu/app || { echo '❌ App directory not found, cloning...'; git clone --single-branch --branch dev https://$GITLAB_USER:$GITLAB_TOKEN@lab.ssafy.com/recreate.kang/ttabong.git /home/ubuntu/app; 
    cd /home/ubuntu/app; }
git reset --hard HEAD
git pull origin dev

# ✅ 6. Docker Compose 설치 확인 및 필요 시 설치
echo '🔍 Checking for Docker Compose installation...'

if ! command -v docker-compose &> /dev/null; then
  echo '🚀 Docker Compose not found. Installing Docker Compose...'
  sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  echo '✅ Docker Compose installed successfully!'
else
  echo '✅ Docker Compose is already installed.'
fi

# ✅ 7. 최신 Docker 이미지 Pull
echo '📦 Pulling latest Docker images from Docker Hub...'
# `ttabong` 폴더로 이동하여 `docker-compose` 명령어 실행
cd /home/ubuntu/ttabong || { echo '❌ ttabong directory not found!'; exit 1; }

docker-compose -f docker-compose.ec2.yml pull || { echo '❌ Docker image pull failed!'; exit 1; }

# ✅ 8. 기존 컨테이너 종료 후 새로운 컨테이너 실행
echo '🚀 Restarting containers with Docker Compose...'
docker-compose -f docker-compose.ec2.yml down || { echo '❌ Docker Compose down failed!'; exit 1; }
docker-compose -f docker-compose.ec2.yml up -d || { echo '❌ Docker Compose up failed!'; exit 1; }

# ✅ 9. 서비스 상태 체크
echo '🔍 Checking running containers...'
docker ps -a || { echo '❌ Docker ps failed!'; exit 1; }

# ✅ 10. 로그 확인
echo '📜 Showing last 50 logs...'
docker-compose logs --tail=50 || { echo '❌ Docker logs failed!'; exit 1; }

echo '✅ Deployment completed successfully!'

ENDSSH

echo "✅ 배포 완료!"
