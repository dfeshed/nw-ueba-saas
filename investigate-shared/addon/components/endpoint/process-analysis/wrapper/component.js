import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['process-analysis-wrapper', 'scrollable-panel-wrapper', 'col-xs-12']
});
