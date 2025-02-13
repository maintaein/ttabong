import React, { useState } from "react";
import { useSprings, animated, to as interpolate } from "@react-spring/web";
import { useDrag } from "react-use-gesture";

const volunteerPosts = [
  { 
    id: 1, 
    title: "환경 정화 활동", 
    location: "서울 한강공원", 
    date: "2025-03-15", 
    description: "한강공원의 쓰레기를 수거하고 깨끗한 환경을 만듭니다.", 
    image: "https://images.pexels.com/photos/3184418/pexels-photo-3184418.jpeg"
  },
  { 
    id: 2, 
    title: "무료 급식 봉사", 
    location: "부산 사랑의 집", 
    date: "2025-03-20", 
    description: "어려운 이웃들에게 따뜻한 한 끼를 제공하는 활동입니다.", 
    image: "https://images.pexels.com/photos/6646912/pexels-photo-6646912.jpeg"
  },
  { 
    id: 3, 
    title: "보육원 아이들과 함께", 
    location: "대전 행복 보육원", 
    date: "2025-04-05", 
    description: "아이들과 놀이 및 학습을 진행하는 따뜻한 봉사입니다.", 
    image: "https://images.pexels.com/photos/2869559/pexels-photo-2869559.jpeg"
  },
  { 
    id: 4, 
    title: "노인 돌봄 봉사", 
    location: "광주 나눔 복지관", 
    date: "2025-04-10", 
    description: "어르신들과 대화하고 생활을 도와드리는 봉사입니다.", 
    image: "https://images.pexels.com/photos/4057753/pexels-photo-4057753.jpeg"
  },
];

const to = (i: number) => ({
  x: 0,
  y: i * -4,
  scale: 1,
  rot: 0,
  delay: i * 100,
});

const from = () => ({ x: 0, rot: 0, scale: 1.2, y: 1000 });

const MainPage: React.FC = () => {
  const [gone] = useState(new Set());
  const [springs, api] = useSprings(volunteerPosts.length, (i) => ({
    ...to(i),
    from: from(),
  }));

  const bind = useDrag(({ args: [index], down, movement: [mx], velocity }) => {
    const trigger = velocity > 0.2;
    if (!down && trigger) gone.add(index);
    api.start((i) => {
      if (index !== i) return;
      const isGone = gone.has(index);
      const x = isGone ? (mx > 0 ? 1000 : -1000) : down ? mx : 0;
      const rot = mx / 100;
      const scale = down ? 1.1 : 1;
      return { x, rot, scale, config: { friction: 50, tension: down ? 800 : isGone ? 200 : 500 } };
    });

    if (!down && gone.size === volunteerPosts.length) {
      setTimeout(() => {
        gone.clear();
        api.start((i) => to(i));
      }, 600);
    }
  });

  return (
    <div className="relative flex items-center justify-center w-screen h-screen bg-gray-100">
      {springs.map(({ x, y, rot, scale }, i) => (
        <animated.div
          key={volunteerPosts[i].id}
          className="absolute w-[320px] h-[480px] bg-white shadow-xl rounded-2xl overflow-hidden"
          style={{
            left: "20%",
            top: "45%",
            transform: interpolate([x, y, rot, scale], (x, y, rot, scale) =>
              `translate3d(${x - 160}px,${y - 240}px,0) rotate(${rot}deg) scale(${scale})`
            ),
          }}
          {...bind(i)}
        >
          <img src={volunteerPosts[i].image} alt={volunteerPosts[i].title} className="w-full h-[60%] object-cover" />
          <div className="p-4">
            <h2 className="text-lg font-bold">{volunteerPosts[i].title}</h2>
            <p className="text-sm text-gray-600">{volunteerPosts[i].location} | {volunteerPosts[i].date}</p>
            <p className="text-sm text-gray-800 mt-2">{volunteerPosts[i].description}</p>
          </div>
        </animated.div>
      ))}
    </div>
  );
};

export default MainPage;
