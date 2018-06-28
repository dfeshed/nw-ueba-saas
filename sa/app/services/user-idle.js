import _ from 'lodash';
import getOwner from 'ember-owner/get';
import UserIdleService from 'ember-user-activity/services/user-idle';

let mousemove;

export default UserIdleService.extend({
  IDLE_TIMEOUT: localStorage.getItem('rsa-x-idle-session-timeout') || 600000,
  init() {
    this._super(...arguments);

    const configuration = getOwner(this).resolveRegistration('config:environment');
    const debounceDelay = configuration.APP.debounceDelay || 1200;

    mousemove = _.debounce(() => {
      if (this.isDestroyed || this.isDestroying) {
        return;
      }
      this.resetTimeout();
      localStorage.setItem('rsa-nw-last-session-access', new Date().getTime());
    }, debounceDelay);

    window.addEventListener('mousemove', mousemove);
  },
  willDestroy() {
    window.removeEventListener('mousemove', mousemove);
    this._super(...arguments);
  }
});
