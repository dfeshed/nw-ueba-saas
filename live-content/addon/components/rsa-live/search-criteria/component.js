import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';

const { Component, run, isArray, isPresent, isEmpty, typeOf } = Ember;

/**
 * Live Search Search Criteria component that includes all form controls for creating/tracking search request criteria
 * for the Live Search results page
 * @public
 */
export default Component.extend({
  classNames: ['rsa-live-search-criteria'],

  classNameBindings: ['isShowingAdvanced'],

  @computed('searchCriteria.textSearch')
  selectedKeywords: {
    get(textSearch) {
      return textSearch;
    },
    set(value) {
      return value;
    }
  },

  @computed('searchCriteria.resourceType', 'resourceTypes')
  selectedResourceType(resourceType, resourceTypes) {
    let selectedResourceType = resourceType;

    if (typeOf(resourceType) === 'string') {
      selectedResourceType = resourceTypes.find((item) => {
        return item.name === resourceType;
      });
    }

    return [selectedResourceType].filter(Boolean);
  },

  @alias('searchCriteria.medium') selectedMedium: null,

  @alias('searchCriteria.metaKeys') selectedMetaKeys: null,

  @alias('searchCriteria.metaValues') selectedMetaValues: null,

  @alias('searchCriteria.tags') selectedCategory: null,

  formUpdated() {
    const changed = {
      textSearch: this._retrieveValue('selectedKeywords'),
      resourceType: this._retrieveValue('selectedResourceType', 'name'),
      medium: this._retrieveValue('selectedMedium'),
      metaKeys: this._retrieveValue('selectedMetaKeys'),
      metaValues: this._retrieveValue('selectedMetaValues'),
      tags: this._retrieveValue('selectedCategory')
    };

    this.sendAction('onchange', changed);
  },

  /**
   * Utility function: assists in pulling out values from arrays of objects and setting empty arrays to null
   * @method _retrieveValue
   * @param propertyName
   * @param valueKey
   * @private
   */
  _retrieveValue(propertyName, valueKey) {
    let value = this.get(propertyName);

    // if the value arg is an array of objects, pull out just the values for each object based on the supplied key name
    if (isArray(value)) {
      value = value.filter(Boolean).map((item) => {
        return isPresent(valueKey) ? item[valueKey] : item;
      });
    }

    return isEmpty(value) ? null : value;
  },

  actions: {
    toggleAdvanced() {
      this.set('isShowingAdvanced', !this.get('isShowingAdvanced'));
      return true;
    },

    onFormChange() {
      this.formUpdated();
    },

    onChangeCategory(category) {
      if (typeOf(category) === 'string') {
        category = category.toLowerCase();
      }
      this.set('selectedCategory', [category]);
      this.formUpdated();
    },

    handleTextChange() {
      run.debounce(this, this.formUpdated, 1000);
      return false;
    },

    reset() {
      this.set('selectedKeywords', null);
      this.formUpdated();
    },

    preventDefault() {
      return false;
    },

    /**
     * General handler for ember power select dropdown selections in the search criteria component
     * @param propertyName The name of the property that needs to be set with the dropdown selections
     * @param selections The set of selections made by the user for that property/field
     * @private
     */
    handleSelectionChange(propertyName, selections) {
      this.set(propertyName, selections);
      this.formUpdated();
    }
  }
});
