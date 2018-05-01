import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fetchGroups } from 'admin-source-management/actions/creators/groups-creators';
import layout from './template';

const dispatchToActions = {
  fetchGroups
};

const AdminSourceManagementContainer = Component.extend({
  tagName: 'box',
  classNames: 'admin-source-management-container',
  layout,

  init() {
    this._super(...arguments);
    this.send('fetchGroups');
  }
});

export default connect(undefined, dispatchToActions)(AdminSourceManagementContainer);
