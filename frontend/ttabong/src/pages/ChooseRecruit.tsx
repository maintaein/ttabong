import React, { useState } from 'react';
import { UserTypeSelect } from './ChooseRecruit/UserTypeSelect';
import { OrgView } from './ChooseRecruit/org/OrgView';
import { VolunteerView } from './ChooseRecruit/volunteer/VolunteerView';

type UserType = 'org' | 'volunteer' | null;

const ChooseRecruit: React.FC = () => {
  const [userType, setUserType] = useState<UserType>(null);

  if (!userType) {
    return <UserTypeSelect onSelect={setUserType} />;
  }

  return userType === 'org' ? <OrgView /> : <VolunteerView />;
};

export default ChooseRecruit;