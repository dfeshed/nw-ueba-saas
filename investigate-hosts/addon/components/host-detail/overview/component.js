import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = ({ endpoint }) => ({
  host: endpoint.overview.hostDetails,
  animation: endpoint.detailsInput.animation
});

const HostOverview = Component.extend({

  tagName: 'hbox',

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

export default connect(stateToComputed)(HostOverview);
