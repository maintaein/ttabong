#!/bin/sh
echo "📌 GitLab Runner 등록 중..."
gitlab-runner register --non-interactive \
    --url "$GITLAB_URL" \
    --registration-token "$REGISTRATION_TOKEN" \
    --executor "$EXECUTOR" \
    --docker-image "$DEFAULT_IMAGE" \
    --description "alpine-runner" \
    --tag-list "docker,ci" \
    --run-untagged=true \
    --locked=false

echo "✅ GitLab Runner 등록 완료!"

# GitLab Runner 실행
echo "🚀 GitLab Runner 실행 중..."
exec gitlab-runner run
