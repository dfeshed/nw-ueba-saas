import Component from 'ember-component';
import observer from 'ember-metal/observer';
import { assign } from 'ember-platform';

import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import safeCallback from 'component-lib/utils/safe-callback';
import { metaValueAlias } from 'investigate-events/helpers/meta-value-alias';
import { select, event } from 'd3-selection';
import entityTypeByMeta from './entity-type-by-meta';

const stateToComputed = ({ investigate }) => ({
  aliases: investigate.dictionaries.aliases,
  language: investigate.dictionaries.language
});

const KeyValueComponent = Component.extend({
  classNames: 'rsa-investigate-meta-key-values',
  classNameBindings: ['groupKey.isOpen:is-open', 'values.status', 'values.isEmpty:is-empty'],

  /**
   * The member of `group` to which this component corresponds.
   * Has properties `name` (the meta key identifier) and `isOpen` (boolean).
   * @type {object}
   * @public
   */
  groupKey: undefined,

  /**
   * @see state/meta-key-values
   * @type {object}
   * @public
   */
  values: undefined,

  /**
   * @see state/meta-key-options
   * @type {object}
   * @public
   */
  options: undefined,

  /**
   * The current query. Used to create links for drill-downs on the meta values.
   * @see investigate-events/state/query
   * @type {object}
   * @public
   */
  query: undefined,

  // Options for meta value formatter utility, based on `aliases`.
  @computed('aliases')
  textOptions: ((aliases) => aliases ? { aliases } : {}),

  // Options for meta value tooltip formatter utility. Copy of `textOptions` but shows raw + alias values together.
  @computed('textOptions')
  tooltipOptions: ((textOptions) => assign({ appendRawValue: true }, textOptions)),

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
      case 'error':
        return 'Error';
      default:
        return 'Queued';
    }
  },

  // Responsible for triggering an update of DOM whenever the set of meta values changes.
  valuesDataDidChange: observer('values.data', function() {
    this._renderMetaValues();
  }),

  // Creates a function that will test whether a given (raw) meta value is already selected in the current query.
  // This type of test can be done with simple looping, but we need it to be performant because it will be done
  // on every single meta value we render.  So, as an optimization, we call this method to request a performant test
  // function. That dynamically generated function will avoid looping; instead it will use a closure to do a hash lookup.
  _getMetaValueTester() {
    const groupKeyName = this.get('groupKey.name');
    const conditions = this.get('query.metaFilter.conditions');
    const conditionsForThisKey = (conditions || []).filterBy('key', groupKeyName);
    const selectedValuesForThisKey = conditionsForThisKey.reduce((hash, condition) => {
      hash[condition.value] = true;
      return hash;
    }, {});
    return function(value) {
      return !!selectedValuesForThisKey[value];
    };
  },

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

    // Request a function that will test whether a given (raw) meta value is already selected in the current query.
    // We'll use this function to mark some values as selected & disable their clicks.
    const isValueSelected = this._getMetaValueTester();

    const $root = select($el[0]);

    // Remove the DOM from previous data set (if any).
    // Why? The data is ordered by backend, and the DOM must match that order. Alas, we can't re-order the DOM via CSS
    // (yet) because we are using a free-flowing "paragraph" layout (for now), so we must completely re-render the DOM.
    $root.selectAll('a').remove();

    const $enter = $root.selectAll('a')
      .data(data, (d) => d.value)
      .enter()
      .append('a')
      .attr('class', (d) => {
        // selected values get an extra "selected" CSS class name
        const selected = isValueSelected(d.value);
        return `rsa-investigate-meta-key-values__value ${selected ? 'selected' : ''}`;
      })
      .attr('title', function(d) {
        return d.tooltip;
      })
      .on('click', (d) => {
        // clicking on non-selected values will added them to the query filter (i.e., a "drill").
        // selected values aren't clickable, because they are already in the query filter.
        if (!isValueSelected(d.value)) {
          this.send('safeCallback', clickValueAction, groupKeyName, d.value);
        }
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

export default connect(stateToComputed)(KeyValueComponent);