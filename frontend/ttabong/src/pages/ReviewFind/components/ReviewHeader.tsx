import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import type { ReviewDetail } from '@/types/review';

interface ReviewHeaderProps {
  writer: ReviewDetail['writer'];
  organization: ReviewDetail['organization'];
}

export function ReviewHeader({ writer, organization }: ReviewHeaderProps) {
  return (
    <div className="flex items-center justify-between p-4 border-b">
      <div className="flex items-center gap-3">
        <Avatar>
          <AvatarImage src={writer.writerProfileImage} alt={writer.writerName} />
          <AvatarFallback>{writer.writerName[0]}</AvatarFallback>
        </Avatar>
        <div>
          <div className="flex items-center gap-2">
            <h3 className="font-medium">{writer.writerName}</h3>
            <Avatar className="w-4 h-4">
              <AvatarImage src="/user-icon.png" alt="User" />
              <AvatarFallback>U</AvatarFallback>
            </Avatar>
          </div>
          <div className="flex items-center gap-2">
            <p className="text-sm text-muted-foreground">{organization.orgName}</p>
            <Avatar className="w-4 h-4">
              <AvatarImage src="/org-icon.png" alt="Organization" />
              <AvatarFallback>O</AvatarFallback>
            </Avatar>
          </div>
        </div>
      </div>
      <Button variant="ghost" size="icon">
        <span className="sr-only">Edit review</span>
      </Button>
    </div>
  );
} 