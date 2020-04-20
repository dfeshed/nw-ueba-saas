import { queryPromiseRequest } from './utils';

export const fetchAdminEventSettings = () => {
  return queryPromiseRequest(
    'event-settings'
  );
};
