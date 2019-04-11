// import Component from '@ember/component';
import { assign } from 'ember-platform';
import computed from 'ember-computed-decorators';
import safeCallback from 'component-lib/utils/safe-callback';
import { metaValueAlias } from 'investigate-events/helpers/meta-value-alias';
import { select } from 'd3-selection';
import entityTypeByMeta from './entity-type-by-meta';
import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';

export default RsaContextMenu.extend({
  classNames: 'rsa-investigate-meta-key-values',
  classNameBindings: ['_isOpenToggle:is-open', 'values.status', '_isEmpty:is-empty'],

  /**
   * Configurable callback to be invoked when user clicks the UI to toggle the key open/closed.
   * @type {function}
   * @public
   */
  toggleAction: undefined,

  /**
   * The member of `group` to which this component corresponds.
   * Has properties `name` (the meta key identifier) and `isOpen` (boolean).
   * @type {object}
   * @public
   */
  groupKey: undefined,

  /**
   * @see state/dictionaries-aliases
   */
  aliases: undefined,

  /**
   * @see state/meta-key-state-values
   * @type {object}
   * @public
   */
  values: undefined,

  /**
   * @see state/meta-options
   * @type {object}
   * @public
   */
  options: undefined,

  /**
   * @see state/meta-key-state-info
   * @type {object}
   * @public
   */
  info: undefined,

  /**
   * Object passed in by parent
   */
  contextDetails: undefined,

  /**
   * Options for meta value formatter utility, based on `aliases`.
   */
  @computed('aliases')
  textOptions: ((aliases) => aliases ? { aliases } : {}),

  /**
   * Options for meta value tooltip formatter utility.
   * Copy of `textOptions` but shows raw + alias values together.
   */
  @computed('textOptions')
  tooltipOptions: ((textOptions) => assign({ appendRawValue: true }, textOptions)),

  /**
   * Toggle meta groups
   */
  @computed('info')
  _isOpenToggle: ((info) => info && info.isOpen),

  /**
   * keyName and displayName object used by tool-tip
   */
  @computed('info')
  displayNames: (info) => {
    if (info) {
      const { metaName, displayName } = info;
      return {
        metaName,
        displayName: displayName || metaName,
        bothNames: displayName ? `${displayName} [${metaName}]` : metaName
      };
    }
  },

  /**
   * Does the values array contain any data?
   * Helps optimize drawing D3
   */
  @computed('values.{status}')
  _canRender(status) {
    const values = this.get('values');
    return values && values.data && values.data.length > 0 && status === 'complete';
  },

  /**
   * Here we make a decision either to hide the meta or to render D3
   * if there is data, we render, otherwise they trickle down to the
   * empty meta group that collects them
   */
  @computed('values.{status}', '_canRender')
  _isEmpty(status, canRender) {
    if (canRender) {
      this._renderMetaValues();
    }
    return status && status === 'complete' && this.get('values').data && this.get('values').data.length === 0;
  },

  /**
   * Computes an appropriate string to display for
   * the current status of the data fetch
   */
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

  /**
   * Maps the meta values data array to an array of info used to
   * render those values (e.g., tooltips, URLs, etc).
   */
  @computed('values.data', 'groupKey.name', 'textOptions', 'tooltipOptions')
  _resolvedData(data = [], groupKeyName, textOptions, tooltipOptions) {
    return data.map(({ count, value }) => {
      return {
        value,
        text: metaValueAlias([ groupKeyName, value, textOptions ]),
        tooltip: metaValueAlias([ groupKeyName, value, tooltipOptions ]),
        count
      };
    });
  },

  /**
   * Object required by contextMenu service
   */
  @computed('groupKey.name', 'info.format')
  contextSelection: (metaName, format) => {
    return {
      moduleName: 'EventAnalysisPanel',
      metaName,
      format
    };
  },

  /**
   * Trigger right click options by invoking the service
   * @public
   */
  contextMenu({ target }) {
    const targetClass = target.classList.value;
    const parentClass = target.parentElement.classList.value;
    const needsContextMenu = targetClass.includes('is-context-lookup-enabled') || parentClass.includes('is-context-lookup-enabled');
    if (needsContextMenu) {
      this._super(...arguments);
    } else {
      if (this.get('contextMenuService').deactivate) {
        this.get('contextMenuService').deactivate();
      }
    } // do not call super so that the browser right-click event is preserved
  },

  // Creates a function that will test whether a given (raw) meta value is already selected in the current query.
  // This type of test can be done with simple looping, but we need it to be performant because it will be done
  // on every single meta value we render.  So, as an optimization, we call this method to request a performant test
  // function. That dynamically generated function will avoid looping; instead it will use a closure to do a hash lookup.
  // RIP THIS COOL CODE OUT, SHOULD BE DONE IN META-CREATORS
  _getMetaValueTester() {
    const groupKeyName = this.get('groupKey.name');
    const metaFilters = this.get('query.metaFilter');
    const metaFiltersForThisKey = (metaFilters || []).filterBy('key', groupKeyName);
    const selectedValuesForThisKey = metaFiltersForThisKey.reduce((hash, filter) => {
      hash[filter.value] = true;
      return hash;
    }, {});
    return function(value) {
      return !!selectedValuesForThisKey[value];
    };
  },

  /**
   * Responsible for rendering the meta values DOM.
   * For performance, we do this here with D3 instead of in the template.
   * @private
   */
  _renderMetaValues() {
    const $el = this.element && this.$('.js-content');
    if (!$el || !$el[0]) {
      return;
    }

    // Render latest data set.
    const {
      'groupKey.name': groupKeyName,
      clickValueAction,
      _resolvedData: data
    } = this.getProperties('groupKey.name', 'clickValueAction', '_resolvedData');

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
    // that is, wire up the to some DOM event on the meta value DOM.
    // The UX design for the DOM interaction is still in progress; for now, wire it up to right-mouse click.
    const entityType = entityTypeByMeta(groupKeyName) || {};
    const { name: entityTypeName } = entityType;

    if (entityTypeName) {
      $enter
        .classed('is-context-lookup-enabled', true);
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

  actions: {
    safeCallback
  }
});
