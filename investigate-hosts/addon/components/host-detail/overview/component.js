import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getPropertyData } from 'investigate-hosts/reducers/details/overview/selectors';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  animation: state.endpoint.detailsInput.animation,
  propertyData: getPropertyData(state),
  hostDetails: state.endpoint.overview.hostDetails || []
});

const HostOverview = Component.extend({

  tagName: 'hbox',

  classNames: ['host-overview'],

  domIsReady: false,

  features: service(),

  didRender() {
    // Delay rendering the property panel
    setTimeout(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.set('domIsReady', true);
      }
    }, 250);
  }
});

export default connect(stateToComputed)(HostOverview);
