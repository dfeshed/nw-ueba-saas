import Component from 'ember-component';
import { connect } from 'ember-redux';

import layout from './template';
import {
  addSystemFilter
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  isFilterReset: files.filter.isFilterReset
});

const dispatchToActions = {
  addSystemFilter
};

const FilterList = Component.extend({
  layout,

  tagName: 'ul',

  classNames: ['filter-list'],

  activeFilter: null,

  filterList: [
    {
      favourite: 'true',
      label: 'investigateFiles.filter.windows',
      filterId: '1',
      expression: { propertyName: 'machineOsType', restrictionType: 'IN', propertyValues: [{ value: 'windows' }] }
    },
    {
      favourite: 'true',
      label: 'investigateFiles.filter.linux',
      filterId: '2',
      expression: { propertyName: 'machineOsType', restrictionType: 'IN', propertyValues: [{ value: 'linux' }] }
    },
    {
      favourite: 'true',
      label: 'investigateFiles.filter.mac',
      filterId: '3',
      expression: { propertyName: 'machineOsType', restrictionType: 'IN', propertyValues: [{ value: 'mac' }] }
    }
  ],

  actions: {
    applyFilter({ filterId, expression }) {
      this.set('activeFilter', filterId);
      this.send('addSystemFilter', expression);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FilterList);