import classic from 'ember-classic-decorator';
import { classNames, classNameBindings, tagName, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@tagName('hbox')
@classNames('col-xs-6 col-md-7')
@classNameBindings('property-value')
export default class PropertyValue extends Component {}
