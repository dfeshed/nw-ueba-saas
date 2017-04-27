import Component from 'ember-component';
import layout from './template';
import service from 'ember-service/inject';
import computed from 'ember-computed-decorators';

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
  }
});