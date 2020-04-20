import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import computed, { not } from 'ember-computed-decorators';

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

  @not('nweCallbackId')
  isDisabled: false,

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
