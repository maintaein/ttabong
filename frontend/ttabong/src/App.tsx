import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { ThemeProvider } from "@/hooks/theme-provider";
import { MainLayout } from "@/layout/MainLayout";
import { PageLayout } from "@/layout/PageLayout";
import Me from "@/pages/Me";
import TemplateAndGroup from "@/pages/Me/TemplateAndGroup";
import TemplateAndGroupWrite from "@/pages/Me/TemplateAndGroupWrite";
import { Toaster } from "sonner";
import ChooseRecruit from "@/pages/ChooseRecruit";
import ReviewFind from "@/pages/ReviewFind";
import ReviewDetail from "@/pages/ReviewFind/ReviewDetail";
import ReviewDetailList from "@/pages/ReviewFind/ReviewDetailList";
import RecruitDetail from "@/pages/ChooseRecruit/org/RecruitDetail";

const App: React.FC = () => {
  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <Toaster />
      <div className="min-h-screen bg-zinc-100 dark:bg-zinc-900">
        <div className="mx-auto max-w-[600px] min-w-[320px] h-screen bg-background">
          <MainLayout>
            <PageLayout>
              <Routes>
                <Route path="/" element={<Me />} />
                <Route path="/my-page" element={<Me />} />
                <Route path="/template-and-group" element={<TemplateAndGroup />} />
                <Route path="/template-and-group-write" element={<TemplateAndGroupWrite />} />
                <Route path="/choose-recruit" element={<ChooseRecruit />} />
                <Route path="/review-find" element={<ReviewFind />} />
                <Route path="/review-find/:reviewId" element={<ReviewDetail />} />
                <Route path="/review-find/:reviewId/reviews" element={<ReviewDetailList />} />
                <Route path="/recruits/:recruitId" element={<RecruitDetail />} />
              </Routes>
            </PageLayout>
          </MainLayout>
        </div>
      </div>
    </ThemeProvider>
  );
};

export default App;
