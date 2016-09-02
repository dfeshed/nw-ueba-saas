import Ember from 'ember';
import safeCallback from 'sa/utils/safe-callback';
import computed from 'ember-computed-decorators';

const {
  merge,
  Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-meta-key-values',
  classNameBindings: ['groupKey.isOpen:is-open'],

  // @see components/rsa-investigate/meta/values-panel
  group: undefined,

  /**
   * The member of `group` to which this component corresponds.
   * Has properties `name` (the meta key identifier) and `isOpen` (boolean).
   * @type {object}
   * @public
   */
  groupKey: undefined,

  /**
   * @see protected/investigate/state/meta-key-values
   * @type {object}
   * @public
   */
  values: undefined,

  /**
   * @see protected/investigate/state/meta-key-options
   * @type {object}
   * @public
   */
  options: undefined,

  /**
   * The current query. Used to create links for drill-downs on the meta values.
   * @see investigate/state/query
   * @type {object}
   * @public
   */
  query: undefined,

  // @see components/rsa-investigate/meta/values-panel
  language: undefined,

  // @see components/rsa-investigate/meta/values-panel
  aliases: undefined,

  // Options for meta value formatter utility, based on `aliases`.
  @computed('aliases')
  textOptions: ((aliases) => aliases ? { aliases } : {}),

  // Options for meta value tooltip formatter utility. Copy of `textOptions` but shows raw + alias values together.
  @computed('textOptions')
  tooltipOptions: ((textOptions) => merge({ appendRawValue: true }, textOptions)),

  /**
   * Configurable callback to be invoked when user clicks the UI to toggle the key open/closed.
   * @type {function}
   * @public
   */
  toggleAction: undefined,

  actions: {
    safeCallback
  }
});
