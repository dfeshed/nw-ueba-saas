import classic from 'ember-classic-decorator';
import { classNames, tagName, layout as templateLayout } from '@ember-decorators/component';
import { computed } from '@ember/object';
import layout from './template';
import { connect } from 'ember-redux';
import { isEmpty } from '@ember/utils';
import Component from '@ember/component';
import { next } from '@ember/runloop';
import helpText from './helpText';

import { getEndpointServerList } from '../../actions/data-creators';


const stateToComputed = ({ packager }) => ({
  // Download link for the agent packager
  downloadLink: packager.downloadLink,

  // Loading data indicator
  isLoading: packager.loading,

  error: packager.error
});
const dispatchToActions = {
  getEndpointServerList
};


@classic
@templateLayout(layout)
@tagName('box')
@classNames(
  'packager-container',
  'rsa-application-layout-panel-content',
  'input-content'
)
class Container extends Component {
  serverId = null;
  helpText = helpText;

  // download link for packager
  @computed('downloadLink')
  get iframeSrc() {
    let source = null;
    if (!isEmpty(this.downloadLink)) {
      const time = Number(new Date());
      source = this.downloadLink.includes('?') ? `${this.downloadLink}&${time}` : `${this.downloadLink}?${time}`;
    }
    return source;
  }

  @computed('error')
  get errorMessage() {
    return this.get('i18n').t('packager.error.generic');
  }

  init() {
    super.init(...arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getEndpointServerList', this.get('serverId'));
      }
    });
  }
}

export default connect(stateToComputed, dispatchToActions)(Container);
