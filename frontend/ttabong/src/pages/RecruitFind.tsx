import React, { useState } from "react";
import { FaSearch, FaBell} from "react-icons/fa";

const savedPosts = [
  {
    id: 1,
    title: "아름다운가게 부산사상점 매장운영지원",
    date: "2024.12.01 - 2025.02.28",
    location: "부산광역시 사상구",
    image: "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/749/ad58fe995cb9198412288b21bc63e1c9_res.jpeg",
  },
  {
    id: 2,
    title: "아름다운가게 부산사상점 매장운영지원",
    date: "2024.12.01 - 2025.02.28",
    location: "부산광역시 사상구",
    image: "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/749/ad58fe995cb9198412288b21bc63e1c9_res.jpeg",
  },
  {
    id: 2,
    title: "아름다운가게 부산사상점 매장운영지원",
    date: "2024.12.01 - 2025.02.28",
    location: "부산광역시 사상구",
    image: "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/749/ad58fe995cb9198412288b21bc63e1c9_res.jpeg",
  },
  {
    id: 2,
    title: "아름다운가게 부산사상점 매장운영지원",
    date: "2024.12.01 - 2025.02.28",
    location: "부산광역시 사상구",
    image: "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/749/ad58fe995cb9198412288b21bc63e1c9_res.jpeg",
  },
];

const RecruitFind: React.FC = () => {
  const [posts, setPosts] = useState(savedPosts);

  const removePost = (id: number) => {
    setPosts(posts.filter((post) => post.id !== id));
  };

  return (
    <div className="bg-blue-200 min-h-screen flex justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-xl shadow-lg p-4">
        {/* 헤더 */}
        <div className="flex justify-between items-center">
          <h1 className="text-2xl font-bold flex items-center">
            <span className="mr-2">TTABONG</span> 🤟
          </h1>
          <FaBell className="text-xl text-gray-600" />
        </div>

        {/* 검색창 */}
        <div className="mt-3 relative">
          <input
            type="text"
            placeholder="검색"
            className="w-full p-2 pl-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
          <FaSearch className="absolute left-3 top-3 text-gray-500" />
        </div>

        {/* 봉사 공고 리스트 */}
        <div className="mt-4">
          {posts.map((post) => (
            <div key={post.id} className="bg-white shadow rounded-lg overflow-hidden mb-3">
              <img src={post.image} alt={post.title} className="w-full h-40 object-cover" />
              <div className="p-3">
                <h2 className="text-lg font-bold">{post.title}</h2>
                <p className="text-gray-500 text-sm">{post.date}</p>
                <p className="text-gray-600 text-sm">📍 {post.location}</p>
                <button
                  onClick={() => removePost(post.id)}
                  className="bg-gray-300 text-gray-700 text-xs px-3 py-1 rounded-full mt-2"
                >
                  삭제
                </button>
              </div>
            </div>
          ))}
        </div>

    
      </div>
    </div>
  );
};

export default RecruitFind;
