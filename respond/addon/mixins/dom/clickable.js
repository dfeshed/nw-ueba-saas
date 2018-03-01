import Mixin from '@ember/object/mixin';

export default Mixin.create({
  /**
   * Specifies how this component's items should respond to clicks.
   *
   * If `isInSelectMode` is `false`, items will follow standard selection behavior when clicked.  Specifically, a clicked
   * item will become the one & only selected item in the list.  The previous selection (if any) is cleared.  Users can
   * select multiple items by using Shift+Click or Ctrl+Click.
   *
   * If `isInSelectMode` is `true`, items will follow checkbox-like selection behavior when clicked. Specifically, a
   * clicked item will become selected and remain selected even after other items are subsequently clicked.  The selected
   * item can be un-selected by clicking on it again.  This is essentially the same behavior that happens with Ctrl+Clicks
   * when `isInSelectMode` is `false`. The difference is that when `isInSelectMode` is `true`, the user doesn't need to
   * press the Ctrl key; simple clicking will suffice.
   *
   * The purpose of this feature is to support list UIs which expose an "Edit" or "Select" mode to the user. It enables
   * the user to select multiple list items with simple clicking and then typically choose an action to apply on
   * all the selected items.
   *
   * @type {boolean}
   * @default false
   * @public
   */
  isInSelectMode: false,

  /**
   * Configurable action to be invoked when user does a simple click.
   *
   * The action will receive 2 arguments:
   * `clickData`: {object} the value of this object's `clickData` attr (which is configurable by the consumer of this mixin);
   * `e`: {object} the DOM click event object.
   *
   * @type {function}
   * @public
   */
  clickAction: null,

  /**
   * Configurable action to be invoked when user does a Shift+click.
   *
   * The action will receive 2 arguments:
   * `clickData`: {object} the value of this object's `clickData` attr (which is configurable by the consumer of this mixin);
   * `e`: {object} the DOM click event object.
   *
   * @type {function}
   * @public
   */
  shiftClickAction: null,

  /**
   * Configurable action to be invoked when user does either a Ctrl+click, Alt+click or Cmd+click.
   *
   * The action will receive 2 arguments:
   * `clickData`: {object} the value of this object's `clickData` attr (which is configurable by the consumer of this mixin);
   * `e`: {object} the DOM click event object.
   *
   * @type {function}
   * @public
   */
  ctrlClickAction: null,

  /**
   * Responds to clicks by invoking a configurable action.  The action should be specified as a property of
   * this component.
   *
   * This component supports different actions depending on which modifier keys were detected:
   * "shiftClickAction": function to be invoked if SHIFT key was detected;
   * "ctrlClickAction": function to be invoked if either CTRL or ALT or CMD key was detected and SHIFT key was not detected;
   * "clickAction": function to be invoked if either:
   * (a) CTRL + SHIFT keys were not detected, or
   * (b) SHIFT or CLICK key was detected but the corresponding action for them is not defined.
   *
   * The invoked action will receive the data item that was targeted (or null, if none), plus a hash indicating which modifier
   * keys were pressed during the event (e.g., {shiftKey: true, ctrlKey: true}.
   *
   * Note: Typically in Ember, we invoke actions on click by simply using the `{{action}}` helper in a template. However,
   * the helper doesn't pass in any modifier-key state into the action.  Therefore, in order to support modifier key
   * detection here, we must implement this logic in the component's `click()` handler.
   *
   * @param {Object} e The click event object.
   * @public
   */
  click(e = {}) {
    // Returns the requested action, whether it is stored as a property or a true Ember action
    const getAction = (whichAction) => this.get(whichAction) || this.actions[whichAction];

    // Detect modifier keys and determine which action to take.
    const { shiftKey, ctrlKey, altKey, metaKey } = e;
    let whichAction;
    if (this.get('isInSelectMode')) {
      whichAction = 'ctrlClickAction';
    } else if (shiftKey) {
      whichAction = 'shiftClickAction';
    } else if (ctrlKey || altKey || metaKey) {
      whichAction = 'ctrlClickAction';
    } else {
      whichAction = 'clickAction';
    }
    const action = getAction(whichAction) || getAction('clickAction');
    const data = this.get('clickData');

    if (action && data) {
      action(data, e);
    }

    return true;
  }
});
