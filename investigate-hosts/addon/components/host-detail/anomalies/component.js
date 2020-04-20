import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAnomaliesTabs, selectedAnomaliesTab } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  anomaliesTabs: getAnomaliesTabs(state),
  selectedAnomaliesTab: selectedAnomaliesTab(state)
});

@classic
@tagName('box')
@classNames('host-anomalies')
class HostAnomalies extends Component {}

export default connect(stateToComputed)(HostAnomalies);
