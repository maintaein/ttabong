import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Group, Template } from '@/types/template';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";

// const tempGroupName1 = config.name[7];
// const tempGroupName2 = config.name[2];

// 초기 그룹 데이터 (로컬 스토리지에 저장용)
const INITIAL_GROUPS = [
  { group_id: 1, name: "봉사 그룹1", templates: [] },
  { group_id: 2, name: "봉사 그룹2", templates: [] }
];


const TemplateAndGroup = () => {
  const navigate = useNavigate();
  const [groups, setGroups] = useState<Group[]>([]);
  const [refresh, setRefresh] = useState(0);
  const [deleteDialog, setDeleteDialog] = useState<{
    isOpen: boolean;
    type: 'group' | 'template';
    groupId: number;
    templateId?: number;
  }>({
    isOpen: false,
    type: 'group',
    groupId: 0,
    templateId: undefined
  });

  const location = useLocation();
  
  useEffect(() => {
    const storedGroups = localStorage.getItem("volunteerGroups");
    const storedTemplates = localStorage.getItem("volunteerTemplates");
    
    if (storedGroups) {
      const parsedGroups = JSON.parse(storedGroups);
      const templates = storedTemplates ? JSON.parse(storedTemplates) : [];
      
      const groupsWithTemplates = parsedGroups.map((group:Group) => ({
        ...group,
        templates: templates.filter((t: Template) => t.group_id === group.group_id)
      }));
      
      setGroups(groupsWithTemplates);
    } else {
      localStorage.setItem("volunteerGroups", JSON.stringify(INITIAL_GROUPS));
      setGroups(INITIAL_GROUPS);
    }
  }, [refresh]);

  useEffect(() => {
    if (location.state?.newTemplate) {
      setRefresh(prev => prev + 1);
    }
  }, [location]);

  const handleDeleteGroup = (groupId: number) => {
    const updatedGroups = groups.filter(group => group.group_id !== groupId);
    setGroups(updatedGroups);
    localStorage.setItem("volunteerGroups", JSON.stringify(updatedGroups));
  };

  const handleDeleteTemplate = (groupId: number, templateId: number) => {
    // 로컬 스토리지에서도 템플릿 삭제
    const storedTemplates = localStorage.getItem("volunteerTemplates");
    if (storedTemplates) {
      const templates = JSON.parse(storedTemplates);
      const updatedTemplates = templates.filter((t: Template) => t.template_id !== templateId);
      localStorage.setItem("volunteerTemplates", JSON.stringify(updatedTemplates));
    }


    // 상태 업데이트
    setGroups(groups.map(group =>
      group.group_id === groupId
        ? { ...group, templates: group.templates.filter((t) => t.template_id !== templateId) }
        : group
    ));
  };

  // 삭제 다이얼로그 열기
  const openDeleteDialog = (type: 'group' | 'template', groupId: number, templateId?: number) => {
    setDeleteDialog({
      isOpen: true,
      type,
      groupId,
      templateId
    });
  };

  // 삭제 확인
  const confirmDelete = () => {
    if (deleteDialog.type === 'group') {
      handleDeleteGroup(deleteDialog.groupId);
    } else if (deleteDialog.type === 'template' && deleteDialog.templateId) {
      handleDeleteTemplate(deleteDialog.groupId, deleteDialog.templateId);
    }
    setDeleteDialog(prev => ({ ...prev, isOpen: false }));
  };

  return (
    <div className="flex flex-col h-full">
      {/* 메인 컨텐츠 영역 */}
      <div className="flex-1 bg-gray-50 p-4 space-y-6 mb-24">
        {groups.map((group) => (
          <Card key={group.group_id} className="p-4 bg-white shadow-md">
            <div className="flex justify-between items-center mb-4 border-b pb-3">
              <h2 className="text-lg font-semibold flex items-center">
                {group.name}
                <span className="text-sm text-gray-500 ml-2">(그룹)</span>
              </h2>
              <span 
                className="text-red-500 hover:text-red-700 text-sm cursor-pointer"
                onClick={() => openDeleteDialog('group', group.group_id)}
              >
                삭제
              </span>
            </div>

            <div className="space-y-3">
              {group.templates.length > 0 ? (
                group.templates.map((template: Template) => (
                  <div 
                    key={template.template_id} 
                    className="p-3 bg-gray-50 rounded-lg border hover:shadow-sm transition-shadow"
                  >
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="font-medium text-gray-900">{template.title}</p>
                        <p className="text-sm text-gray-500 mt-1">{template.description}</p>
                      </div>
                      <div className="flex gap-3 text-sm">
                        <button
                          className="text-blue-500 hover:text-blue-700"
                          onClick={() => navigate(`/template-and-group-write`, {
                            state: { templateId: template.template_id }
                          })}
                        >
                          사용
                        </button>
                        <button
                          className="text-red-500 hover:text-red-700"
                          onClick={() => openDeleteDialog('template', group.group_id, template.template_id)}
                        >
                          삭제
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-center text-gray-500 py-4">
                  아직 템플릿이 없습니다
                </p>
              )}
            </div>
          </Card>
        ))}
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed inset-x-0 bottom-[72px] mx-4">
        <div className="max-w-[500px] mx-auto w-full bg-white p-4 border rounded-lg shadow-md">
          <button 
            className="w-full bg-blue-500 text-white hover:bg-blue-600 active:bg-blue-700 
                       focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 
                       py-4 rounded-lg font-medium transition-colors text-lg"
            onClick={() => navigate("/template-and-group-write")}
          >
            새로운 템플릿으로 공고 작성
          </button>
        </div>
      </div>

      {/* 삭제 확인 다이얼로그 */}
      <Dialog open={deleteDialog.isOpen} onOpenChange={(open) => setDeleteDialog(prev => ({ ...prev, isOpen: open }))}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>삭제 확인</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            {deleteDialog.type === 'group' 
              ? "정말 이 그룹을 삭제하시겠습니까?"
              : "정말 이 템플릿을 삭제하시겠습니까?"
            }
          </div>
          <DialogFooter className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => setDeleteDialog(prev => ({ ...prev, isOpen: false }))}
            >
              아니오
            </Button>
            <Button
              variant="destructive"
              onClick={confirmDelete}
            >
              네
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default TemplateAndGroup;
