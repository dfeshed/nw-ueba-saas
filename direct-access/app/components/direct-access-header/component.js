import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isDevelopmentBuild } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  deviceInfo: state.deviceInfo,
  isDevelopmentBuild: isDevelopmentBuild(state)
});

const directAccessHeader = Component.extend({
  classNames: ['direct-access-header']
});

export default connect(stateToComputed)(directAccessHeader);
