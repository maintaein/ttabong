import React from "react";
import { Bell } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useNavigate, useLocation } from "react-router-dom";
import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";

export const TopBar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [showBackDialog, setShowBackDialog] = useState(false);

  const handleBackClick = () => {
    // 템플릿 작성 페이지일 때만 다이얼로그 표시
    if (location.pathname === "/template-and-group-write") {
      setShowBackDialog(true);
    } else {
      navigate(-1);
    }
  };

  const handleConfirmBack = () => {
    setShowBackDialog(false);
    navigate("/template-and-group");
  };

  return (
    <>
      <div className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="flex h-14 items-center justify-between px-4">
          <div className="flex items-center gap-2">
            <button onClick={handleBackClick} className="p-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={1.5}
                stroke="currentColor"
                className="w-6 h-6"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M15.75 19.5L8.25 12l7.5-7.5"
                />
              </svg>
            </button>
          </div>

          <Button variant="ghost" size="icon" className="text-foreground">
            <Bell className="h-5 w-5" />
            <span className="sr-only">Notifications</span>
          </Button>
        </div>
      </div>

      {/* 뒤로가기 확인 다이얼로그 */}
      <Dialog open={showBackDialog} onOpenChange={setShowBackDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>작성 취소</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            작성 중인 내용이 저장되지 않습니다.
            초기 목록 화면으로 돌아가시겠습니까?
          </div>
          <DialogFooter className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => setShowBackDialog(false)}
            >
              아니오
            </Button>
            <Button
              variant="destructive"
              onClick={handleConfirmBack}
            >
              예
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}; 