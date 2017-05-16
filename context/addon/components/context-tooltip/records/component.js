import Component from 'ember-component';
import layout from './template';
import service from 'ember-service/inject';
import computed from 'ember-computed-decorators';
import safeCallback from 'component-lib/utils/safe-callback';
import connect from 'ember-redux/components/connect';
import { updateActiveTab } from 'context/actions/context-creators';
import { next } from 'ember-runloop';

const dispatchToActions = {
  // Sets the active tab of the context panel's state.
  updateActiveTab
};

// Maps the dataSource name from summary records to the name of a
// tab on the `context-panel` component. By default, if tab name
// is not found in this map, we assume it matches the dataSource name of record.
const recordNameToTabMap = {
  IOC: 'Endpoint',
  Modules: 'Endpoint'
};

const ContextTooltipRecords = Component.extend({
  tagName: 'section',
  classNames: ['rsa-context-tooltip-records'],
  layout,
  context: service(),

  /**
   * The entity (type & id) whose data is to be displayed.
   *
   * @type {{ type: String, id: String }}
   * @public
   */
  model: null,

  /**
   * Configurable optional action to be invoked when user clicks on a data record.
   * When invoked, the function will receive one input parameter:
   * - entity: ({type: String, id: String}} An object specifying the entity type (e.g., "IP") & identifier (e.g., "10.20.30.40").
   * @type {Function}
   * @public
   */
  clickDataAction: null,

  /**
   * An array of streaming summary-level data for the current `model`.
   * @see context/addon/services/context#summary()
   *
   * When `model` is set to a valid entity type-id pair, this component will fetch summary-level data for the
   * given entity from the `context` service.  That data array will be streamed to this component's `modelSummary`
   * property, and can then be rendered in the UI.
   *
   * @type {{key: String, value: *, lastUpdated: Number}[]}
   * @public
   */
  @computed('model')
  modelSummary(model = {}) {
    next(this, '_fetchSummary', model);
    return [];
  },

  /**
   * The status of the request for `modelSummary` data.
   * @type {String} Either 'streaming', 'error' or 'complete';
   * @public
   */
  modelStatus: null,

  /**
   * Kicks off stream request for summary data.
   * The responses are then used to update this component's `modelStatus` and `modelSummary`.
   * Before kicking off the request, `modelStatus` is reset to `null` (if model is empty) or `'streaming'`.
   * @private
   */
  _fetchSummary(model) {
    const { type, id } = model || {};
    if (!type || !id) {

      // Model is not well-defined, reset to inert state.
      this.setProperties({
        modelStatus: null,
        modelSummary: []
      });
    } else {

      // Model is well-defined, prepare to fetch data.
      this.setProperties({
        modelStatus: 'streaming',
        modelSummary: []
      });

      // Define a callback that will update our `modelStatus` string and append to our `modelSummary` array.
      const callback = (type, id, status, records) => {

        // Since this callback is invoked async and the tooltip component is short-lived,
        // check to make sure this component is still alive before manipulating it.
        if (this.get('isDestroying') || this.get('isDestroyed')) {
          return;
        }

        this.set('modelStatus', status);
        if (records) {
          this.get('modelSummary').clear().pushObjects(records);
        }
      };

      this.get('context').summary([ model ], callback);
    }

  },

  actions: {
    // Handles clicks on each data record.
    clickRecord(record) {
      safeCallback(this.get('clickDataAction'), record);
      if (record && record.name) {
        const tabName = recordNameToTabMap[record.name] || record.name;
        this.send('updateActiveTab', tabName);
      }
    }
  }
});

export default connect(undefined, dispatchToActions)(ContextTooltipRecords);
