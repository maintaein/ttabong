import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { ReviewWriteHeader } from './components/ReviewWriteHeader';
import { ReviewWriteImages } from './components/ReviewWriteImages';
import { ReviewWriteForm } from './components/ReviewWriteForm';
import { useReviewWriteStore } from '@/stores/reviewWriteStore';

export default function ReviewWrite() {
  const navigate = useNavigate();
  const { recruitId, orgId, resetReviewInfo } = useReviewWriteStore();
  const [images, setImages] = useState<string[]>([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [isPublic, setIsPublic] = useState(true);

  useEffect(() => {
    const currentRecruitId = Number(sessionStorage.getItem('reviewWrite.recruitId'));
    const currentOrgId = Number(sessionStorage.getItem('reviewWrite.orgId'));
    
    if (!currentRecruitId || !currentOrgId) {
      navigate('/choose-recruit');
      return;
    }
  }, [navigate]);

  useEffect(() => {
    return () => resetReviewInfo();
  }, [resetReviewInfo]);

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    files.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImages(prev => [...prev, reader.result as string]);
      };
      reader.readAsDataURL(file);
    });
  };

  const handleImageRemove = (index: number) => {
    setImages(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async () => {
    if (!recruitId || !orgId) {
      console.error('Required information is missing');
      return;
    }

    const reviewData = {
      recruitId,
      orgId,
      writerId: 10, // TODO: 실제 사용자 ID로 대체
      title,
      content,
      isPublic,
      thumbnailImg: images[0], // TODO: 실제 이미지 업로드 로직 추가
      imgCount: images.length
    };

    // TODO: API 연동
    console.log(reviewData);
  };

  return (
    <div className="pb-[calc(56px+56px)]">
      <ReviewWriteHeader
        writer={{
          writerId: 1,
          writerName: "김봉사",
          writerProfileImage: "https://picsum.photos/100/100"
        }}
        organization={{
          orgId: 1,
          orgName: "서울 환경 봉사단"
        }}
        isPublic={isPublic}
      />
      
      <ReviewWriteImages
        images={images}
        onImageUpload={handleImageUpload}
        onImageRemove={handleImageRemove}
      />

      <ReviewWriteForm
        title={title}
        content={content}
        isPublic={isPublic}
        onTitleChange={(e) => setTitle(e.target.value)}
        onContentChange={(e) => setContent(e.target.value)}
        onPublicChange={setIsPublic}
      />

      <div className="fixed bottom-[56px] left-0 right-0 p-4 bg-background border-t max-w-[600px] mx-auto">
        <div className="flex gap-2">
          <Button variant="outline" className="flex-1" onClick={() => navigate(-1)}>
            취소
          </Button>
          <Button className="flex-1" onClick={handleSubmit}>
            작성
          </Button>
        </div>
      </div>
    </div>
  );
}
