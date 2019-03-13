import Component from '@ember/component';
import { scheduleOnce } from '@ember/runloop';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import {
  updatePolicyProperty
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  channels,
  channelConfig,
  channelFiltersValidator
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';

const stateToComputed = (state) => ({
  channelFilters: channels(state),
  channelFiltersValidator: channelFiltersValidator(state),
  columns: channelConfig()
});

const dispatchToActions = {
  updatePolicyProperty
};

const WindowsLogChannelFilters = Component.extend({
  tagName: 'box',
  classNames: 'windows-log-channel-filters',

  @computed()
  panelId() {
    return `winLogCollectionTooltip-${this.get('elementId')}`;
  },
  @computed('channelFilters')
  channels(channelFilters) {
    return _.cloneDeep(channelFilters);
  },

  _updateChannelFilters() {
    this.send('updatePolicyProperty', 'channelFilters', this.get('channels'));
  },

  _scrollToAddChannelButton() {
    this.get('element').querySelector('.add-channel-button').scrollIntoView(false);
  },

  actions: {
    // adding a row to the channel filters table
    addRowFilter() {
      this.get('channels').pushObject({ channel: '', filterType: 'INCLUDE', eventId: 'ALL' });
      this._updateChannelFilters();
      scheduleOnce('afterRender', this, '_scrollToAddChannelButton');
    },
    // pass the index of the row to delete the row in the channel filters
    deleteRow(index) {
      this.get('channels').removeAt(index);
      this._updateChannelFilters();
    },
    // when the child component `body cell` modifies channels, this gets called
    channelUpdated() {
      this._updateChannelFilters();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(WindowsLogChannelFilters);