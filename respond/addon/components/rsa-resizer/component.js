import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import DragBehavior from 'respond/utils/behaviors/drag';
import safeCallback from 'component-lib/utils/safe-callback';

export default Component.extend({
  layout,
  classNames: ['rsa-resizer'],
  classNameBindings: ['isResizing'],

  /**
   * Configurable callback to be invoked as this component is dragmoved.
   * @param {Number} x The `x` value of this component plus the difference in pixels that the mouse has moved since
   * the drag began.
   * @param {Number} y The `y` value of this component plus the difference in pixels that the mouse has moved since
   * the drag began.
   * @public
   */
  resizeAction: null,

  /**
   * Configurable value to serve as the basis for a horizontal drag computation.
   * This number is not really used by this component, other than to compute a value to be passed back to the
   * configurable `resizeAction` callback when it is invoked.  Typically, this value will be set by the consumer to
   * some width, which enables `resizeAction` to compute a new width based on the distance of the user's drag.
   * @type {Number}
   * @readonly
   * @public
   */
  x: 0,

  /**
   * Configurable value to serve as the basis for a vertical drag computation.
   * This number is not really used by this component, other than to compute a value to be passed back to the
   * configurable `resizeAction` callback when it is invoked.  Typically, this value will be set by the consumer to
   * some height, which enables `resizeAction` to compute a new height based on the distance of the user's drag.
   * @type {Number}
   * @readonly
   * @public
   */
  y: 0,

  /**
   * Indicates whether or not this component is currently being dragged.
   * @type {Boolean}
   * @readonly
   * @public
   */
  isResizing: false,

  /**
   * Creates a DragBehavior instance and attaches it to this component's mouseDown.
   * @private
   */
  @computed()
  dragBehavior() {
    const dragstart = () => {
      this.setProperties({
        isResizing: true,
        x0: this.get('x'),
        y0: this.get('y')
      });
    };
    const dragmove = (e, ctxt) => {
      const [ deltaX, deltaY ] = ctxt.get('delta') || [];
      safeCallback(this.get('resizeAction'), this.get('x0') + deltaX, this.get('y0') + deltaY);
    };
    const dragend = () => {
      this.set('isResizing', false);
    };
    return DragBehavior.create({
      minMouseMoves: 0,
      callbacks: {
        dragstart,
        dragmove,
        dragend
      }
    });
  },

  // Notifies `dragBehavior` that a drag may be starting.
  mouseDown(e) {
    const behavior = this.get('dragBehavior');
    behavior.mouseDidDown(e);
    e.preventDefault();
    return false;
  }
});
