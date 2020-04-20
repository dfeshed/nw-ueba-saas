import { computed } from '@ember/object';
import Component from '@ember/component';
import { notEmpty } from '@ember/object/computed';
import { dasherize } from '@ember/string';
import safeCallback from 'component-lib/utils/safe-callback';
import { connect } from 'ember-redux';
import { toggleEntityVisibility } from 'respond/actions/creators/incidents-creators';
import { getVisibleEntityTypes } from 'respond/selectors/incidents';

const stateToComputed = (state) => ({
  visibleEntityTypes: getVisibleEntityTypes(state)
});

const dispatchToActions = {
  toggleEntityVisibility
};

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

  hasData: notEmpty('data'),

  resolvedSelection: computed('selection', function() {
    if (!this.selection || !this.selection.ids.length) {
      return null;
    } else if (String(this.selection.type).match(/node|link/)) {
      return null;
    } else {
      return this.selection;
    }
  }),

  // Same key-value pairs as in `data`, but with strings added for UI display.
  // @private
  resolvedData: computed('data', function() {
    return (this.data || []).map(({ key, value }) => ({
      key,
      cssClass: dasherize(String(key)),
      i18nKey: `respond.entity.legend.${key}`,
      value
    }));
  }),

  actions: {
    safeCallback
  }
});

export default connect(stateToComputed, dispatchToActions)(EntitiesLegend);