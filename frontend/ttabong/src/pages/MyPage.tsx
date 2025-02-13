import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUserStore } from '@/stores/userStore';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Card, CardContent } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Heart, ClipboardList, PenSquare, User, MapPin, Calendar } from 'lucide-react';
import { LogoutButton } from '@/components/LogoutButton';

export default function MyPage() {
  const navigate = useNavigate();
  const { 
    userId, 
    likedTemplates, 
    isLoadingLikes, 
    hasMoreLikes,
    fetchLikedTemplates 
  } = useUserStore();

  useEffect(() => {
    fetchLikedTemplates({ limit: 10 });
  }, [fetchLikedTemplates]);

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;
    if (scrollHeight - scrollTop <= clientHeight * 1.5 && hasMoreLikes && !isLoadingLikes) {
      const lastTemplateId = likedTemplates[likedTemplates.length - 1]?.templateId;
      if (lastTemplateId) {
        fetchLikedTemplates({ cursor: lastTemplateId, limit: 10 });
      }
    }
  };

  if (!userId) return null;

  return (
    <div className="h-screen overflow-y-auto">
      <div className="flex items-center justify-center p-4">
        <div className="w-full max-w-md space-y-6">
          {/* 프로필 섹션 */}
          <div className="space-y-2 text-center">
            <Avatar className="h-24 w-24 mx-auto border-4 border-background shadow-lg">
              <AvatarImage src="/images/profile.jpg" alt="프로필 이미지" />
              <AvatarFallback className="bg-primary/5">
                <User className="h-12 w-12 text-primary/70" />
              </AvatarFallback>
            </Avatar>
            <h1 className="text-2xl font-semibold tracking-tight mt-4">김봉사</h1>
            <p className="text-sm text-muted-foreground">volunteer@example.com</p>
            <Badge variant="secondary" className="mt-1">개인 회원</Badge>
          </div>

          {/* 활동 관리 섹션 */}
          <Card className="border shadow-sm">
            <CardContent className="p-6 space-y-4">
              <h2 className="text-lg font-semibold tracking-tight">활동 관리</h2>
              <div className="grid grid-cols-2 gap-3">
                <Button 
                  variant="outline" 
                  className="h-auto py-6 hover:bg-primary/5"
                  onClick={() => navigate('/volunteer-history')}
                >
                  <div className="flex flex-col items-center gap-2">
                    <ClipboardList className="h-5 w-5 text-primary" />
                    <span className="text-sm font-medium">봉사 내역</span>
                  </div>
                </Button>
                <Button 
                  variant="outline" 
                  className="h-auto py-6 hover:bg-primary/5"
                  onClick={() => navigate('/my-reviews')}
                >
                  <div className="flex flex-col items-center gap-2">
                    <PenSquare className="h-5 w-5 text-primary" />
                    <span className="text-sm font-medium">봉사후기</span>
                  </div>
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* 관심있는 봉사 섹션 */}
          <Card className="border shadow-sm">
            <CardContent className="p-4">
              <div className="flex justify-between items-center mb-3">
                <h2 className="text-lg font-semibold tracking-tight">관심있는 봉사</h2>
                <Badge variant="secondary">{likedTemplates.length}개</Badge>
              </div>
              <ScrollArea 
                className="h-[calc(100vh-480px)] min-h-[280px] -mx-2 px-2"
                onScroll={handleScroll}
              >
                {isLoadingLikes && likedTemplates.length === 0 ? (
                  <div className="flex justify-center items-center h-[200px]">
                    <p className="text-sm text-muted-foreground">로딩 중...</p>
                  </div>
                ) : (
                  <div className="grid grid-cols-1 gap-2">
                    {likedTemplates.map((template) => (
                      <Card 
                        key={template.templateId} 
                        className="overflow-hidden hover:bg-secondary/40 active:bg-secondary/60 transition-colors"
                        onClick={() => navigate(`/recruits/${template.templateId}`)}
                      >
                        <CardContent className="p-3">
                          <div className="flex gap-3">
                            <div className="relative w-[72px] h-[72px] rounded-md overflow-hidden flex-shrink-0">
                              <img 
                                src={template.thumbnailImg} 
                                alt={template.title}
                                className="w-full h-full object-cover"
                                loading="lazy"
                              />
                              <Heart className="absolute top-1 right-1 h-4 w-4 text-red-500 fill-red-500 drop-shadow-md" />
                            </div>
                            <div className="flex-1 min-w-0 py-0.5">
                              <h3 className="font-medium text-sm line-clamp-2 mb-1.5 leading-snug">
                                {template.title}
                              </h3>
                              <div className="space-y-1">
                                <div className="flex items-center text-muted-foreground">
                                  <MapPin className="h-3 w-3 mr-1 flex-shrink-0" />
                                  <p className="text-xs truncate">{template.activityLocation}</p>
                                </div>
                                <div className="flex items-center text-muted-foreground">
                                  <Calendar className="h-3 w-3 mr-1 flex-shrink-0" />
                                  <p className="text-xs">
                                    {new Date(template.recruit.deadline).toLocaleDateString('ko-KR', {
                                      month: 'long',
                                      day: 'numeric'
                                    })} 마감
                                  </p>
                                </div>
                              </div>
                            </div>
                          </div>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                )}
                {isLoadingLikes && likedTemplates.length > 0 && (
                  <div className="flex justify-center py-3">
                    <p className="text-xs text-muted-foreground">더 불러오는 중...</p>
                  </div>
                )}
              </ScrollArea>
            </CardContent>
          </Card>

          <LogoutButton className="w-full h-11" />
        </div>
      </div>
    </div>
  );
}
