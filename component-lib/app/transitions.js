/**
 * @file Liquid Fire Transitions Map for declarative animations.
 * Defines transitions which can then be leveraged by components/routes/etc.
 * @see http://ember-animation.github.io/liquid-fire/#/transition-map
 * @public
 */
import Ember from 'ember';

const { get, isArray } = Ember;

// Helper to retrive an appropriate "index" for a given value.  If given
// an object that has a number `index` attr, use that if defined. Otherwise,
// if it's an array, use the array's length.  Otherwise just return the given
// argument or zero.
function _getIndex(value) {
  if (value && typeof value === 'object') {
    let index = get(value, 'index');
    if (!isNaN(index)) {
      return index;
    }
  }
  if (isArray(value)) {
    return value.length;
  }
  return value || 0;
}

export default function() {
  /*
   * For sliding DOM horizontally on-/off-screen:
   * Use a `{{liquid-bind}}` component with a `liquid-slide-horizontal` CSS class, and bind it to
   * either an index (number) or an object with an `index` property or an array's length.
   * For example:
   *
   * ```js
   * {{#liquid-bind foo class="liquid-slide-horizontal" as |currentFooValue|}}
   *   <!-- content goes here that will slide -->
   *   {{currentFooValue}}
   * {{/liquid-bind}}
   * ```
   *
   * The two transitions defined below will then cause the content to slide when the bound value changes.
   * If the value increases, we slide toLeft; if the value decreases, we slide toRight.
   */
  this.transition(
    this.hasClass('liquid-slide-horizontal'),
    this.toValue(function(toValue, fromValue) {
      return _getIndex(toValue) > _getIndex(fromValue);
    }),
    this.use('toLeft')
  );
  this.transition(
    this.hasClass('liquid-slide-horizontal'),
    this.toValue(function(toValue, fromValue) {
      return _getIndex(toValue) < _getIndex(fromValue);
    }),
    this.use('toRight')
  );
}
