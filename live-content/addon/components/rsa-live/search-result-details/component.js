import Ember from 'ember';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';

const { Component } = Ember;

const stateToComputed = ({ live: { search } }) => {
  return {
    resource: search.focusResource
  };
};

/**
 * Resource Detail View
 * @public
 */
const ResourceDetails = Component.extend({
  classNames: ['rsa-live-search-result-details'],

  /**
   * Converts the resourceType name into a css class name by converting non-compatible class name characters to valid
   * characters
   * @property resourceTypeClass
   * @private
   */
  @computed('resource.resourceType.name')
  resourceTypeClass(resourceType) {
    return typeof resourceType === 'string' ? resourceType.replace(':', '-').dasherize() : '';
  }
});

export default connect(stateToComputed, undefined)(ResourceDetails);