import Component from 'ember-component';
import { connect } from 'ember-redux';
import { getLoggedInUsers, isMachineLinux } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  loggedInUsers: getLoggedInUsers(state),
  isMachineLinux: isMachineLinux(state)
});

const LoggedInUsers = Component.extend({

  tagName: 'vbox',

  classNames: 'col-xs-12'
});

export default connect(stateToComputed)(LoggedInUsers);