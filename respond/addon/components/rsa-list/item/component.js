import Component from '@ember/component';
import { alias } from 'ember-computed-decorators';
import Clickable from 'respond/mixins/dom/clickable';

export default Component.extend(Clickable, {
  classNames: ['rsa-list-item'],
  classNameBindings: ['isSelected', 'isInSelectMode'],
  item: null,
  isSelected: false,

  // Specifies the data to be submitted to click event handlers.
  // @see respond/mixins/dom/clickable
  @alias('item')
  clickData: null
});
