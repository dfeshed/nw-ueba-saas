import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';

const { Component } = Ember;

export default Component.extend({
  layout,
  tagName: 'section',
  classNameBindings: [':recon-pager', 'isHidden'],

  /**
   * Total count of list items (across all pages).
   * @type Number
   * @default 0
   * @public
   */
  total: 0,

  /**
   * Maximum number of list items allowed in a single page.
   * @type Number
   * @default 0
   * @public
   */
  pageSize: 0,

  // Resolve to `true` if all items fit within a single page.
  @computed('total', 'pageSize')
  isHidden(total = 0, pageSize = 0) {
    return !total || (total < pageSize);
  }

});
