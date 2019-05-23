import Component from '@ember/component';
import { connect } from 'ember-redux';
import propertyConfig from './library-property-config';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './libraries-columns';

const stateToComputed = (state) => ({
  columnsConfig: getColumnsConfig(state, columnsConfig, 'LIBRARY')
});


const Libraries = Component.extend({
  tagName: '',
  propertyConfig
});

export default connect(stateToComputed)(Libraries);
