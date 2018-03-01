import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  getLoggedInUsers,
  isMachineLinux,
  isMachineWindows
} from 'investigate-hosts/reducers/details/overview/selectors';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';

const stateToComputed = (state) => ({
  loggedInUsers: getLoggedInUsers(state),
  isMachineLinux: isMachineLinux(state),
  isMachineWindows: isMachineWindows(state),
  serviceList: serviceList(state)
});

const LoggedInUsers = Component.extend({

  tagName: 'vbox',

  classNames: 'col-xs-12'
});

export default connect(stateToComputed)(LoggedInUsers);
