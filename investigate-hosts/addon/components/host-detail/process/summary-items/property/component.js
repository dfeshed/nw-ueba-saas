import Component from 'ember-component';
import computed from 'ember-computed-decorators';

/**
 * Display key and value and also shows the tool tip if text content are not visible fully
 * @public
 */
export default Component.extend({

  tagName: 'box',

  classNames: ['header-item'],

  item: null,

  /** *
   * Extra CSS class name for the component
   * @type string valid css class name
   * @public
   */
  classNameBindings: ['cssClass'],

  @computed('item.cssClass')
  cssClass: (cssClass) => cssClass || 'col-xs-4 col-md-3',

  init() {
    this._super(...arguments);
    if (!this.cssClass) {
      this.set('cssClass', 'col-xs-4 col-md-3');
    }
  }
});
