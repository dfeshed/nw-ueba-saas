/**
 * @file user-name
 * @description when the argument has an id equals to -1, it returns '(Unassigneed)' using i18n service.
 * Otherwise it returns the user's name
 * @public
 */
import Ember from 'ember';

const {
  Helper,
  inject: {
    service
  },
  get
} = Ember;

export default Helper.extend({
  i18n: service('i18n'),

  compute([user]) {
    return user && get(user, 'id') !== -1 ?
      user.name :
      this.get('i18n').t('incident.assignee.none');
  }
});