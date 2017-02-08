import UserIdleService from 'ember-user-activity/services/user-idle';

export default UserIdleService.extend({
  IDLE_TIMEOUT: localStorage.getItem('rsa-x-idle-session-timeout') || 600000,
  resetTimeout() {
    localStorage.setItem('rsa-nw-last-session-access', new Date().getTime());
    this._super(...arguments);
  }
});