/**
 * @file Breadcrumb component
 * Displays the constituent pieces of a given Netwitness Core query.
 * @public
 */
import Ember from 'ember';
import computed, { bool } from 'ember-computed-decorators';
import { uriEncodeEventQuery } from 'sa/protected/investigate/actions/helpers/query-utils';
import formatUtil from '../events-table-row/format-util';
import { metaKeyAlias } from 'sa/helpers/meta-key-alias';

const { Component } = Ember;

export default Component.extend({
  tagName: 'nav',
  classNames: 'rsa-investigate-breadcrumb',

  /**
   * An object whose properties are the filter parameters for a Netwitness Core query; including
   * `serviceId`, `startTime`, `endTime` and an optional `metaFilter`.
   * @see protected/investigate/state/query
   * @type {object}
   * @public
   */
  query: undefined,

  /**
   * Array of meta key definitions for given query.
   * Used for rendering user-friendly display names for meta keys.
   * @type {object[]}
   * @public
   */
  language: undefined,

  /**
   * Hash of meta value alias lookup tables, keyed by meta key.
   * Used for rendering user-friendly aliases for meta key values.
   * @type {object}
   * @public
   */
  aliases: undefined,

  /**
   * List of known Netwitness Core services.
   * Used for looking up the name of a service by its ID.
   * @type {object[]}
   * @public
   */
  services: undefined,

  // Compute an options hash for the utilities which will format the meta values.
  // The options hash will include any meta value aliases defined on the Core device.
  // For consistency with 10.6 UI, it will also include some i18n strings for certain event types (i.e., `medium`s).
  // These event types (namely, Network, Log & Correlation) are to be rendered as i18n strings consistently across all
  // deployments, regardless of whether or not the customer has bothered to define aliases for them. Note that there
  // may be other event types found in data; for those, the UI will still support aliases (non-i18n), if defined.
  @computed('i18n', 'aliases')
  _opts(i18n, aliases) {
    return {
      aliases,
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

  // Computes the service object in `services` that matches `query.serviceId`.
  @computed('services', 'query.serviceId')
  serviceObject: ((services = [], serviceId = '') => services.findBy('id', serviceId)),

  // Computes URI for the given query WITHOUT the query's meta filter conditions.
  @computed('query')
  baseUri(query) {
    if (!query) {
      return '';
    }
    let clone = query.clone();
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
    let allConditions = query && query.get('metaFilter.conditions');
    if (!allConditions || !allConditions.length) {
      return [];
    }

    let clone = query.clone();

    return allConditions.map(({ key, value }, index) => {
      const keyFormat = metaKeyAlias([ key, language ]);
      const valueFormat = {
        text: formatUtil.text(key, value, opts),
        tooltip: formatUtil.tooltip(key, value, opts)
      };

      // Make a query clone whose conditions include only up to this condition.
      let thisAndPreviousConditions = allConditions.slice(0, index + 1);
      clone.set('metaFilter.conditions', thisAndPreviousConditions);
      let gotoUri = uriEncodeEventQuery(clone);

      // Make a query clone whose conditions exclude just this condition.
      let allOtherConditions = [].concat(allConditions).removeAt(index);
      clone.set('metaFilter.conditions', allOtherConditions);
      let deleteUri = uriEncodeEventQuery(clone);

      return {
        keyFormat,
        valueFormat,
        gotoUri,
        deleteUri
      };
    });
  }
});
