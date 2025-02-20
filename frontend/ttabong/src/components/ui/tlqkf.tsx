import React from "react";

const tlqkf: React.FC = () => {
  const handleIframeError = (e: React.SyntheticEvent<HTMLIFrameElement, Event>) => {
    console.error('iframe 로딩 실패:', e);
  };

  return (
    <div className="w-full h-[calc(100vh-112px)]">
      <iframe 
        src="https://praven.kro.kr/ttabong1"
        className="w-full h-full border-none"
        title="Praven Website"
        onError={handleIframeError}
        sandbox="allow-same-origin allow-scripts allow-forms allow-popups"
      />
    </div>
  );
};

export default tlqkf;
