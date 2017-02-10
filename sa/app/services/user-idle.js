import UserIdleService from 'ember-user-activity/services/user-idle';

export default UserIdleService.extend({
  IDLE_TIMEOUT: localStorage.getItem('rsa-x-idle-session-timeout') || 600000,
  init() {
    this._super(...arguments);

    window.addEventListener('storage', (e) => {
      if (e.key === 'rsa-nw-last-session-access') {
        this.resetTimeout();
      }
    });
  }
});