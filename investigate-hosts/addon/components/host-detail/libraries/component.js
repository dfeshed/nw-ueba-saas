import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import propertyConfig from './library-property-config';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './libraries-columns';

const stateToComputed = (state) => ({
  columnsConfig: getColumnsConfig(state, columnsConfig, 'LIBRARY')
});


@classic
@tagName('')
class Libraries extends Component {
  propertyConfig = propertyConfig;
}

export default connect(stateToComputed)(Libraries);
