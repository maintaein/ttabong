import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { ReviewWriteHeader } from './components/ReviewWriteHeader';
import { ReviewWriteImages } from './components/ReviewWriteImages';
import { ReviewWriteForm } from './components/ReviewWriteForm';
import { useReviewWriteStore } from '@/stores/reviewWriteStore';

export default function ReviewWrite() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { resetReviewInfo } = useReviewWriteStore();
  const [images, setImages] = useState<string[]>([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [isPublic, setIsPublic] = useState(true);

  useEffect(() => {
    const recruitId = searchParams.get('recruitId');
    const orgId = searchParams.get('orgId');
    
    if (!recruitId || !orgId) {
      navigate('/choose-recruit');
      return;
    }
  }, [searchParams, navigate]);

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
    const recruitId = searchParams.get('recruitId');
    const orgId = searchParams.get('orgId');

    if (!recruitId || !orgId) {
      navigate('/choose-recruit');
      return;
    }

    const reviewData = {
      recruitId: Number(recruitId),
      orgId: Number(orgId),
      writerId: 10,
      title,
      content,
      isPublic,
      thumbnailImg: images[0],
      imgCount: images.length
    };

    try {
      // TODO: API 연동
      console.log(reviewData);
    } catch (error) {
      if (error instanceof Error && error.message.includes('404')) {
        navigate('/choose-recruit');
        return;
      }
      // 다른 에러 처리
    }
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