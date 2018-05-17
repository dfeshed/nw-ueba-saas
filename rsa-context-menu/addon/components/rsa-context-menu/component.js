import Component from '@ember/component';
import contextMenuMixin from 'ember-context-menu';

export default Component.extend(contextMenuMixin, {
  tagName: 'span',
  classNames: ['content-context-menu']
});
