import React, { useEffect, useState } from "react";
import { useSprings, animated, to as interpolate } from "@react-spring/web";
import { useDrag } from "react-use-gesture";

// API 응답 데이터 타입 정의
type RecruitData = {
  recruit: {
    recruitId: number;
    status: string;
    maxVolunteer: number;
    participateVolCount: number;
    activityDate?: string;
  };
  group?: {
    groupId: number;
    groupName?: string;
  };
  template?: {
    templateId: number;
    title?: string;
  };
};

// 카드에 표시할 데이터 타입
type VolunteerPost = {
  id: number;
  title: string;
  location: string;
  date: string;
  description: string;
  image?: string | null;
};

const API_URL = "https://ttabong.store/api/org/recruits?cursor=null&limit=10";

const to = (i: number) => ({
  x: 0,
  y: i * -4,
  scale: 1,
  rot: 0,
  delay: i * 100,
});

const from = () => ({ x: 0, rot: 0, scale: 1.2, y: 1000 });

const MainPage: React.FC = () => {
  const [volunteerPosts, setVolunteerPosts] = useState<VolunteerPost[]>([]);
  const [gone] = useState(new Set());

  useEffect(() => {
    const fetchRecruits = async () => {
      try {
        const response = await fetch(API_URL);
        const data = await response.json();

        // 데이터 가공하여 VolunteerPost 타입에 맞게 변환
        const formattedData: VolunteerPost[] = data.recruits.map((item: RecruitData, index: number) => ({
          id: item.recruit.recruitId,
          title: item.template?.title || "제목 없음", // title이 없으면 "제목 없음"
          location: item.group?.groupName || "위치 미정",
          date: item.recruit?.activityDate?.split("T")[0] || "날짜 미정", // 날짜가 없으면 "날짜 미정"
          description: `모집 상태: ${item.recruit?.status} | 신청 인원: ${item.recruit?.participateVolCount || 0}/${item.recruit?.maxVolunteer || "?"}`,
          image: item.template?.title
            ? `https://source.unsplash.com/400x300/?volunteer&sig=${index}` // 랜덤 이미지
            : null, // title이 없으면 이미지 없음
        }));

        setVolunteerPosts(formattedData);
      } catch (error) {
        console.error("Error fetching recruit data:", error);
      }
    };

    fetchRecruits();
  }, []);

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
          key={volunteerPosts[i]?.id}
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
          {volunteerPosts[i]?.image && (
            <img
              src={volunteerPosts[i]?.image}
              alt={volunteerPosts[i]?.title}
              className="w-full h-[60%] object-cover"
            />
          )}
          <div className="p-4">
            {volunteerPosts[i]?.title && <h2 className="text-lg font-bold">{volunteerPosts[i]?.title}</h2>}
            <p className="text-sm text-gray-600">{volunteerPosts[i]?.location} | {volunteerPosts[i]?.date}</p>
            <p className="text-sm text-gray-800 mt-2">{volunteerPosts[i]?.description}</p>
          </div>
        </animated.div>
      ))}
    </div>
  );
};

export default MainPage;
