import Component from '@ember/component';
import { connect } from 'ember-redux';
import PROPERTY_CONFIG from 'investigate-hosts/components/host-detail/base-property-config';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './host-files-columns';

const stateToComputed = (state) => ({
  columnsConfig: getColumnsConfig(state, columnsConfig, 'FILE') // Need to pass the tabName
});


const Files = Component.extend({

  tagName: '',

  filePropertyConfig: PROPERTY_CONFIG

});

export default connect(stateToComputed)(Files);