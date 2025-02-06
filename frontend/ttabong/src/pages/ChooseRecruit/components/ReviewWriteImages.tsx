import { Button } from '@/components/ui/button';
import { ImagePlus, X } from 'lucide-react';

interface ReviewWriteImagesProps {
  images: string[];
  onImageUpload: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onImageRemove: (index: number) => void;
}

export function ReviewWriteImages({ images, onImageUpload, onImageRemove }: ReviewWriteImagesProps) {
  return (
    <div className="p-4">
      <div className="grid grid-cols-3 gap-2">
        {images.map((image, index) => (
          <div key={index} className="relative aspect-square">
            <img src={image} alt={`업로드 이미지 ${index + 1}`} className="w-full h-full object-cover rounded-lg" />
            <Button
              variant="destructive"
              size="icon"
              className="absolute top-1 right-1 w-6 h-6"
              onClick={() => onImageRemove(index)}
            >
              <X className="w-4 h-4" />
            </Button>
          </div>
        ))}
        {images.length < 5 && (
          <label className="aspect-square border-2 border-dashed rounded-lg flex items-center justify-center cursor-pointer">
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={onImageUpload}
              multiple
            />
            <ImagePlus className="w-6 h-6 text-muted-foreground" />
          </label>
        )}
      </div>
    </div>
  );
} 