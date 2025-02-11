import { useState } from 'react';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Send, MoreVertical, Pencil, Trash2 } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import type { Comment } from '@/types/reviewType';

interface ReviewCommentsProps {
  comments: Comment[];
  commentContent: string;
  onCommentChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: (e: React.FormEvent) => void;
  onUpdateComment: (commentId: number, content: string) => Promise<void>;
  onDeleteComment: (commentId: number) => Promise<void>;
}

export function ReviewComments({ 
  comments, 
  commentContent, 
  onCommentChange, 
  onSubmit,
  onUpdateComment,
  onDeleteComment
}: ReviewCommentsProps) {
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState('');

  const handleEditStart = (comment: Comment) => {
    setEditingCommentId(comment.commentId);
    setEditContent(comment.content);
  };

  const handleEditSubmit = async (commentId: number) => {
    if (!editContent.trim()) return;
    await onUpdateComment(commentId, editContent);
    setEditingCommentId(null);
    setEditContent('');
  };

  const handleDelete = async (commentId: number) => {
    if (window.confirm('댓글을 삭제하시겠습니까?')) {
      await onDeleteComment(commentId);
    }
  };

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
                <div className="flex-1">
                  <div className="flex items-center justify-between">
                    <strong className="text-sm">{comment.writerName}</strong>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="icon" className="h-6 w-6">
                          <MoreVertical className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuItem onClick={() => handleEditStart(comment)}>
                          <Pencil className="h-4 w-4 mr-2" />
                          수정하기
                        </DropdownMenuItem>
                        <DropdownMenuItem 
                          onClick={() => handleDelete(comment.commentId)}
                          className="text-destructive"
                        >
                          <Trash2 className="h-4 w-4 mr-2" />
                          삭제하기
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                  {editingCommentId === comment.commentId ? (
                    <div className="flex gap-2 mt-1">
                      <Input
                        value={editContent}
                        onChange={(e) => setEditContent(e.target.value)}
                        className="text-sm"
                      />
                      <Button 
                        size="sm"
                        onClick={() => handleEditSubmit(comment.commentId)}
                      >
                        수정
                      </Button>
                      <Button 
                        size="sm" 
                        variant="outline"
                        onClick={() => setEditingCommentId(null)}
                      >
                        취소
                      </Button>
                    </div>
                  ) : (
                    <p className="text-sm text-gray-600">{comment.content}</p>
                  )}
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