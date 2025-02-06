import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import DatePicker from "react-datepicker";
import { ko } from "date-fns/locale";
import "react-datepicker/dist/react-datepicker.css";
import { StepProps } from '@/types/template';
import { CalendarIcon } from "lucide-react";

// 초기 그룹 데이터를 TemplateAndGroup과 공유
const INITIAL_GROUPS = [
  { group_id: 1, name: "봉사 그룹 1", templates: [] },
  { group_id: 2, name: "봉사 그룹 2", templates: [] }
];

const Step0GroupSelection: React.FC<StepProps> = ({ templateData, setTemplateData }) => {
  const [groups, setGroups] = useState<{ group_id: number; name: string }[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [newGroupName, setNewGroupName] = useState("");

  // 로컬 스토리지에서 그룹 데이터 로드
  useEffect(() => {
    const storedGroups = localStorage.getItem("volunteerGroups");
    if (storedGroups) {
      setGroups(JSON.parse(storedGroups));
    } else {
      // 초기 데이터 설정
      localStorage.setItem("volunteerGroups", JSON.stringify(INITIAL_GROUPS));
      setGroups(INITIAL_GROUPS);
    }
  }, []);

  // 그룹 추가 함수
  const addNewGroup = () => {
    if (newGroupName.trim()) {
      const newGroup = { 
        group_id: Date.now(), 
        name: newGroupName,
        templates: []
      };
      const updatedGroups = [...groups, newGroup];

      setGroups(updatedGroups);
      localStorage.setItem("volunteerGroups", JSON.stringify(updatedGroups));
      
      // 새로 추가된 그룹 자동 선택
      setTemplateData(prev => ({
        ...prev,
        groupId: newGroup.group_id
      }));
      
      setNewGroupName("");
      setIsDialogOpen(false);
    }
  };

  // 봉사 분야 목록
  const volunteerFields = [
    "교육",
    "문화예술",
    "환경",
    "사회복지",
    "보건의료",
    "농어촌",
    "기타"
  ];

  // 봉사 분야 토글 함수
  const toggleField = (field: string) => {
    setTemplateData(prev => ({
      ...prev,
      volunteerField: prev.volunteerField.includes(field)
        ? prev.volunteerField.filter(f => f !== field)
        : [...prev.volunteerField, field]
    }));
  };

  // 오늘 날짜 생성
  const today = new Date();
  today.setHours(0, 0, 0, 0); // 시간을 00:00:00으로 설정

  return (
    <div>
      <h2 className="text-lg font-semibold mb-4">공고 그룹 선택</h2>

      <Select
        onValueChange={(value) =>
          setTemplateData((prev) => ({ ...prev, groupId: Number(value) }))
        }
        value={templateData.groupId?.toString() || ""}
      >
        <SelectTrigger>
          <SelectValue placeholder="공고 그룹 선택" />
        </SelectTrigger>
        <SelectContent>
          {groups.map((group) => (
            <SelectItem key={group.group_id} value={group.group_id.toString()}>
              {group.name}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      {/* 🔹 그룹 추가 버튼 */}
      <Button className="mt-2" onClick={() => setIsDialogOpen(true)}>
        그룹 추가
      </Button>

      {/* 🔹 그룹 추가 다이얼로그 */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>새 그룹 추가</DialogTitle>
          </DialogHeader>
          <input
            type="text"
            className="w-full p-2 border rounded-md"
            placeholder="그룹명 입력"
            value={newGroupName}
            onChange={(e) => setNewGroupName(e.target.value)}
          />
          <DialogFooter>
            <Button onClick={addNewGroup}>추가</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 🔹 모집 기간 선택 */}
      <div className="mt-4">
        <label className="block text-sm font-medium text-gray-700">모집 기간</label>
        <div className="flex gap-2">
          <div className="relative flex-1">
            <DatePicker
              selected={templateData.startDate}
              onChange={(date) => setTemplateData((prev) => ({
                ...prev,
                startDate: date,
                endDate: null,
                volunteerDate: null
              }))}
              placeholderText="시작일 선택"
              className="w-full p-2 pl-10 border rounded-md"
              minDate={today}
              locale={ko}
              dateFormat="yyyy년 MM월 dd일"
            />
            <CalendarIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          </div>

          <span className="self-center">~</span>

          <div className="relative flex-1">
            <DatePicker
              selected={templateData.endDate}
              onChange={(date) => setTemplateData((prev) => ({
                ...prev,
                endDate: date,
                volunteerDate: null
              }))}
              placeholderText="종료일 선택"
              className="w-full p-2 pl-10 border rounded-md"
              minDate={templateData.startDate || today}
              locale={ko}
              dateFormat="yyyy년 MM월 dd일"
            />
            <CalendarIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          </div>
        </div>
      </div>

      {/* 🔹 봉사일 선택 */}
      <div className="mt-4">
        <label className="block text-sm font-medium text-gray-700">봉사일</label>
        <div className="relative">
          <DatePicker
            selected={templateData.volunteerDate}
            onChange={(date) => setTemplateData((prev) => ({ ...prev, volunteerDate: date }))}
            placeholderText="봉사일 선택"
            className="w-full p-2 pl-10 border rounded-md"
            minDate={templateData.endDate || today}
            locale={ko}
            dateFormat="yyyy년 MM월 dd일"
          />
          <CalendarIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        </div>
      </div>

      {/* 🔹 봉사 시간 선택 */}
      <div className="mt-4">
        <label className="block text-sm font-medium text-gray-700">봉사 시간</label>
        <div className="flex gap-2">
          <input
            type="time"
            className="w-1/2 p-2 border rounded-md"
            value={templateData.startTime}
            onChange={(e) => setTemplateData((prev) => ({ ...prev, startTime: e.target.value }))}
          />
          <span className="self-center">~</span>


          <input
            type="time"
            className="w-1/2 p-2 border rounded-md"
            value={templateData.endTime}
            onChange={(e) => setTemplateData((prev) => ({ ...prev, endTime: e.target.value }))}
          />
        </div>


      </div>

      {/* 봉사 분야 선택 추가 */}
      <div className="mt-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">봉사 분야</label>
        <div className="grid grid-cols-3 gap-2">
          {volunteerFields.map((field) => (
            <button
              key={field}
              type="button"
              onClick={() => toggleField(field)}
              className={`p-2 border rounded-md text-sm
                ${templateData.volunteerField.includes(field)
                  ? "bg-blue-500 text-white"
                  : "bg-gray-100"
                }`}
            >
              {field}
            </button>
          ))}
        </div>
        <p className="text-xs text-gray-500 mt-1">* 다중선택 가능</p>
      </div>
    </div>
  );
};

export default Step0GroupSelection;
