import Component from '@ember/component';
import { run, next } from '@ember/runloop';
import { isNumeric, getInnerWidth } from 'component-lib/utils/jquery-replacement';
import HasTableParent from '../mixins/has-table-parent';
import layout from './template';

export default Component.extend(HasTableParent, {
  layout,
  classNames: 'rsa-data-table-header-cell-resizer',
  classNameBindings: ['side', 'isDragging'],

  column: null,

  /**
   * Indicates which edge of the cell this component will resize; either 'LEFT' or 'RIGHT'.
   * @type {string}
   * @public
   */
  side: 'left',

  /**
   * This will be set to `true` by the component while it is being dragged. Useful for styling.
   * @type {boolean}
   * @public
   */
  isDragging: false,

  /**
   * Configurable action to be invoked when user attempts to resize a column via drag-drop.
   * If not given, this component will abort the resize and exit silently.
   * @param {object} column The column model.
   * @param {number} width The column width in pixels.
   * @type {function}
   * @public
   */
  resizeAction: null,

  /**
   * Configurable minimum limit that cell can be resized to.
   * @type {number}
   * @public
   */
  minWidth: 3,

  mouseDown(e) {
    if (!e) {
      return;
    }
    this._dragStartPos = [ e.pageX, e.pageY ];
    this._measureMe();

    document.body.addEventListener('mousemove', this._mousemoveCallback);
    document.body.addEventListener('mouseup', this._mouseupCallback);

    this.set('isDragging', true);
    return false; // disables native browser text selection/highlighting when dragging
  },

  _dragMove(e) {
    if (!e) {
      return;
    }
    const [ x0, y0 ] = this._dragStartPos;
    this._dragDelta = [ e.pageX - x0, e.pageY - y0 ];
    this._resizeMe();
  },

  _dragEnd() {
    this.set('isDragging', false);

    next(() => {
      const fn = this.get('columnsConfigDidChange');
      if (typeof fn === 'function') {
        fn('COLUMN_WIDTH');
      }
    });

    document.body.removeEventListener('mousemove', this._mousemoveCallback);
    document.body.removeEventListener('mouseup', this._mouseupCallback);
  },

  /**
   * Called on dragstart. Caches the model & width of column being resized, in number of pixels, at the start of the drag.
   * Used later to compute the new width of the cell after a drag move has happened.  We don't really care what exactly
   * is the current width, we just need a base pixel measurement to which we can apply a pixel delta when the user resizes.
   * If we can read that base from `column.width`, great. But if `column.width` is not in pixels, then it is useless
   * to use (e.g., we can't apply a delta of 10px to a width of 50%); so in that case, we must measure the width,
   * and if we assume `box-sizing` is `content-box` then we should measure innerWidth rather than outerWidth.
   * @private
   */
  _measureMe() {
    const { column, side } = this.getProperties('column', 'side');
    let target = column;

    if (side === 'left') {
      const columns = this.get('table.visibleColumns');
      const index = columns.indexOf(column);

      if (index > 0) {
        target = columns[index - 1];
      }
    }
    this._resizeColumn = target;

    if (target) {
      const { element } = this;
      let w = target.get('width');

      // If the `width` is a numeric with either no units (e.g., '20') or `px` units (e.g., '20px'), coerce it into integer pixels.
      // Otherwise, don't bother converting to pixels, just measure the current (inner) width, and use that as our basis.
      if (isNumeric(w) || ((typeof w === 'string') && w.match(/px$/))) {
        w = parseInt(w, 10);
      } else {
        // In case of % elem is returing resizer not the actual column. Due to that initial width is always 9px.
        const elem = element.parentElement.parentElement;
        w = parseInt(getInnerWidth(elem), 10);
      }
      this._initialWidth = w;
    }
  },

  /**
   * Called on dragmove. Triggers the `resizeAction` callback, which is responsible for updating the cell width.
   * The new width is computed as the initial width (at the dragstart) plus a delta, which is the number of pixels
   * that the mouse has moved horizontally since the dragstart occurred.  Min & max limits are then applied.
   * The actual updating of the cell width doesn't happen here; instead, the `resizeAction` should accomplish that
   * (for example, typically this cell will have a `column` model, which is used for binding the cell's HTML width;
   * then `resizeAction` should update that model, which will refresh the binding).
   * @private
   */
  _resizeMe() {
    const target = this._resizeColumn;
    const initial = this._initialWidth;

    if (target && !isNaN(initial)) {
      let width = initial + this._dragDelta[0];
      width = Math.max(this.get('minWidth'), width);
      const fn = this.get('resizeAction');
      if (typeof fn === 'function') {
        fn(target, width);
      }
    }
  },

  didInsertElement() {
    this._super(...arguments);
    this._mousemoveCallback = run.bind(this, this._dragMove);
    this._mouseupCallback = run.bind(this, this._dragEnd);
  },

  willDestroyElement() {
    this._mousemoveCallback = this._mouseupCallback = null;
  }
});
