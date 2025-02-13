import React from "react";
import { useLocation, useNavigate } from 'react-router-dom';
import { ScrollProvider } from '@/contexts/ScrollContext';
import { ScrollArea } from "@/components/ui/scroll-area";
import { ArrowLeft } from "lucide-react";
import NavBar from "@/components/NavBar";

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  const location = useLocation();
  const navigate = useNavigate();

  const handleBack = () => {
    // template-and-group-write 페이지에서는 main으로 이동
    if (location.pathname === '/template-and-group-write') {
      navigate('/main');
    } else {
      navigate(-1);
    }
  };

  return (
    <div className="h-screen grid grid-rows-[56px_1fr_56px]">
      <div className="sticky top-0 z-50 bg-background border-b">
        <div className="flex items-center h-14 px-4">
          <button onClick={handleBack} className="mr-4">
            <ArrowLeft className="h-6 w-6" />
          </button>
        </div>
      </div>

      <main className="overflow-hidden">
        <ScrollProvider>
          <ScrollArea className="h-full">
            {children}
          </ScrollArea>
        </ScrollProvider>
      </main>

      <NavBar />
    </div>
  );
}; 