import React from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import type { OrgRecruit } from '@/types/recruitType';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { recruitApi } from "@/api/recruitApi";
import { useNavigate } from 'react-router-dom';

const STATUS_MAP = {
  '모집중': { label: '모집중', className: 'bg-green-100 text-green-700' },
  '모집마감': { label: '모집마감', className: 'bg-yellow-100 text-yellow-700' },
  '활동완료': { label: '활동완료', className: 'bg-blue-100 text-blue-700' },
} as const;

interface RecruitCardProps {
  recruit: OrgRecruit;
  onDelete: () => void;
}

const formatTime = (time: number) => {
  const hours = Math.floor(time);
  const minutes = Math.round((time - hours) * 60);
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
};

export const RecruitCard: React.FC<RecruitCardProps> = ({ recruit, onDelete }) => {
  const { toast } = useToast();
  const navigate = useNavigate();

  const handleDelete = async () => {
    try {
      await recruitApi.deleteRecruit(recruit.recruit.recruitId);
      toast({
        title: "공고 삭제",
        description: "공고가 삭제되었습니다.",
      });
      onDelete();
    } catch (error) {
      console.error("공고 삭제 오류:", error);
      toast({
        title: "오류",
        description: "공고 삭제에 실패했습니다.",
        variant: "destructive",
      });
    }
  };

  const handleEditClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    navigate('/template-and-group-write', {
      state: { 
        isRecruitEdit: true,
        recruitId: recruit.recruit.recruitId,
        templateId: recruit.template.templateId,
        template: recruit.template,
        recruitData: {
          deadline: recruit.recruit.deadline,
          activityDate: recruit.recruit.activityDate,
          activityStart: recruit.recruit.activityStart,
          activityEnd: recruit.recruit.activityEnd,
          maxVolunteer: recruit.recruit.maxVolunteer,
          groupId: recruit.group.groupId,
          status: recruit.recruit.status
        }
      }
    });
  };

  const handleClick = () => {
    console.log('Navigating to detail with recruitId:', recruit.recruit.recruitId);
    navigate(`/recruits/${recruit.recruit.recruitId}`, { 
      state: { recruit } 
    });
  };

  return (
    <Card 
      className="p-4 hover:shadow-md transition-shadow cursor-pointer"
      onClick={handleClick}
    >
      <div className="flex justify-between items-start">
        <div className="space-y-2">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">{recruit.group.groupName}</p>
            <h3 className="font-semibold">{recruit.template.title}</h3>
          </div>
          <div className="space-y-1">
            <p className="text-sm">
              활동일: {recruit.recruit.activityDate} {formatTime(recruit.recruit.activityStart)}~{formatTime(recruit.recruit.activityEnd)}
            </p>
            <p className="text-sm">
              신청현황: {recruit.recruit.participateVolCount}/{recruit.recruit.maxVolunteer}명
            </p>
            <p className="text-sm text-muted-foreground">
              마감일: {new Date(recruit.recruit.deadline).toLocaleDateString()}
            </p>
          </div>
        </div>
        <Badge 
          variant="secondary"
          className={cn(
            "ml-2",
            STATUS_MAP[recruit.recruit.status].className
          )}
        >
          {STATUS_MAP[recruit.recruit.status].label}
        </Badge>
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={handleEditClick}
          >
            수정
          </Button>
          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button variant="ghost" size="sm" className="text-destructive">
                삭제
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>공고 삭제</AlertDialogTitle>
                <AlertDialogDescription>
                  이 공고를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>취소</AlertDialogCancel>
                <AlertDialogAction onClick={handleDelete}>삭제</AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </div>
    </Card>
  );
}; 