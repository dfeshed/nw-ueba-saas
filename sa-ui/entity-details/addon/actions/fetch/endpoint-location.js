export default {
  // Entity details Url
  userDetails: '/api/entity/{entityId}/details',

  //  For getting alerts for entity '/api/alerts?entity_id={entityId}&load_comments=true
  entityAlerts: '/api/alerts?',

  // IndicatorEvents: /api/evidences/{indicatorId}/events?page=1&size=100&sort_direction=DESC
  indicatorEvents: '/api/evidences/{indicatorId}/events?',

  // Histogram:
  // presidio/api/evidences/{indicatorId}/historical-data?feature=high_number_of_successful_object_change_operations&function=distinctEventsByTime
  // presidio/api/evidences/{indicatorId}/historical-data?feature=abnormal_event_day_time&function=hourlyCountGroupByDayOfWeek
  // presidio/api/evidences/{indicatorId}/historical-data?feature=abnormal_process_injects_into_windows_process&function=Count
  historicalData: '/api/evidences/{indicatorId}/historical-data?',

  // Watch User : /api/entity/true/followUsers  -- POST { 'userIds': ['af2284e2-d278-4101-97aa-7a980e781bf0'] }
  followUser: '/api/entity/true/followUsers',
  // UNFOLLOW USER: /api/entity/false/followUsers -- POST { 'userIds':['af2284e2-d278-4101-97aa-7a980e781bf0'] }
  unfollowUser: '/api/entity/false/followUsers',

  // Not a risk: presidio/api/alerts/0bd963d0-a0ae-4601-8497-b0c363becd1f -- Patch Call  { 'status':'Closed', 'feedback':'Rejected','analystUserName':'ca-admin' }
  // {"status":"Open","feedback":"None","analystUserName":"ca-admin"}
  notARisk: '/api/alerts/{alertId}'
};