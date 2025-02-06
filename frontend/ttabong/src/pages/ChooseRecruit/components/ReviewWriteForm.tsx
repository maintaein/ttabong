import { Textarea } from '@/components/ui/textarea';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Card } from '@/components/ui/card';

interface ReviewWriteFormProps {
  title: string;
  content: string;
  isPublic: boolean;
  onTitleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onContentChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  onPublicChange: (checked: boolean) => void;
}

export function ReviewWriteForm({
  title,
  content,
  isPublic, 
  onTitleChange,
  onContentChange,
  onPublicChange,
}: ReviewWriteFormProps) {
  return (
    <div className="space-y-6 p-4">
      <div className="space-y-2">
        <Label htmlFor="title">제목</Label>
        <Input
          id="title"
          value={title}
          onChange={onTitleChange}
          maxLength={100}
          placeholder="제목을 입력하세요"
        />
        <p className="text-sm text-muted-foreground text-right">{title.length}/100</p>
      </div>
      
      <div className="space-y-2">
        <Label htmlFor="content">내용</Label>
        <Textarea
          id="content"
          value={content}
          onChange={onContentChange}
          maxLength={2000}
          placeholder="내용을 입력하세요"
          className="min-h-[200px]"
        />
        <p className="text-sm text-muted-foreground text-right">{content.length}/2000</p>
      </div>

      <Card className="p-4">
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <Label htmlFor="public" className="text-base">공개 설정</Label>
            <p className="text-sm text-muted-foreground">
              {isPublic ? '모든 사용자가 볼 수 있습니다' : '나만 볼 수 있습니다'}
            </p>
          </div>
          <Switch
            id="public"
            checked={isPublic}
            onCheckedChange={onPublicChange}
          />
        </div>
      </Card>
    </div>
  );
} 