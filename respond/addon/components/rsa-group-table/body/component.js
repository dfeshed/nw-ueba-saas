import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import HasSizeAttr from 'respond/mixins/dom/has-size-attr';
import HasScrollAttr from 'respond/mixins/dom/has-scroll-attr';
import { htmlSafe } from 'ember-string';
import $ from 'jquery';

/**
 * @class Group Table Body Component
 * Represents the "body" section of a group table, i.e. the scrollable below the (optional) column headers.
 * The "body" includes all the data rows -- rows for the group headers and rows for the the items within the groups.
 * @public
 */
export default Component.extend(HasSizeAttr, HasScrollAttr, {
  tagName: 'section',
  layout,
  classNames: ['rsa-group-table-body'],

  /**
   * Configurable name of the Ember Component to be used for rendering the table's header rows.
   * Each instance of this component will automatically receive `group`, `index`, `table` & `top` attrs at run-time.
   * @type {String}
   * @default 'rsa-group-table/group'
   * @public
   */
  groupComponentClass: 'rsa-group-table/group',

  /**
   * Configurable name of the Ember Component to be used for rendering the sticky group header at the top of viewport.
   * Typically set to the sample component class used for the groupComponentClass' child "header" component.
   * This component will automatically receive a `group`, `index` & `table` attrs at run-time.
   * @type {String}
   * @default 'rsa-group-table/group-header'
   * @public
   */
  stickyHeaderComponentClass: 'rsa-group-table/group-header',

  // Selector for the DOM node whose size is to be broadcast.
  // @see respond/mixins/dom/has-size-attr
  // @workaround We want the INNER size of the scroll element, not including scrollbar thickness, but if we measure
  // clientWidth of scroll-element, it includes the scrollbar for some odd reason. To workaround, we put instead
  // measure a div inside the scroll-element whose width & height are 100%, meaning they fill the scroll-element
  // except for its scrollbar area.
  sizeSelector: '.js-size-element',

  // Target attr prefix for size properties.
  // @see respond/mixins/dom/has-size-attr
  sizeAttr: 'table.scrollerSize',

  // Selector for the DOM node whose scroll position is to be broadcast.
  // @see respond/mixins/dom/has-scroll-attrs
  scrollSelector: '.js-scroll-element',

  // Target attr prefix for scroll properties.
  // @see respond/mixins/dom/has-scroll-attr
  scrollAttr: 'table.scrollerPos',

  /**
   * Escaped CSS style string to be applied to the groups' container DOM node.
   * @type {String}
   * @private
   */
  @computed('table.{totalColumnsWidth,totalRowsHeight}')
  placeholderStyle(width, height) {
    return htmlSafe(`width: ${width}; height: ${height};`);
  },

  @computed('table.scrollerSize.innerWidth')
  stickyHeaderContainerStyle(width) {
    const styleText = $.isNumeric(width) ? `width:${width}px;` : '';
    return htmlSafe(`${styleText}`);
  },

  @computed('table.{totalColumnsWidth,scrollerPos.left}')
  stickyHeaderScrollerStyle(width, scrollLeft) {
    const px = $.isNumeric(scrollLeft) ? scrollLeft : 0;
    return htmlSafe(`width:${width}; left: -${px}px`);
  },

  // Returns the first group (if any) that has items; otherwise the first group, if any.
  // This group will be rendered invisibly and used to measure the DOM.
  @computed('table.groups.@each.items')
  sampleGroup(groups) {
    groups = groups || [];
    const found = groups.findBy('items.length');
    return found || groups[0];
  }
});
