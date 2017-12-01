import Component from 'ember-component';
import { connect } from 'ember-redux';
import { getNetworkInterfaces } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  networkInterfaces: getNetworkInterfaces(state)
});

const IpAddresses = Component.extend({

  tagName: 'div',

  classNames: 'col-xs-12'
});

export default connect(stateToComputed)(IpAddresses);