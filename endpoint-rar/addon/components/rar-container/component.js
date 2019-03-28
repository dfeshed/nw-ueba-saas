import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { rarInstallerURL } from '../../reducers/selectors';
import { setServerId, getRARConfig } from '../../actions/data-creators';
import { failure } from 'investigate-shared/utils/flash-messages';
import { next } from '@ember/runloop';

const callback = {
  onFailure(message) {
    failure(message, null, false);
  }
};

const stateToComputed = (state) => ({
  iframeSrc: rarInstallerURL(state)
});

const dispatchToActions = {
  setServerId,
  getRARConfig
};

const RARContainer = Component.extend({
  layout,
  tagName: 'box',
  classNames: ['rar-container'],
  serverId: null,

  didReceiveAttrs() {
    this._super(...arguments);
    const serverId = this.get('serverId');
    this.send('setServerId', serverId);
  },

  init() {
    this._super(...arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getRARConfig', callback);
      }
    });
  },

  actions: {
    backToServiceList() {
      window.location.href = `${window.location.origin}/admin/services/`;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(RARContainer);