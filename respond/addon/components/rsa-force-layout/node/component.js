import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { svgTranslation } from 'respond/helpers/svg/translation';
import Clickable from 'respond/mixins/dom/clickable';

export default Component.extend(Clickable, {
  tagName: 'g',
  classNames: ['rsa-force-layout-node'],
  classNameBindings: ['datum.isSelected:is-selected', 'datum.isHidden:is-hidden', 'datum.type'],
  attributeBindings: ['transform'],

  /**
   * An object with `x`, `y` & `r` properties, as well as optional `text`, `type`, `isSelected` & `isHidden` props.
   *
   * The `datum` object is typically the same node object as in the d3 force-layout spec. However, that node spec uses
   * `x` & `y` properties in a way that is not KVO-compliant with Ember.  In other words, the d3 force-layout algorithm
   * sets these properties (repeatedly) directly, not using Ember.set; therefore Ember will not be notified when the
   * values are updated; therefore we cannot use template bindings with `x` & `y`.
   *
   * As a workaround, we introduce 2 extra node properties, `xObservable` & `yObservable`, which mirror the values of
   * `x` & `y` respectively, but are updated in a KVO-compliant manner (using Ember.set). These properties must be present
   * in `datum` in order for this component's template bindings to render the node in its correct position.  These
   * properties' values will be written by the Ember `force-layout` component itself (in its `tick()` handler).
   *
   * Note that the Ember template does use the datum's `r` property, which again is not KVO-compliant by default in d3.
   * This implies that the node's radius is rendered only once. Thus we assume for simplicity that the radius is constant
   * for the lifetime of the `datum`.
   *
   * The `datum` object may also have an optional `text` string attr, whose value will be rendered as text in DOM.
   *
   * The `datum` object may also have optional `type`, `isSelected` & `isHidden` attrs, which are used simply for
   * CSS class name bindings.
   *
   * @type {{ xObservable: number, yObservable: number, r: number, isSelected: boolean, isHidden: boolean }}
   * @public
   */
  datum: null,

  // Specifies the data to be submitted to click event handlers.
  // @see respond/mixins/dom/clickable
  @alias('datum')
  clickData: null,

  @computed('datum.xObservable', 'datum.yObservable')
  transform(x, y) {
    return svgTranslation(x, y);
  }
});
