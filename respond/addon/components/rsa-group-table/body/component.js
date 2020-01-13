import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import HasSizeAttr from 'respond/mixins/dom/has-size-attr';
import HasScrollAttr from 'respond/mixins/dom/has-scroll-attr';
import { htmlSafe } from '@ember/string';
import { isNumeric } from 'component-lib/utils/jquery-replacement';

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
   * A stub for an action passed down from the controller, to close any open UEBA/RECON page, by transitioning back
   * to the parent incident/<incident-id> route.
   * */
  closeOverlay: null,

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
  placeholderStyle: computed('table.totalColumnsWidth', 'table.totalRowsHeight', function() {
    return htmlSafe(`width: ${this.table?.totalColumnsWidth}; height: ${this.table?.totalRowsHeight};`);
  }),

  stickyHeaderContainerStyle: computed('table.scrollerSize.innerWidth', function() {
    const styleText = isNumeric(this.table?.scrollerSize?.innerWidth) ? `width:${this.table?.scrollerSize?.innerWidth}px;` : '';
    return htmlSafe(`${styleText}`);
  }),

  stickyHeaderScrollerStyle: computed('table.totalColumnsWidth', 'table.scrollerPos.left', function() {
    const px = isNumeric(this.table?.scrollerPos?.left) ? this.table?.scrollerPos?.left : 0;
    return htmlSafe(`width:${this.table?.totalColumnsWidth}; left: -${px}px`);
  }),

  // Returns the first group (if any) that has items; otherwise the first group, if any.
  // This group will be rendered invisibly and used to measure the DOM.
  sampleGroup: computed('table.groups.@each.items', function() {
    const groups = this.table?.groups || [];
    const found = groups.findBy('items.length');
    return found || groups[0];
  }),

  /**
   * A click on a non-link storypoint or event cell closes any open open recon/ueba overlay.
   */
  click() {
    if (this.get('closeOverlay')) {
      this.get('closeOverlay')();
    }
  }
});
