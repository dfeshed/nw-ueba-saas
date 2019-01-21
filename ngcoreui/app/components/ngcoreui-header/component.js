import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isDevelopmentBuild } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  deviceInfo: state.deviceInfo,
  isDevelopmentBuild: isDevelopmentBuild(state)
});

const ngcoreuiHeader = Component.extend({
  classNames: ['ngcoreui-header']
});

export default connect(stateToComputed)(ngcoreuiHeader);
