import Ember from 'ember';
import safeCallback from 'component-lib/utils/safe-callback';
import computed from 'ember-computed-decorators';
import { metaValueAlias } from 'sa/helpers/meta-value-alias';
import { select, event } from 'd3-selection';
import entityTypeByMeta from 'sa/utils/context/entity-type-by-meta';

const {
  merge,
  observer,
  Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-meta-key-values',
  classNameBindings: ['groupKey.isOpen:is-open', 'values.status', 'values.isEmpty:is-empty'],

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

  // Computes an appropriate string to display for the current status of the data fetch.
  @computed('values.{status,description}')
  resolvedDescription(status, description) {
    if (description) {
      return description;
    }
    switch (status) {
      case 'streaming':
        return 'Loading values...';
      case 'stopped':
        return 'Stopped';
      case 'complete':
        return '';
      default:
        return 'Queued';
    }
  },

  // Responsible for triggering an update of DOM whenever the set of meta values changes.
  valuesDataDidChange: observer('values.data', function() {
    this._renderMetaValues();
  }),

  // Responsible for rendering the meta values DOM. For performance, we do this here with D3 instead of in the template.
  _renderMetaValues() {
    const $el = this.element && this.$('.js-content');
    if (!$el || !$el[0]) {
      return;
    }

    // Render latest data set.
    const {
      'groupKey.name': groupKeyName,
      clickValueAction,
      contextLookupAction,
      resolvedData: data
    } = this.getProperties('groupKey.name', 'clickValueAction', 'contextLookupAction', 'resolvedData');

    const $root = select($el[0]);

    // Remove the DOM from previous data set (if any).
    // Why? The data is ordered by backend, and the DOM must match that order. Alas, we can't re-order the DOM via CSS
    // (yet) because we are using a free-flowing "paragraph" layout (for now), so we must completely re-render the DOM.
    $root.selectAll('a').remove();

    let $enter = $root.selectAll('a')
      .data(data, (d) => d.value)
      .enter()
      .append('a')
      .classed('rsa-investigate-meta-key-values__value', true)
      .attr('title', function(d) {
        return d.tooltip;
      })
      .on('click', (d) => {
        this.send('safeCallback', clickValueAction, groupKeyName, d.value);
      });

    // If the meta key corresponds to a known entity, enable context lookup on that meta value:
    // that is, wire up the `contextLookupAction` to some DOM event on the meta value DOM.
    // The UX design for the DOM interaction is still in progress; for now, wire it up to right-mouse click.
    const entityType = entityTypeByMeta(groupKeyName) || {};
    const { name: entityTypeName } = entityType;

    if (entityTypeName) {
      $enter
        .classed('is-context-lookup-enabled', true)
        .on('contextmenu', (d) => {
          this.send('safeCallback', contextLookupAction, entityTypeName, d.value);
          // prevent browser from showing native RMC menu
          event.preventDefault();
          // prevent browser from doing native text "highlighting"
          try {
            window.getSelection().removeAllRanges();
          } catch (e) {
            // browser doesn't support selection API; no harm done; swallow err
          }
        });
    }

    $enter.append('span')
      .classed('rsa-investigate-meta-key-values__value-label', true)
      .text(function(d) {
        return d.text;
      });

    $enter.append('span')
      .classed('rsa-investigate-meta-key-values__value-metric', true)
      .text(function(d) {
        return d.count;
      });
  },

  // Maps the meta values data array to an array of info used to render those values (e.g., tooltips, URLs, etc).
  @computed('values.data', 'groupKey.name', 'textOptions', 'tooltipOptions')
  resolvedData(data = [], groupKeyName, textOptions, tooltipOptions) {
    return data.map(({ count, value }) => {
      return {
        value,
        text: metaValueAlias([ groupKeyName, value, textOptions ]),
        tooltip: metaValueAlias([ groupKeyName, value, tooltipOptions ]),
        count
      };
    });
  },

  actions: {
    safeCallback
  }
});
