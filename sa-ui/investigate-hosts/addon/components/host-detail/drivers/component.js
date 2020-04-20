import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import propertyConfig from './drivers-property-config';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './drivers-columns';

const stateToComputed = (state) => ({
  columnsConfig: getColumnsConfig(state, columnsConfig, 'DRIVER')
});

@classic
@tagName('')
class Drivers extends Component {
  propertyConfig = propertyConfig;
}

export default connect(stateToComputed)(Drivers);
