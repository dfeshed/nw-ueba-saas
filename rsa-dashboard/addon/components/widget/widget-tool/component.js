import Component from '@ember/component';
import layout from './template';
import { guidFor } from '@ember/object/internals';

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['widget-tool flexi-fit'],

  showConfig: true,

  panelId: null,

  init() {
    this._super(arguments);
    this.set('panelId', guidFor(this));
  }

});
