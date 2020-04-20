import classic from 'ember-classic-decorator';
import { classNames, attributeBindings, tagName, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@tagName('hbox')
@classNames('top-risk-item')
@attributeBindings('testId:test-id')
export default class TopRiskItem extends Component {
  testId = 'top-risk-item';
}
