import React, { useState } from "react";
import TinderCard from "react-tinder-card";

const volunteerPosts = [
  {
    id: 1,
    title: "따봉이 화이팅",
    description: "공원에서 쓰레기를 줍고 자연을 보호하는 활동입니다.",
    location: "서울, 한강공원",
    date: "2025-02-15",
    image: "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/749/ad58fe995cb9198412288b21bc63e1c9_res.jpeg",
  },
  {
    id: 2,
    title: "노인 돌봄 봉사",
    description: "어르신들과 함께 시간을 보내고 도움을 제공합니다.",
    location: "부산, 행복한 요양원",
    date: "2025-03-10",
    image: "https://i.ytimg.com/vi/KErDjspnCNA/maxresdefault.jpg",
  },
];

const MainPage: React.FC = () => {
  const [posts] = useState(volunteerPosts);

  const swiped = (direction: string, postTitle: string) => {
    console.log(`${postTitle} was swiped ${direction}`);
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
      <h1 className="text-3xl font-bold mb-6">봉사 공고 스와이프</h1>
      <div className="relative w-[500px] h-[900px]">
        {posts.map((post) => (
          <TinderCard
            key={post.id}
            onSwipe={(dir: string) => swiped(dir, post.title)}
            preventSwipe={["up", "down"]}
            className="absolute w-full h-full"
          >
            <div className="bg-white shadow-lg rounded-2xl overflow-hidden w-full h-full flex flex-col">
              <img src={post.image} alt={post.title} className="w-full h-2/3 object-cover" />
              <div className="p-4 flex flex-col justify-between flex-1">
                <h2 className="text-xl font-semibold">{post.title}</h2>
                <p className="text-gray-600 text-sm">{post.description}</p>
                <p className="text-gray-500 text-sm">📍 {post.location}</p>
                <p className="text-gray-500 text-sm">📅 {post.date}</p>
              </div>
            </div>
          </TinderCard>
        ))}
      </div>
    </div>
  );
};

export default MainPage;
