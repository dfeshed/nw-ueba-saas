import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';
import typeActionsMap from './type-actions-map';
import { htmlSafe } from 'ember-string';
import set from 'ember-metal/set';

export default Component.extend({
  tagName: 'ul',
  layout,
  classNames: ['rsa-context-tooltip-actions'],

  /**
   * The type of entity for which actions will be rendered (e.g., 'IP').
   * @type {String}
   * @public
   */
  entityType: null,

  /**
   * List of actions to be displayed in tooltip UI. The list varies by entity type.
   *
   * @type {{name: String, icon: String}[]}
   * @private
   */
  @computed('entityType', 'actionsMap')
  actionList(type, actionsMap) {

    // Map the action type to a list of actions.
    actionsMap = actionsMap || {};
    const actions = actionsMap[type] || actionsMap.default || [];

    // HTML escape any actions that use glyphs instead of icons.
    return actions.map((action) => {
      if (action.glyph && !action.htmlSafeGlyph) {
        set(action, 'htmlSafeGlyph', htmlSafe(action.glyph));
      }
      return action;
    });
  },

  /**
   * Configurable mapping of entity type to list of actions.
   *
   * Each map key is an entity type; each map value is an array of actions for that entity type, with properties:
   * - `text`: {String} the i18n key of the display text for the action (sans prefix 'context.tooltip.actions.');
   * - `icon`: {String} name for the `rsa-icon` to be displayed for the action;
   * - `glyph`: {String} HTML entity to be displayed instead of `icon` (`glyph` has higher precedence than `icon`).
   *
   * If no map key is found for the current `entityType`, then the actions under the `default` key will be used.
   *
   * @type {Object}
   * @public
   */
  actionsMap: typeActionsMap
});