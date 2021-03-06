import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { suspiciousThreadsData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-suspicious-threads-columns';
import { toggleSelectedProcessDllRow, setDllRowSelectedId } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  toggleSelectedProcessDllRow,
  setDllRowSelectedId
};
const stateToComputed = (state) => ({
  threadList: suspiciousThreadsData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig),
  selectedIndex: state.endpoint.process.selectedDllRowIndex,
  sid: state.endpointQuery.serverId
});

@classic
@tagName('box')
@classNames('process-suspicious-threads-list')
class suspiciousThreads extends Component {
  @action
  toggleSelectedRow(item, index, e) {
    this.send('toggleSelectedProcessDllRow', item);
    this.openPropertyPanel();
    this.send('setDllRowSelectedId', index);
    e.stopPropagation();
  }
}

export default connect(stateToComputed, dispatchToActions)(suspiciousThreads);