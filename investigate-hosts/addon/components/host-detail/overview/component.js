import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getPropertyData } from 'investigate-hosts/reducers/details/overview/selectors';

import {
  setAlertTab
} from 'investigate-hosts/actions/data-creators/details';

const dispatchToActions = {
  setAlertTab
};

const stateToComputed = (state) => ({
  animation: state.endpoint.detailsInput.animation,
  propertyData: getPropertyData(state),
  hostDetails: state.endpoint.overview.hostDetails || [],
  activeAlertTab: state.endpoint.overview.activeAlertTab
});

const HostOverview = Component.extend({

  tagName: 'box',

  classNames: ['host-overview'],

  domIsReady: false,

  didRender() {
    // Delay rendering the property panel
    setTimeout(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.set('domIsReady', true);
      }
    }, 250);
  }
});

export default connect(stateToComputed, dispatchToActions)(HostOverview);
