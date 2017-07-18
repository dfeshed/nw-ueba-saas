import Component from 'ember-component';
import service from 'ember-service/inject';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';

import layout from './template';
import { nweCallbackId } from 'recon/reducers/meta/selectors';

const stateToComputed = ({ recon }) => ({
  nweCallbackId: nweCallbackId(recon)
});

const PivotToEndpoint = Component.extend({
  layout,

  attributeBindings: ['title'],
  classNames: ['pivot-to-endpoint'],
  tagName: 'div',

  i18n: service(),

  @computed('nweCallbackId')
  href: (callbackId) => `ecatui://${callbackId}`,

  @computed
  title() {
    return this.get('i18n').t('recon.textView.pivotToEndpointTitle');
  },

  actions: {
    goToEcat() {
      window.open(this.get('href'), '_blank').focus();
    }
  }
});

export default connect(stateToComputed)(PivotToEndpoint);