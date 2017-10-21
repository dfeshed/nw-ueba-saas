import Component from 'ember-component';
import CopyToClipboard from './copy-to-clipboard';

export default Component.extend(CopyToClipboard, {
  classNames: ['copy-content copy-icon js-copy-trigger'],
  title: 'Copy content',
  attributeBindings: ['title', 'text:data-clipboard-text'],
  text: null
});
