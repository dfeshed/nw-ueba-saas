import classic from 'ember-classic-decorator';
import { classNames, tagName, layout as templateLayout } from '@ember-decorators/component';
import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { rarInstallerURL } from '../../reducers/selectors';
import { setServerId, getRARConfig, getRarStatus } from '../../actions/data-creators';
import { failure } from 'investigate-shared/utils/flash-messages';
import { next } from '@ember/runloop';
import helpText from './helpText';
import { inject as service } from '@ember/service';

const onFailure = {
  onFailure(message) {
    failure(`endpointRAR.rarConfig.${message}`);
  }
};
const callback = (self) => ({
  onSuccess() {
    self.send('getRARConfig', onFailure);
  },
  ...onFailure
});

const stateToComputed = (state) => ({
  iframeSrc: rarInstallerURL(state)
});

const dispatchToActions = {
  setServerId,
  getRARConfig,
  getRarStatus
};

@classic
@templateLayout(layout)
@tagName('box')
@classNames('rar-container')
class RARContainer extends Component {

  @service
  accessControl;

  serverId = null;
  helpText = helpText;
  enableRarPage = false;

  didReceiveAttrs() {
    super.didReceiveAttrs(...arguments);
    const serverId = this.get('serverId');
    this.send('setServerId', serverId);
  }

  init() {
    super.init(...arguments);
    next(() => {
      if (this.get('accessControl.hasEndpointRarReadPermission')) {
        this.set('enableRarPage', true);
        if (!this.get('isDestroyed') && !this.get('isDestroying')) {
          this.send('getRarStatus', callback(this));
        }
      }
    });
  }
}

export default connect(stateToComputed, dispatchToActions)(RARContainer);