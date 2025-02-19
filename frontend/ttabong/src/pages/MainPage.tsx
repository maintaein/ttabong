import React, { useEffect, useState } from "react";
import { useSprings, animated, to as interpolate } from "@react-spring/web";
import { useDrag } from "react-use-gesture";
import axiosInstance from '@/api/axiosInstance';

// API 응답 데이터 타입 정의
type RecruitData = {
  template: {
    templateId: number;
    title: string;
    activityLocation: string;
    status: string;
    imageId?: string;
    description: string;
    createdAt: string;
  };
  group: {
    groupId: number;
    groupName: string;
  };
  organization: {
    orgId: number;
    orgName: string;
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
        const response = await axiosInstance.get('/vol/templates', {
          params: {
            cursor: 0,
            limit: 20
          }
        });
        
        const formattedData: VolunteerPost[] = response.data.templates.map((item: RecruitData, index: number) => ({
          id: item.template.templateId,
          title: item.template.title,
          location: item.group.groupName,
          date: item.template.createdAt.split('T')[0],
          description: `${item.organization.orgName} | ${item.template.description}`,
          image: item.template.imageId || `https://source.unsplash.com/400x300/?volunteer&sig=${index}`,
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
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-112px)] bg-background relative">
      <div className="w-full max-w-[600px] h-[calc(100vh-180px)] relative">
        {springs.map(({ x, y, rot, scale }, i) => (
          <animated.div
            key={volunteerPosts[i]?.id}
            className="absolute w-[90vw] max-w-[400px] h-[70vh] max-h-[600px] bg-white shadow-xl rounded-2xl overflow-hidden"
            style={{
              left: "50%",
              top: "50%",
              transform: interpolate(
                [x, y, rot, scale],
                (x, y, rot, scale) =>
                  `translate3d(calc(${x}px - 50%), calc(${y}px - 50%), 0) rotate(${rot}deg) scale(${scale})`
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
            <div className="p-6">
              <h2 className="text-xl font-bold mb-2">{volunteerPosts[i]?.title}</h2>
              <p className="text-sm text-muted-foreground mb-4">
                {volunteerPosts[i]?.location} | {volunteerPosts[i]?.date}
              </p>
              <p className="text-sm text-foreground">{volunteerPosts[i]?.description}</p>
            </div>
          </animated.div>
        ))}
      </div>
    </div>
  );
};

export default MainPage;
