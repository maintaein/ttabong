import React, { useEffect, useState } from 'react';
import { RecruitList } from './RecruitList';
import { useRecruitStore } from '@/stores/recruitStore';
import { Button } from '@/components/ui/button';
import { toast } from 'react-hot-toast';
import { recruitApi } from "@/api/recruitApi";

const STATUS_MAP = {
  '모집중': 'RECRUITING',
  '모집마감': 'CLOSED',
  '활동완료': 'COMPLETED'
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

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  return (
    <div className="container mx-auto px-4 py-6">
      <div className="flex justify-between items-center mb-6">
        <div className="flex items-center gap-4">
          <h1 className="text-2xl font-bold">봉사 공고 목록</h1>
          <Button
            variant="outline"
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
              onClick={handleDeleteSelected}
              disabled={selectedRecruits.length === 0}
            >
              선택 삭제
            </Button>
          )}
        </div>
        <div className="space-x-2">
          {['모집중', '모집마감', '활동완료'].map(status => (
            <button
              key={status}
              onClick={() => setSelectedStatus(status)}
              className={`px-4 py-2 rounded ${
                selectedStatus === status 
                  ? 'bg-blue-500 text-white' 
                  : 'bg-gray-100'
              }`}
            >
              {status}
            </button>
          ))}
        </div>
      </div>
      <RecruitList 
        recruits={filteredRecruits}
        isEditing={isEditing}
        selectedRecruits={selectedRecruits}
        onSelectRecruit={(recruitId) => {
          setSelectedRecruits(prev => 
            prev.includes(recruitId)
              ? prev.filter(id => id !== recruitId)
              : [...prev, recruitId]
          );
        }}
      />
    </div>
  );
}; 