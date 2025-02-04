import { Badge } from '@/components/ui/badge';

interface ReviewGalleryProps {
  images: string[];
  isOrganization: boolean;
}

export function ReviewGallery({ images, isOrganization }: ReviewGalleryProps) {
  return (
    <div className="aspect-square relative">
      <img
        src={images[0]}
        alt="Review"
        className="w-full h-full object-cover"
      />
      <div className="absolute bottom-3 right-3">
        <Badge variant={isOrganization ? "default" : "secondary"}>
          {isOrganization ? "기관 후기" : "개인 후기"}
        </Badge>
      </div>
    </div>
  );
} 