import Component from '@ember/component';
import { connect } from 'ember-redux';
import { ieEdgeDetection } from 'component-lib/utils/browser-detection';
import {
  machineOsType,
  networkInterfacesCount,
  loggedInUsersCount,
  isMachineLinux
} from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  loggedInUsersCount: loggedInUsersCount(state),
  networkInterfacesCount: networkInterfacesCount(state),
  isMachineLinux: isMachineLinux(state)
});

const Summary = Component.extend({

  tagName: 'vbox',

  classNames: ['host-content'],

  animate: !ieEdgeDetection()

});

export default connect(stateToComputed)(Summary);
