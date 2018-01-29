import Component from 'ember-component';
import layout from './template';
import { getScheduleConfig } from 'hosts-scan-configure/actions/data-creators';
import { connect } from 'ember-redux';

const dispatchToActions = {
  getScheduleConfig
};

const Container = Component.extend({
  layout,
  tagName: 'box',
  classNames: 'hosts-scan-configure-container',

  init() {
    this._super(...arguments);
    this.send('getScheduleConfig');
  }
});

export default connect(undefined, dispatchToActions)(Container);
