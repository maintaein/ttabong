import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from "@/components/ui/badge";

interface ReviewHeaderProps {
  writer: {
    writerId: number;
    writerName: string;
    writerProfileImage: string;
  };
  organization: {
    orgId: number;
    orgName: string;
  };
  isPublic: boolean;
}

export function ReviewHeader({ writer, organization, isPublic }: ReviewHeaderProps) {
  return (
    <div className="flex items-center gap-3">
      <Avatar className="w-10 h-10">
        <AvatarImage src={writer.writerProfileImage} alt={writer.writerName} />
        <AvatarFallback>{writer.writerName[0]}</AvatarFallback>
      </Avatar>
      <div className="flex-1">
        <div className="flex items-center gap-2">
          <h3 className="font-medium">{writer.writerName}</h3>
          <Badge variant={isPublic ? "default" : "secondary"} className="text-xs">
            {isPublic ? "공개" : "비공개"}
          </Badge>
        </div>
        <p className="text-sm text-muted-foreground">{organization.orgName}</p>
      </div>
    </div>
  );
} 