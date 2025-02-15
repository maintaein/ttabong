import { useCallback } from 'react';
import { ImagePlus, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useToast } from '@/hooks/use-toast';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import { Card } from '@/components/ui/card';

interface ReviewWriteImagesProps {
  images: string[];
  onImageUpload: (files: File[]) => void;
  onImageRemove: (index: number) => void;
}

const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png'];
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

export function ReviewWriteImages({ images, onImageUpload, onImageRemove }: ReviewWriteImagesProps) {
  const { toast } = useToast();
  const MAX_IMAGES = 10;

  const validateImage = useCallback((file: File): Promise<boolean> => {
    return new Promise((resolve) => {
      if (!ALLOWED_TYPES.includes(file.type)) {
        setTimeout(() => {
          toast({
            variant: "destructive",
            title: "지원되지 않는 파일 형식",
            description: "PNG, JPG, JPEG 형식만 지원됩니다."
          });
          resolve(false);
        }, 0);
        return;
      }

      if (file.size > MAX_FILE_SIZE) {
        setTimeout(() => {
          toast({
            variant: "destructive",
            title: "파일 크기 초과",
            description: "이미지 크기는 10MB 이하여야 합니다."
          });
          resolve(false);
        }, 0);
        return;
      }

      resolve(true);
    });
  }, [toast]);

  const handleImageUpload = useCallback(async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    
    if (!files.length) return;
    
    if (images.length + files.length > MAX_IMAGES) {
      toast({
        variant: "destructive",
        title: "이미지 개수 초과",
        description: `이미지는 최대 ${MAX_IMAGES}개까지 첨부 가능합니다.`
      });
      e.target.value = '';
      return;
    }

    let hasInvalidFile = false;
    
    for (const file of files) {
      const isValid = await validateImage(file);
      if (!isValid) {
        hasInvalidFile = true;
        break;
      }
    }

    if (hasInvalidFile) {
      e.target.value = '';
      return;
    }

    onImageUpload(files);
    e.target.value = '';
  }, [images.length, onImageUpload, toast, validateImage, MAX_IMAGES]);

  return (
    <Card className="p-4 space-y-4">
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium">이미지 첨부 ({images.length}/{MAX_IMAGES})</span>
        <Button 
          variant="outline" 
          size="sm" 
          className="gap-2"
          onClick={() => {
            if (images.length >= MAX_IMAGES) {
              toast({
                variant: "destructive",
                title: "이미지 개수 초과",
                description: `이미지는 최대 ${MAX_IMAGES}개까지 첨부 가능합니다.`
              });
              return;
            }
          }}
        >
          <ImagePlus className="h-4 w-4" />
          <label className={`cursor-pointer ${images.length >= MAX_IMAGES ? 'cursor-not-allowed' : ''}`}>
            이미지 추가
            <input
              type="file"
              className="hidden"
              accept="image/png,image/jpeg,image/jpg"
              multiple
              onChange={handleImageUpload}
              disabled={images.length >= MAX_IMAGES}
              onClick={(e) => {
                if (images.length >= MAX_IMAGES) {
                  e.preventDefault();
                  return;
                }
                (e.target as HTMLInputElement).value = '';
              }}
            />
          </label>
        </Button>
      </div>
      
      <p className="text-xs text-muted-foreground">
        • PNG, JPG, JPEG 형식만 지원 (최대 10MB)
      </p>
      
      {images.length > 0 && (
        <div className="relative">
          <Carousel 
            className="w-full" 
            opts={{ 
              startIndex: images.length - 1,
              align: "end"
            }}
          >
            <CarouselContent className="-ml-4">
              {images.map((image, index) => (
                <CarouselItem key={index} className="pl-4 basis-1/3">
                  <div className="relative aspect-square">
                    <img
                      src={image}
                      alt={`첨부 이미지 ${index + 1}`}
                      className="w-full h-full object-cover rounded-md"
                      onError={(e) => {
                        e.currentTarget.src = '/fallback-image.png';
                        toast({
                          variant: "destructive",
                          title: "이미지 로드 실패",
                          description: "이미지를 표시할 수 없습니다."
                        });
                      }}
                    />
                    <Button
                      variant="destructive"
                      size="icon"
                      className="absolute top-2 right-2 z-10"
                      onClick={() => onImageRemove(index)}
                    >
                      <X className="h-4 w-4" />
                    </Button>
                    <div className="absolute bottom-2 right-2 bg-black/50 text-white px-2 py-1 rounded-md text-xs">
                      {index + 1}/{images.length}
                    </div>
                  </div>
                </CarouselItem>
              ))}
            </CarouselContent>
            {images.length > 3 && (
              <>
                <CarouselPrevious className="absolute left-2 top-1/2 -translate-y-1/2" />
                <CarouselNext className="absolute right-2 top-1/2 -translate-y-1/2" />
              </>
            )}
          </Carousel>
        </div>
      )}
    </Card>
  );
} 