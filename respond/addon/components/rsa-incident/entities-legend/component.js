import Component from '@ember/component';
import computed, { notEmpty } from 'ember-computed-decorators';
import { dasherize } from '@ember/string';
import safeCallback from 'component-lib/utils/safe-callback';

const EntitiesLegend = Component.extend({
  classNames: ['rsa-incident-entities-legend'],
  classNameBindings: ['hasData:has-data:has-no-data'],
  data: null,
  selection: null,

  /**
   * Optional maximum number of nodes that can be shown. Used only to display message to user if limit is exceeded.
   * @type {Number}
   * @public
   */
  nodeLimit: null,

  /**
   * Indicates whether or not to show end-user message that some nodes are not rendered due to `nodeLimit`.
   * @type {Boolean}
   * @public
   */
  hasExceededNodeLimit: false,

  /**
   * @public
   */
  selectionCanBeRendered: true,

  /**
   * Configurable callback to be invoked when user clicks on "Show All Data" link.
   * @type {Function}
   * @public
   */
  showAllAction: null,

  @notEmpty('data')
  hasData: null,

  @computed('selection')
  resolvedSelection(selection) {
    if (!selection || !selection.ids.length) {
      return null;
    } else if (String(selection.type).match(/node|link/)) {
      return null;
    } else {
      return selection;
    }
  },

  // Same key-value pairs as in `data`, but with strings added for UI display.
  // @private
  @computed('data')
  resolvedData(data) {
    return (data || []).map(({ key, value }) => ({
      key,
      cssClass: dasherize(String(key)),
      i18nKey: `respond.entity.legend.${key}`,
      value
    }));
  },

  actions: {
    safeCallback
  }
});

export default EntitiesLegend;
