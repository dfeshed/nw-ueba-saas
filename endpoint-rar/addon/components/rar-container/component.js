import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { rarInstallerURL } from '../../reducers/selectors';
import { setServerId } from '../../actions/data-creators';

const stateToComputed = (state) => ({
  iframeSrc: rarInstallerURL(state)
});

const dispatchToActions = {
  setServerId
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

  actions: {
    backToServiceList() {
      window.location.href = `${window.location.origin}/admin/services/`;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(RARContainer);