import React from 'react';
import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { ReviewWriteHeader } from './components/ReviewWriteHeader';
import { ReviewWriteImages } from './components/ReviewWriteImages';
import { ReviewWriteForm } from './components/ReviewWriteForm';
import { useReviewStore } from '@/stores/reviewStore';
import { reviewApi } from '@/api/reviewApi';
import { useToast } from '@/hooks/use-toast';

export default function ReviewWrite() {
  const navigate = useNavigate();
  const location = useLocation();
  const { orgId, resetReviewInfo } = useReviewStore();
  const { updateReview } = useReviewStore();
  
  // location.state에서 수정 관련 정보 가져오기
  const isEdit = Boolean(location.state?.isEdit);
  const editReviewId = location.state?.reviewId;
  
  const [images, setImages] = useState<string[]>([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const { toast } = useToast();

  useEffect(() => {
    if (isEdit && editReviewId) {
      const loadReview = async () => {
        try {
          const review = await reviewApi.getReviewDetail(Number(editReviewId));
          setTitle(review.title);
          setContent(review.content);
          setImages(review.images || []);
          setIsPublic(review.isPublic);
        } catch (error) {
          toast({
            variant: "destructive",
            title: "오류",
            description: "리뷰 정보를 불러오는데 실패했습니다."
          });
          navigate(-1);
        }
      };
      loadReview();
    }
  }, [editReviewId, isEdit, navigate, toast]);

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
    if (!title.trim() || !content.trim()) {
      toast({
        variant: "destructive",
        title: "오류",
        description: "제목과 내용을 입력해주세요."
      });
      return;
    }

    try {
      const reviewData = {
        title,
        content,
        isPublic,
        images,
        imageCount: images.length
      };

      if (isEdit && editReviewId) {
        const response = await updateReview(Number(editReviewId), reviewData);
        
        // 새로운 이미지가 있다면 업로드
        if (response.presignedUrl.length > 0) {
          // 이미지 업로드 로직 구현
          // response.presignedUrl을 사용하여 이미지 업로드
        }

        toast({
          title: "성공",
          description: "리뷰가 수정되었습니다."
        });
      } else {
        // 새 리뷰 생성 로직
      }
      
      navigate(-1);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "오류",
        description: isEdit ? "리뷰 수정에 실패했습니다." : "리뷰 등록에 실패했습니다."
      });
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
          orgId: orgId!,
          orgName: "서울 환경 봉사단"
        }}
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
            {isEdit ? '수정' : '등록'}
          </Button>
        </div>
      </div>
    </div>
  );
}
