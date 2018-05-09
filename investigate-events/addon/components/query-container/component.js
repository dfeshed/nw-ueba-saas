import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hasRequiredValuesToQuery, guidedHasFocus, freeFormHasFocus } from 'investigate-events/reducers/investigate/query-node/selectors';
import computed from 'ember-computed-decorators';
import { setQueryView } from 'investigate-events/actions/interaction-creators';
import { run } from '@ember/runloop';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import { transformTextToFilters, filterIsPresent } from 'investigate-events/actions/utils';
import EmberObject from '@ember/object';

const addToArray = (filterObject) => {
  const { filters, meta, operator, value, complexFilter } = filterObject;
  const obj = EmberObject.create({
    meta,
    operator,
    value,
    filterIndex: filters.length,
    filter: undefined,
    editActive: meta === undefined && complexFilter === undefined,
    selected: false,
    saved: (meta || complexFilter) !== undefined,
    complexFilter
  });
  return filters.pushObject(obj);
};

const transformToString = (filters) => {
  return encodeMetaFilterConditions(filters).replace(/(&&\s*)*$/g, '').trim();
};

const stateToComputed = (state) => ({
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state),
  guidedHasFocus: guidedHasFocus(state),
  freeFormHasFocus: freeFormHasFocus(state),
  queryView: state.investigate.queryNode.queryView
});

const dispatchToActions = {
  setQueryView
};

const QueryContainer = Component.extend({
  classNames: ['rsa-investigate-query-container', 'rsa-button-group'],

  tagName: 'nav',

  classNameBindings: ['queryView'],

  // should replace the computed with a selector once filters are in state
  @computed('filters.[]')
  freeFormText(filters) {
    if (filters) {
      return transformToString(filters);
    }
  },

  actions: {
    changeView(view) {
      const filters = this.get('filters');
      // This is needed when you try and edit a filter in guided mode
      // freeFormText is not re-computed as filters ( mis-managed by query-filters ) array is not mutated, instead is being reset
      // A hack to forcefully set freeFormText in that scenario
      if (view === 'freeForm' && filters && !filterIsPresent(filters, this.get('freeFormText'))) {
        this.set('freeFormText', transformToString(filters));
      }
      this.send('setQueryView', view);
      run.next(() => {
        if (this.get('guidedHasFocus')) {
          this.$('.rsa-query-meta input').focus();
        } else if (this.get('freeFormHasFocus')) {
          this.$('.rsa-investigate-free-form-query-bar input').focus();
        }
      });
    },
    addFilters(str) {
      const filtersList = this.get('filters');
      // check if the filter is already present
      if (!filterIsPresent(filtersList, str)) {
        const filter = transformTextToFilters(str.trim());
        // In case there is a addition
        // 1. empty the array - this makes it easier for us to add, instead of finding out what we're missing and adding that specific filter
        // 2. convert freeFormText into one single pill - can be either a complex or a regular filter
        // 3. add an empty editActive filter, so that a brand new filter is ready to be created in guided mode
        filtersList.removeObjects(filtersList);
        if (filter.complexFilter) {
          addToArray({ filters: filtersList, complexFilter: filter.complexFilter });
        } else {
          addToArray({ filters: filtersList, meta: filter.meta, operator: filter.operator, value: filter.value });
        }
        addToArray({ filters: filtersList }); // will need an empty object with `editActive: true, saved: false`
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
