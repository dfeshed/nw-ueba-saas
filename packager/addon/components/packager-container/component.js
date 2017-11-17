import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { isEmpty } from 'ember-utils';
import Component from 'ember-component';

import {
  getConfig
} from '../../actions/data-creators';


const stateToComputed = ({ packager }) => ({
  // Download link for the agent packager
  downloadLink: packager.downloadLink,

  // Loading data indicator
  isLoading: packager.loading,

  error: packager.error
});
const dispatchToActions = {
  getConfig
};


const Container = Component.extend({
  layout,
  tagName: 'vbox',
  classNames: 'packager-container rsa-application-layout-panel-content scroll-box',

  // download link for packager
  @computed('downloadLink')
  iframeSrc(link) {
    let source = null;
    if (!isEmpty(link)) {
      const time = Number(new Date());
      source = link.includes('?') ? `${link}&${time}` : `${link}?${time}`;
    }
    return source;
  },

  @computed('error')
  errorMessage() {
    return this.get('i18n').t('packager.error.generic');
  },

  init() {
    this._super(...arguments);
    this.send('getConfig');
  }

});

export default connect(stateToComputed, dispatchToActions)(Container);