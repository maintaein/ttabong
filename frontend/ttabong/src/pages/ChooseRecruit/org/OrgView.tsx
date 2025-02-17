import React, { useEffect, useState } from 'react';
import { RecruitList } from './RecruitList';
import { useRecruitStore } from '@/stores/recruitStore';
import { Button } from '@/components/ui/button';
import { toast } from 'react-hot-toast';
import { recruitApi } from "@/api/recruitApi";

const STATUS_MAP = {
  'RECRUITING': { label: '모집중', className: 'bg-green-100 text-green-700' },
  'RECRUITMENT_CLOSED': { label: '모집마감', className: 'bg-yellow-100 text-yellow-700' },
  'ACTIVITY_COMPLETED': { label: '활동완료', className: 'bg-blue-100 text-blue-700' }
} as const;

interface OrgViewProps {
  onRecruitClick: (recruitId: number) => void;
}

export const OrgView: React.FC<OrgViewProps> = ({ onRecruitClick }) => {
  const { recruitList, isLoading, error, fetchRecruitList } = useRecruitStore();
  const [selectedStatus, setSelectedStatus] = useState<string>('모집중');
  const [isEditing, setIsEditing] = useState(false);
  const [selectedRecruits, setSelectedRecruits] = useState<number[]>([]);

  useEffect(() => {
    fetchRecruitList();
  }, [fetchRecruitList]);

  const filteredRecruits = recruitList?.filter(item => 
    item.recruit.status === STATUS_MAP[selectedStatus as keyof typeof STATUS_MAP]
  ) || [];

  const handleDeleteSelected = async () => {
    if (selectedRecruits.length === 0) return;

    try {
      console.log('Deleting recruits:', selectedRecruits); // 디버깅용
      const response = await recruitApi.deleteRecruit(selectedRecruits);
      console.log('Delete response:', response); // 디버깅용
      
      toast.success('선택한 공고가 삭제되었습니다.');
      setSelectedRecruits([]);
      setIsEditing(false);
      fetchRecruitList(); // 목록 새로고침
    } catch (error) {
      console.error('Delete error:', error); // 디버깅용
      toast.error('공고 삭제에 실패했습니다.');
    }
  };

  const handleSelectRecruit = (recruitId: number) => {
    setSelectedRecruits(prev => 
      prev.includes(recruitId)
        ? prev.filter(id => id !== recruitId)
        : [...prev, recruitId]
    );
  };

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  return (
    <div className="container max-w-2xl mx-auto px-4 py-4">
      <div className="space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center gap-4">
          <h1 className="text-xl font-semibold">봉사 공고 목록</h1>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => {
                setIsEditing(!isEditing);
                setSelectedRecruits([]);
              }}
            >
              {isEditing ? '완료' : '편집'}
            </Button>
            {isEditing && (
              <Button
                variant="destructive"
                size="sm"
                onClick={handleDeleteSelected}
                disabled={selectedRecruits.length === 0}
              >
                선택 삭제
              </Button>
            )}
          </div>
        </div>

        <div className="flex gap-2 overflow-x-auto pb-2">
          {Object.entries(STATUS_MAP).map(([key, value]) => (
            <Button
              key={key}
              variant={selectedStatus === key ? "default" : "outline"}
              size="sm"
              onClick={() => setSelectedStatus(key)}
              className="whitespace-nowrap"
            >
              {value.label}
            </Button>
          ))}
        </div>
      </div>

      <div className="mt-4">
        <RecruitList 
          recruits={filteredRecruits}
          isEditing={isEditing}
          selectedRecruits={selectedRecruits}
          onSelectRecruit={handleSelectRecruit}
        />
      </div>
    </div>
  );
}; 