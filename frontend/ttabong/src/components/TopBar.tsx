import { Bell, ChevronLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useNavigate, useLocation } from "react-router-dom";
import logo from "@/assets/logo_color.svg";

export const TopBar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const showBackButton = !['/', '/main-page', '/recruit-find', '/feed-add', '/review-find', '/my-page'].includes(location.pathname);

  return (
    <div className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="flex h-14 items-center justify-between px-4">
        <div className="flex items-center gap-2">
          {showBackButton ? (
            <Button
              variant="ghost"
              size="icon"
              onClick={() => navigate(-1)}
              className="text-foreground"
            >
              <ChevronLeft className="h-5 w-5" />
              <span className="sr-only">Go back</span>
            </Button>
          ) : (
            <img src={logo} alt="Logo" className="h-10 w-10" />
          )}
        </div>

        <Button variant="ghost" size="icon" className="text-foreground">
          <Bell className="h-5 w-5" />
          <span className="sr-only">Notifications</span>
        </Button>
      </div>
    </div>
  );
}; 