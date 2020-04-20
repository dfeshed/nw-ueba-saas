import Component from '@ember/component';
import { connect } from 'ember-redux';
import { initiateUser } from 'investigate-users/actions/user-details';

const dispatchToActions = {
  initiateUser
};

const UserTabAlertComponent = Component.extend({
});

export default connect(null, dispatchToActions)(UserTabAlertComponent);