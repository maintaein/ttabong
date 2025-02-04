import { Routes, Route } from 'react-router-dom';
import { ThemeProvider } from "@/hooks/theme-provider";
import { MainLayout } from "@/layout/MainLayout";
import { PageLayout } from "@/layout/PageLayout";


function App() {
  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <div className="min-h-screen bg-zinc-100 dark:bg-zinc-900">
        <div className="mx-auto max-w-[600px] min-w-[320px] h-screen bg-background">
          <MainLayout>
            <PageLayout>
              <Routes>

              </Routes>
            </PageLayout>
          </MainLayout>
        </div>
      </div>
    </ThemeProvider>
  );
}

export default App;
