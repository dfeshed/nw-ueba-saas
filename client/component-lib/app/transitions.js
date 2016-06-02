/**
 * @file Liquid Fire Transitions Map for declarative animations.
 * Defines transitions which can then be leveraged by components/routes/etc.
 * @see http://ember-animation.github.io/liquid-fire/#/transition-map
 * @public
 */
import Ember from 'ember';

// Helper to retrieve 'index' property from a given object; otherwise, if not an object, just returns the given arg.
function _getIndex(value) {
  if (value && typeof value === 'object') {
    return Ember.get(value, 'index');
  } else {
    return value;
  }
}

export default function() {
  /*
   * For sliding DOM horizontally on-/off-screen:
   * Use a `{{liquid-bind}}` component with a `liquid-slide-horizontal` CSS class, and bind it to
   * either an index (number) or an object with an `index` property.
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
