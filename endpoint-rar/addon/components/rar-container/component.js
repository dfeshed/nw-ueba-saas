import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { rarInstallerURL } from '../../reducers/selectors';
import { setServerId, getRARConfig, getRarStatus } from '../../actions/data-creators';
import { failure } from 'investigate-shared/utils/flash-messages';
import { next } from '@ember/runloop';
import helpText from './helpText';
import { inject as service } from '@ember/service';

const onFailure = {
  onFailure(message) {
    failure(`endpointRAR.rarConfig.${message}`);
  }
};
const callback = (self) => ({
  onSuccess() {
    self.send('getRARConfig', onFailure);
  },
  ...onFailure
});

const stateToComputed = (state) => ({
  iframeSrc: rarInstallerURL(state)
});

const dispatchToActions = {
  setServerId,
  getRARConfig,
  getRarStatus
};

const RARContainer = Component.extend({
  layout,
  tagName: 'box',
  classNames: ['rar-container'],
  serverId: null,
  helpText,
  accessControl: service(),
  enableRarPage: false,

  didReceiveAttrs() {
    this._super(...arguments);
    const serverId = this.get('serverId');
    this.send('setServerId', serverId);
  },

  init() {
    this._super(...arguments);
    next(() => {
      if (this.get('accessControl.hasEndpointRarReadPermission')) {
        this.set('enableRarPage', true);
        if (!this.get('isDestroyed') && !this.get('isDestroying')) {
          this.send('getRarStatus', callback(this));
        }
      }
    });
  }
});

export default connect(stateToComputed, dispatchToActions)(RARContainer);