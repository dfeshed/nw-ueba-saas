import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { svgTranslation } from 'respond/helpers/svg/translation';
import linkCoords from 'respond/utils/force-layout/link-coords';
import Clickable from 'respond/mixins/dom/clickable';

export default Component.extend(Clickable, {
  tagName: 'g',
  classNames: ['rsa-force-layout-link'],
  classNameBindings: ['datum.isSelected:is-selected', 'datum.isHidden:is-hidden', 'datum.type'],
  attributeBindings: ['transform'],

  /**
   * An object with `source` & `target` & `coords` properties, as well as optional `text`, `type`, `isSelected` & `isHidden` props.
   *
   * The `datum` object is typically the same link object as in the d3 force-layout spec. As per that spec, it must
   * have `source` & `target` properties, each of which point to a node data object. But in order to be compatible with
   * this Ember component, it is expected to have additional properties, unknown to d3, which make the object
   * be KVO-friendly. @see components/force-layout/node#datum for details about the node data object.
   *
   * The `datum` object may also have an optional `text` string attr, whose value will be rendered as text in DOM.
   *
   * The `datum` object may also have optional `type`, `isSelected` & `isHidden` attrs, which are used simply for
   * CSS class name bindings.
   *
   * @type {{ source: object, target: object, coords: object, isSelected: boolean, isHidden: boolean }}
   * @public
   */
  datum: null,

  // Specifies the data to be submitted to click event handlers.
  // @see respond/mixins/dom/clickable
  @alias('datum')
  clickData: null,

  /**
   * A KVO-friendly object with the computed measurements for this link's position and rotation.
   *
   * These measurements are computed from the coordinates of the link's source & target nodes.  They must be updated
   * whenever either of those nodes moves or resizes.  In theory, the nodes should have `x` & `y` properties that
   * specify their coordinates, but those aren't KVO-compliant, so instead we listen for their KVO-compliant
   * mirror properties, `xObservable` & `yObservable`.  As a performance optimization, we assume node radius is
   * constant for the lifetime of the node.
   *
   * @type {object}
   * @public
   */
  @computed('datum.source.{xObservable,yObservable}', 'datum.target.{xObservable,yObservable}')
  coords(sourceX, sourceY, targetX, targetY) {
    const sourceR = this.get('datum.source.r');
    const targetR = this.get('datum.target.r');
    return linkCoords(
      sourceX,
      sourceY,
      sourceR,
      targetX,
      targetY,
      targetR
    );
  },

  // Computes the SVG translation which will move this DOM's origin to the center of the source node.
  @computed('coords')
  transform({ sourceX, sourceY }) {
    return svgTranslation(sourceX, sourceY);
  }
});
