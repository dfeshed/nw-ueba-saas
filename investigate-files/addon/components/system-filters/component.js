import Component from 'ember-component';
import { connect } from 'ember-redux';
import {
  addSystemFilter,
  setSystemFilterFlag
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  isFilterReset: files.filter.isFilterReset,
  isSystemFilter: files.filter.isSystemFilter
});

const dispatchToActions = {
  addSystemFilter,
  setSystemFilterFlag
};

const FilterList = Component.extend({
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
      this.send('addSystemFilter', [expression]);
      this.send('setSystemFilterFlag', true);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FilterList);