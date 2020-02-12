import Component from '@ember/component';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import classic from 'ember-classic-decorator';
import layout from './template';
import { HW_COLUMNS } from './hw-columns';
import { connect } from 'ember-redux';

const stateToComputed = ({ hw }) => ({
  items: hw.monitors
});

@classic
@classNames('hw-table')
@templateLayout(layout)
class HWTable extends Component {
  columns = HW_COLUMNS;

}

export default connect(stateToComputed)(HWTable);