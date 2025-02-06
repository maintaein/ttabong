import React, { ReactNode } from 'react';

interface PageLayoutProps {
  children: ReactNode;
}

export const PageLayout = ({ children }: PageLayoutProps) => {
  return (
    <div className="min-h-[calc(100%-56px)]">
      {children}
    </div>
  );
}; 