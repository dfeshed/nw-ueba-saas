import Component from 'ember-component';
import layout from './template';
import service from 'ember-service/inject';
import computed from 'ember-computed-decorators';
import safeCallback from 'component-lib/utils/safe-callback';

export default Component.extend({
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
    const result = [];
    const { type, id } = model;
    if (type && id) {
      this.get('context').summary(
        [ model ],
        (type, id, data) => {
          result.clear().pushObjects(data);
        }
      );
    }
    return result;
  },

  actions: {
    safeCallback
  }
});