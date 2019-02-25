import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { isDevelopmentBuild } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  deviceInfo: state.deviceInfo,
  wsConnected: state.wsConnected,
  username: state.username,
  isDevelopmentBuild: isDevelopmentBuild(state)
});

const ngcoreuiHeader = Component.extend({
  classNames: ['ngcoreui-header'],

  @computed('wsConnected')
  connectionString: (wsConnected) => {
    return wsConnected ? 'Online' : 'Disconnected';
  }
});

export default connect(stateToComputed)(ngcoreuiHeader);
