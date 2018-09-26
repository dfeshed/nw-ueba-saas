import Component from '@ember/component';
import { connect } from 'ember-redux';
import propertyConfig from './drivers-property-config';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './drivers-columns';

const stateToComputed = (state) => ({
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const Drivers = Component.extend({
  tagName: '',

  propertyConfig
});

export default connect(stateToComputed)(Drivers);
