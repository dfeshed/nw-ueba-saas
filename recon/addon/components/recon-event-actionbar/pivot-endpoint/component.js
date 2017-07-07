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

  attributeBindings: ['href', 'target', 'title'],
  classNames: ['pivot-to-endpoint'],
  tagName: 'a',
  target: '_blank',

  i18n: service(),

  @computed('nweCallbackId')
  href: (callbackId) => `ecatui://${callbackId}`,

  @computed
  title() {
    return this.get('i18n').t('recon.textView.pivotToEndpointTitle');
  }
});

export default connect(stateToComputed)(PivotToEndpoint);