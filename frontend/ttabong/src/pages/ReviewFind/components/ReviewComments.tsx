import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Send } from 'lucide-react';
import type { Comment } from '@/types/review';

interface ReviewCommentsProps {
  comments: Comment[];
  commentContent: string;
  onCommentChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: (e: React.FormEvent) => void;
}

export function ReviewComments({ 
  comments, 
  commentContent, 
  onCommentChange, 
  onSubmit 
}: ReviewCommentsProps) {
  return (
    <>
      <Card className="border-0 shadow-none mb-16">
        <CardContent className="p-4 space-y-4">
          <h3 className="font-semibold">댓글 {comments.length}개</h3>
          <div className="space-y-4">
            {comments.map((comment) => (
              <div key={comment.commentId} className="flex gap-2 items-start">
                <Avatar className="w-8 h-8">
                  <AvatarFallback>{comment.writerName[0]}</AvatarFallback>
                </Avatar>
                <div>
                  <strong className="text-sm">{comment.writerName}</strong>
                  <p className="text-sm text-gray-600">{comment.content}</p>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
      
      <div className="fixed bottom-[56px] left-0 right-0 bg-background border-t max-w-[600px] mx-auto">
        <form onSubmit={onSubmit} className="p-2 flex gap-2">
          <Input
            value={commentContent}
            onChange={onCommentChange}
            placeholder="댓글을 입력하세요..."
            className="w-full"
          />
          <Button type="submit" size="icon" variant="ghost">
            <Send className="h-4 w-4" />
            <span className="sr-only">댓글 작성</span>
          </Button>
        </form>
      </div>
    </>
  );
} 