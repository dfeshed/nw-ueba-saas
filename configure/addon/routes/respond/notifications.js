import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { getNotificationSettings } from 'configure/actions/creators/respond/notification-creators';

export default Route.extend({
  accessControl: inject(),
  contextualHelp: inject(),
  i18n: inject(),
  redux: inject(),

  titleToken() {
    return this.get('i18n').t('configure.respondNotifications');
  },

  model() {
    const redux = this.get('redux');
    redux.dispatch(getNotificationSettings());
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respNotifSetVw'));
  },

  deactivate() {
    this.set('contextualHelp.topic', null);
  }
});
