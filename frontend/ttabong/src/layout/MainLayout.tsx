import { TopBar } from "@/components/TopBar";
import NavBar from "@/components/NavBar";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  return (
    <div className="h-screen grid grid-rows-[56px_1fr_56px]">
      <TopBar />
      
      <main className="overflow-hidden">
        <ScrollArea className="h-full">
          {children}
          <ScrollBar 
            orientation="vertical" 
            className="hover:bg-muted/50 transition-colors"
          />
        </ScrollArea>
      </main>

      <NavBar />
    </div>
  );
}; 