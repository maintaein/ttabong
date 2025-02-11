import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { 
  Home,
  Search,
  PlusCircle,
  Eye,
  User
} from 'lucide-react';

interface NavItemProps {
  to: string;
  icon: React.ComponentType<{ className?: string }>;
}

const NavItem = ({ to, icon: Icon }: NavItemProps) => {
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
  const isFeedDetail = location.pathname.startsWith('/feed/');
  const isFeedAdd = location.pathname === '/feed/add';

  if (isFeedDetail || isFeedAdd) return null;

  return (
    <nav className="
      fixed bottom-0 left-1/2 -translate-x-1/2 
      w-full max-w-[600px] min-w-[320px] 
      bg-background border-t border-border
      h-14
      z-50
    ">
      <div className="flex justify-around h-full py-2">
        <NavItem to="/main-page" icon={Home} />
        <NavItem to="/recruit-find" icon={Search} />
        <NavItem to="/choose-recruit" icon={PlusCircle} />
        <NavItem to="/review-find" icon={Eye} />
        <NavItem to="/my-page" icon={User} />
      </div>
    </nav>
  );
};

export default NavBar;
