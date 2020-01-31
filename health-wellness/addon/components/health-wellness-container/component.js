import classic from 'ember-classic-decorator';
import layout from './template';
import Component from '@ember/component';
import { layout as templateLayout } from '@ember-decorators/component';

@classic
@templateLayout(layout)
export default class HWContainer extends Component {}


