export default {
  // Entity details Url
  userDetails: '/presidio/api/user/{entityId}/details',

  //  For getting alerts for entity '/presidio/api/alerts?entity_id={entityId}&load_comments=true
  entityAlerts: '/presidio/api/alerts?',

  // IndicatorEvents: /presidio/api/evidences/{indicatorId}/events?page=1&size=100&sort_direction=DESC
  indicatorEvents: '/presidio/api/evidences/{indicatorId}/events?',

  // Histogram:
  // presidio/api/evidences/{indicatorId}/historical-data?feature=high_number_of_successful_object_change_operations&function=distinctEventsByTime
  // presidio/api/evidences/{indicatorId}/historical-data?feature=abnormal_event_day_time&function=hourlyCountGroupByDayOfWeek
  // presidio/api/evidences/{indicatorId}/historical-data?feature=abnormal_process_injects_into_windows_process&function=Count
  historicalData: '/presidio/api/evidences/{indicatorId}/historical-data?',

  // Watch User : /presidio/api/user/true/followUsers  -- POST { 'userIds': ['af2284e2-d278-4101-97aa-7a980e781bf0'] }
  followUser: '/presidio/api/user/true/followUsers',
  // UNFOLLOW USER: /presidio/api/user/false/followUsers -- POST { 'userIds':['af2284e2-d278-4101-97aa-7a980e781bf0'] }
  unfollowUser: '/presidio/api/user/false/followUsers',

  // Not a risk: presidio/api/alerts/0bd963d0-a0ae-4601-8497-b0c363becd1f -- Patch Call  { 'status':'Closed', 'feedback':'Rejected','analystUserName':'ca-admin' }
  // {"status":"Open","feedback":"None","analystUserName":"ca-admin"}
  notARisk: '/presidio/api/alerts/{alertId}'
};