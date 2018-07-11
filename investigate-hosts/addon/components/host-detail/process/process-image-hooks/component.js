import Component from '@ember/component';
import { connect } from 'ember-redux';
import { imageHooksData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-image-hooks-columns';

const stateToComputed = (state) => ({
  hookList: imageHooksData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const imageHooksList = Component.extend({

  tagName: 'box',

  classNames: ['process-image-hooks-list']
});

export default connect(stateToComputed)(imageHooksList);
