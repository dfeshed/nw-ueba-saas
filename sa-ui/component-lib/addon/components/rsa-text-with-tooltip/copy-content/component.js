import Component from '@ember/component';
import CopyToClipboard from '../../../mixins/copy-to-clipboard';
import layout from './template';

export default Component.extend(CopyToClipboard, {
  layout,
  classNames: ['copy-content copy-icon js-copy-trigger'],
  title: 'Copy content',
  attributeBindings: ['title', 'text:data-clipboard-text'],
  text: null
});
