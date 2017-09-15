import Component from 'ember-component';
import run from 'ember-runloop';
import service from 'ember-service/inject';
import computed, { bool, empty } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { uriEncodeEventQuery } from 'investigate-events/actions/helpers/query-utils';
import formatUtil from 'investigate-events/components/events-table-row/format-util';
import { metaKeyAlias } from 'investigate-events/helpers/meta-key-alias';
import { selectedService } from 'investigate-events/reducers/services/selectors';
import $ from 'jquery';

const stateToComputed = (state) => ({
  serviceObject: selectedService(state),
  serviceData: state.services.data
});

const BreadCrumbComponent = Component.extend({
  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),

  tagName: 'nav',
  classNames: 'rsa-investigate-breadcrumb',
  isAddingMeta: false,
  queryString: '',

  /**
   * Hash of meta value alias lookup tables, keyed by meta key.
   * Used for rendering user-friendly aliases for meta key values.
   * @type {object}
   * @public
   */
  aliases: undefined,

  /**
   * Array of meta key definitions for given query.
   * Used for rendering user-friendly display names for meta keys.
   * @type {object[]}
   * @public
   */
  language: undefined,

  /**
   * An object whose properties are the filter parameters for a NetWitness Core query; including
   * `serviceId`, `startTime`, `endTime` and an optional `metaFilter`.
   * @see state/query
   * @type {object}
   * @public
   */
  query: undefined,

  @empty('queryString')
  isInvalidQuery: false,

  @computed('serviceData', 'query')
  servicesWithURI: (services, query) => this._addQueryUriToServices(services, query),

  // Compute an options hash for the utilities which will format the meta values.
  // The options hash will include any meta value aliases defined on the Core device.
  // For consistency with 10.6 UI, it will also include some i18n strings for certain event types (i.e., `medium`s).
  // These event types (namely, Network, Log & Correlation) are to be rendered as i18n strings consistently across all
  // deployments, regardless of whether or not the customer has bothered to define aliases for them. Note that there
  // may be other event types found in data; for those, the UI will still support aliases (non-i18n), if defined.
  @computed('i18n', 'aliases', 'dateFormat.selected.format', 'timeFormat.selected.format', 'timezone.selected.zoneId')
  _opts(i18n, aliases) {
    return {
      aliases,
      dateTimeFormat: `${this.get('dateFormat.selected.format')} ${this.get('timeFormat.selected.format')}`,
      timeZone: this.get('timezone.selected.zoneId'),
      i18n: i18n && {
        // For these values, display them as i18n strings (rather than alias strings which are not localized).
        medium: {
          '1': i18n.t('investigate.medium.network'),
          '32': i18n.t('investigate.medium.log'),
          '33': i18n.t('investigate.medium.correlation')
        }
      }
    };
  },

  // Resolves to true if the given query has some meta filter conditions.
  @bool('query.metaFilter.conditions.length')
  hasDrill: null,

  // Computes URI for the given query WITHOUT the query's meta filter conditions.
  @computed('query')
  baseUri(query) {
    if (!query) {
      return '';
    }
    const clone = query.clone();
    clone.metaFilter.conditions.clear();
    return uriEncodeEventQuery(clone);
  },

  // Maps the given query's conditions to a set of objects with information for each key, including:
  // * `keyFormat`: raw & friendly names for meta key;
  // * `valueFormat`: raw & friendly names for meta value;
  // * `gotoUri`: URI for the hyperlink to "jump to" this filter condition (i.e., truncate all subsequent conditions);
  // * `deleteUri`: URI for the hyperlink to remove this filter condition.
  @computed('query', 'language', '_opts')
  crumbs(query, language, opts) {
    const allConditions = query && query.get('metaFilter.conditions');
    if (!allConditions || !allConditions.length) {
      return [];
    }

    const clone = query.clone();

    return allConditions.map(({ queryString, isKeyValuePair = false, key, value }, index) => {
      const crumb = {
        text: queryString,
        tooltip: queryString
      };

      if (isKeyValuePair) {
        const keyFormat = metaKeyAlias([key, language]);
        const valueFormat = {
          text: formatUtil.text(key, value, opts),
          tooltip: formatUtil.tooltip(key, value, opts)
        };
        crumb.text = `${keyFormat.displayName} = ${valueFormat.text}`;
        crumb.tooltip = `${keyFormat.bothNames}: ${valueFormat.tooltip}`;
      }

      // Make a query clone whose conditions include only up to this condition.
      const thisAndPreviousConditions = allConditions.slice(0, index + 1);
      clone.set('metaFilter.conditions', thisAndPreviousConditions);
      crumb.gotoUri = uriEncodeEventQuery(clone);

      // Make a query clone whose conditions exclude just this condition.
      const allOtherConditions = [].concat(allConditions).removeAt(index);
      clone.set('metaFilter.conditions', allOtherConditions);
      crumb.deleteUri = uriEncodeEventQuery(clone);

      return crumb;
    });
  },

  _addQueryUriToServices(services, query) {
    return services.map((service) => {
      const clone = query.clone();
      clone.metaFilter.conditions.clear();
      clone.serviceId = service.id;
      service.queryURI = uriEncodeEventQuery(clone);
      return service;
    });
  },

  actions: {
    addMeta() {
      this.toggleProperty('isAddingMeta');
      run.next(this, function() {
        $('.rsa-investigate-query-input input').focus();
      });
    },

    submit(q) {
      // Call action on route
      this.sendAction('submitQuery', q);
      // Cleanup
      this.set('queryString', '');
      this.toggleProperty('isAddingMeta');
    }
  }
});

export default connect(stateToComputed)(BreadCrumbComponent);
