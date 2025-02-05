import { Routes, Route } from 'react-router-dom';
import { ThemeProvider } from "@/hooks/theme-provider";
import { MainLayout } from "@/layout/MainLayout";
import { PageLayout } from "@/layout/PageLayout";
import ReviewFind from '@/pages/ReviewFind';
import ReviewDetail from '@/pages/ReviewFind/ReviewDetail';
import ReviewDetailList from '@/pages/ReviewFind/ReviewDetailList';
import ReviewWrite from '@/pages/ChooseRecruit/ReviewWrite';

function App() {
  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <div className="min-h-screen bg-zinc-100 dark:bg-zinc-900">
        <div className="mx-auto max-w-[600px] min-w-[320px] h-screen bg-background">
          <MainLayout>
            <PageLayout>
              <Routes>
                <Route path="/review-find" element={<ReviewFind />} />
                <Route path="/review-find/:reviewId" element={<ReviewDetail />} />
                <Route path="/review-find/:reviewId/reviews" element={<ReviewDetailList />} />
                <Route path="/review-write" element={<ReviewWrite />} />
              </Routes>
            </PageLayout>
          </MainLayout>
        </div>
      </div>
    </ThemeProvider>
  );
}

export default App;
