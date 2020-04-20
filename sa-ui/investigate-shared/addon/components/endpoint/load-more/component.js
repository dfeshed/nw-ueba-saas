import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import {
  isBrokerView
} from 'investigate-shared/selectors/broker-load-more/selectors';

import layout from './template';

export default Component.extend({
  layout,

  classNames: 'endpoint-load-more',

  status: undefined,

  count: undefined,

  servers: undefined,

  serverId: undefined,

  getMoreData: undefined,

  title: undefined,

  didReceiveAttrs() {
    this._super(...arguments);
    const state = {
      serverId: this.get('serverId'),
      servers: this.get('servers')
    };
    this.setProperties({
      isBrokerView: isBrokerView(state)
    });
  },

  @computed('isBrokerView', 'status', 'count')
  showMessage(isBrokerView, status, count) {
    return isBrokerView && status === 'completed' && count >= 1000;
  }
});
