import Component from '@ember/component';
import { connect } from 'ember-redux';
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

  classNames: ['host-content']
});

export default connect(stateToComputed)(Summary);
