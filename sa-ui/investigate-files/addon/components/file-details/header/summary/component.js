import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileSummary } from 'investigate-files/reducers/file-detail/selectors';

const stateToComputed = (state) => ({
  summary: fileSummary(state)
});

@classic
@tagName('hbox')
@classNames('file-summary', 'flexi-fit')
class SummaryComponent extends Component {}

export default connect(stateToComputed, undefined)(SummaryComponent);
