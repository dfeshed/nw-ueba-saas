import Component from '@ember/component';
import { connect } from 'ember-redux';
import { activeTabName } from 'investigate-users/reducers/tabs/selectors';

const stateToComputed = (state) => ({
  activeTabName: activeTabName(state)
});

const BodyComponent = Component.extend({
  classNames: 'user-body'
});

export default connect(stateToComputed)(BodyComponent);