import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { isDevelopmentBuild } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  // TODO selectors?
  deviceInfo: state.shared.deviceInfo,
  wsConnected: state.shared.wsConnected,
  username: state.shared.username,
  availablePermissions: state.shared.availablePermissions,
  isDevelopmentBuild: isDevelopmentBuild(state)
});

const ngcoreuiHeader = Component.extend({
  classNames: ['ngcoreui-header'],

  @computed('wsConnected')
  connectionString: (wsConnected) => {
    return wsConnected ? 'Online' : 'Disconnected';
  },

  @computed('availablePermissions')
  permissionsString: (availablePermissions) => {
    return availablePermissions ? `Available permissions:\n${availablePermissions.join('\n')}` : 'Permissions not loaded yet';
  }
});

export default connect(stateToComputed)(ngcoreuiHeader);
