/**
 * @file Liquid Fire Transitions Map for declarative animations.
 * Defines transitions which can then be leveraged by components/routes/etc.
 * @see http://ember-animation.github.io/liquid-fire/#/transition-map
 * @public
 */
import { get } from '@ember/object';

import { isArray } from '@ember/array';

const explodeUpDownDuration = 200;
const explodeFadeDuration = 275;

// Helper to retrive an appropriate "index" for a given value.  If given
// an object that has a number `index` attr, use that if defined. Otherwise,
// if it's an array, use the array's length.  Otherwise just return the given
// argument or zero.
function _getIndex(value) {
  if (value && typeof value === 'object') {
    const index = get(value, 'index');
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
  this.transition(
    this.childOf('[test-id=eventsListRow]'),
    this.use('explode', {
      pickOld: '[test-id=genericEventFooter]',
      use: ['toUp', { duration: explodeUpDownDuration }]
    }, {
      pickNew: '[test-id=genericEventFooter]',
      use: ['toDown', { duration: explodeUpDownDuration }]
    }, {
      pickOld: '[test-id=endpointEventFooter]',
      use: ['toUp', { duration: explodeUpDownDuration }]
    }, {
      pickNew: '[test-id=endpointEventFooter]',
      use: ['toDown', { duration: explodeUpDownDuration }]
    }, {
      pickOld: '[test-id=eventsListDetail]',
      use: ['fade', { duration: explodeFadeDuration }]
    }, {
      pickNew: '[test-id=eventsListDetail]',
      use: ['fade', { duration: explodeFadeDuration }]
    })
  );

  // Investigate hosts route
  this.transition(
    this.fromRoute('hosts.index'),
    this.toRoute('hosts.details'),
    this.use('toLeft'),
    this.reverse('toRight')
  );

  // Investigate files route
  this.transition(
    this.fromRoute('files.index'),
    this.toRoute('files.details'),
    this.toRoute('files.certificates'),
    this.use('toLeft'),
    this.reverse('toRight')
  );
}
