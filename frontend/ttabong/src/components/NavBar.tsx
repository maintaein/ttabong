import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { Home, Search, PlusCircle, Eye, User } from 'lucide-react';
import { useUserStore } from '@/stores/userStore';

const NavItem = ({ to, icon: Icon }: { to: string; icon: React.ComponentType<{ className?: string }> }) => {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => `
        flex items-center justify-center h-full flex-1
        ${isActive ? 'text-primary' : 'text-muted-foreground'}
      `}
    >
      <Icon className="w-6 h-6" />
    </NavLink>
  );
};

const NavBar = () => {
  const location = useLocation();
  const { userType } = useUserStore();
  const isLoginPage = location.pathname === '/login';
  
  if (isLoginPage) return null;

  const recruitPath = userType === 'volunteer' ? '/recruit' : '/add-recruit';

  return (
    <nav className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-[600px] min-w-[320px] bg-background border-t border-border h-14 z-50">
      <div className="flex justify-around h-full py-2">
        <NavItem to="/main" icon={Home} />
        <NavItem to="/recruit-find" icon={Search} />
        <NavItem to={recruitPath} icon={PlusCircle} />
        <NavItem to="/review-find" icon={Eye} />
        <NavItem to="/mypage" icon={User} />
      </div>
    </nav>
  );
};

export default NavBar;
