import Component from '@ember/component';

export default Component.extend({

  tagName: 'section',

  classNames: ['file-pager'],

  /**
   * Index of event.
   * @type Number
   * @default 0
   * @public
   */
  index: 0,

  /**
   * Total number of all events.
   * @type Number
   * @default 0
   * @public
   */
  total: 0,

  /**
   * tab name of the table
   * @type String
   * @default ''
   * @public
   */
  label: ''
});
